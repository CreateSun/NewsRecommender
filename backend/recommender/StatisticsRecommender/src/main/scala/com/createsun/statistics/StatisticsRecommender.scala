package com.createsun.statistics

import java.text.SimpleDateFormat
import java.util.{Date, Formatter}

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.math.BigDecimal.RoundingMode

case class Rating(userId: Int, newsId: Int, score: Double, timestamp: Int)

/*
  新闻ID  浏览量、点赞量、收藏量 热度
 */
case class NewsStatistic(newsId: String, views: Int, likes: Int, collections: Int, hot: Double, timestamp: Long)

case class MongoConfig(uri: String, db: String)

object StatisticsRecommender {
  // 定义mongodb中存储的表名
  val MONGODB_RATING_COLLECTION = "Rating"

//  val RATE_MORE_NEWS = "RateMoreNews"
  val HOT_RECENTLY_NEWS = "HotRecentlyNews" // 近期热榜
//  val AVERAGE_NEWS = "AverageNews"
  val MONGODB_NEWS_STATISTIC_COLLECTION = "NewsStatistic";
//  val MONGODB_NEWS_HOT_COLLECTION = "HotNews"; // 综合天梯榜
  val MONGODB_NEWS_MOST_ACTION_COLLECTION = "MostActionNews"; // 浏览量\点赞\收藏 天梯榜

  def main(args: Array[String]): Unit = {
    val config = Map(
      "spark.cores" -> "local[1]",
      "mongo.uri" -> "mongodb://127.0.0.1:27017/recommend_news",
      "mongo.db" -> "recommend_news"
    )
    val config2 = List("local[*]")
    // 创建一个spark config
    //    val sparkConf = new SparkConf().setMaster(config("spark.cores")).setAppName("StatisticsRecommender")
    val sparkConf = new SparkConf().setMaster(config2.head).setAppName("StatisticsRecommender")
    // 创建spark session
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()

    import spark.implicits._
    implicit val mongoConfig: MongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))

    // 加载数据
    val statisticDF = spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_NEWS_STATISTIC_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[NewsStatistic]
      .toDF()

    // 创建一张叫ratings的临时表
    statisticDF.createOrReplaceTempView("ratings")
    val allDF = spark.sql("select * from ratings")
    try {
      val newDF = allDF.map(
        row => {
          var hot = 0.1
          hot = calHot(row.getAs[Int]("likes"), row.getAs[Int]("views"), row.getAs[Int]("collections"))
          println(row.getAs[String]("newsId").toString, hot)
          NewsStatistic(row.getAs[String]("newsId")
            , row.getAs[Int]("views")
            , row.getAs[Int]("likes")
            , row.getAs[Int]("collections")
            , hot, row.getAs[Long]("timestamp"))
        }
      )
        val hotDF = newDF.rdd
        .sortBy(_.hot, ascending = false)
        .toDF()  //  计算热门商品排行榜

      // 2. 近期热门新闻，把时间戳转换成yyyyMM格式进行评分个数统计，最终得到newsId

      hotDF.createOrReplaceTempView("ratingsInRec") // 近期热榜
      val rateMoreRecentlyNewsDF = spark.sql("select newsId, hot, timestamp from ratingsInRec order by hot, timestamp desc")
      // 把df保存到mongodb
      storeDFInMongoDB(rateMoreRecentlyNewsDF.limit(20), HOT_RECENTLY_NEWS)
//      storeDFInMongoDB(hotDF, MONGODB_NEWS_HOT_COLLECTION)
      // 计算新闻的各项最值
      val likeNewsDF = newDF.rdd.sortBy(_.likes, ascending = false).toDF().limit(10)
      val viewNewsDF = newDF.rdd.sortBy(_.views, ascending = false).toDF().limit(10)
      val collectNewsDF = newDF.rdd.sortBy(_.collections, ascending = false).toDF().limit(10)
      storeDFInMongoDB(likeNewsDF, MONGODB_NEWS_MOST_ACTION_COLLECTION)
      storeDFInMongoDB(viewNewsDF, MONGODB_NEWS_MOST_ACTION_COLLECTION, isOverWrite = false)
      storeDFInMongoDB(collectNewsDF, MONGODB_NEWS_MOST_ACTION_COLLECTION, isOverWrite = false)
    } catch {
      case e: Exception => println(e)
    }


    /*
    // 用spark sql去做不同的统计推荐
    // 1. 历史热门新闻，按照评分个数统计，newsId，count
    val rateMoreNewsDF = spark.sql("select newsId, count(newsId) as count from ratings group by newsId order by count desc")
    storeDFInMongoDB(rateMoreNewsDF, RATE_MORE_NEWS)

    // 2. 近期热门新闻，把时间戳转换成yyyyMM格式进行评分个数统计，最终得到newsId, count, yearmonth
    // 创建一个日期格式化工具
    val simpleDateFormat = new SimpleDateFormat("yyyyMM")
    // 注册UDF，将timestamp转化为年月格式yyyyMM
    spark.udf.register("changeDate", (x: Int) => simpleDateFormat.format(new Date(x * 1000L)).toInt)
    // 把原始rating数据转换成想要的结构newsId, score, yearmonth
    val ratingOfYearMonthDF = spark.sql("select newsId, score, changeDate(timestamp) as yearmonth from ratings")
    ratingOfYearMonthDF.createOrReplaceTempView("ratingOfMonth")
    val rateMoreRecentlyNewsDF = spark.sql("select newsId, count(newsId) as count, yearmonth from ratingOfMonth group by yearmonth, newsId order by yearmonth desc, count desc")
    // 把df保存到mongodb
    storeDFInMongoDB(rateMoreRecentlyNewsDF, RATE_MORE_RECENTLY_NEWS)

    // 3. 优质新闻统计，新闻的平均评分，newsId，avg
    val averageNewsDF = spark.sql("select newsId, avg(score) as avg from ratings group by newsId order by avg desc")
    storeDFInMongoDB(averageNewsDF, AVERAGE_NEWS)
  */
    spark.stop()
  }

  def storeDFInMongoDB(df: DataFrame, collection_name: String, isOverWrite: Boolean = true)(implicit mongoConfig: MongoConfig): Unit = {
    df.write
      .option("uri", mongoConfig.uri)
      .option("collection", collection_name)
      .mode(if (isOverWrite) "overwrite" else "append")
      .format("com.mongodb.spark.sql")
      .save()
  }

  def calHot(likes: Int, views: Int, collections: Int): Double = {
    val v:Double = views + (likes * 1.5) + (collections * 3.6)
    BigDecimal(v).setScale(3, RoundingMode.HALF_UP).toDouble
  }

}
