package com.createsun.rating

import java.util.Date

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._

case class MongoConfig(uri: String, db: String)

case class Rate(userId: String, newsId: String,score: Double, timestamp: Long)

case class NewsAction(userId: String, timestamp: Long, newsId: String)


object CalRating {
  // 定义mongodb中存储的表名
  val MONGODB_NEWS_VIEW_COLLECTION = "UserViews"
  val MONGODB_NEWS_LIKE_COLLECTION = "UserLikes"
  val MONGODB_NEWS_COLLECT_COLLECTION = "UserCollects"
  val RATING = "Rating"
  val config: Map[String, String] = Map(
    "spark.cores" -> "local[*]",
    "mongo.uri" -> "mongodb://127.0.0.1:27017/recommend_news",
    "mongo.db" -> "recommend_news"
  )
  // 创建一个spark config
  val sparkConf: SparkConf = new SparkConf().setMaster("local[*]").setAppName("CalRating")
  //    .set("spark.sql.crossJoin.enabled", "true")
  // 创建spark session
  val spark: SparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

  def main(args: Array[String]): Unit = {

    import spark.implicits._
    implicit val mongoConfig: MongoConfig = MongoConfig("mongodb://127.0.0.1:27017/recommend_news", config("mongo.db"))

    // 载入新闻数据，做预处理
    var newsViewDF = spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_NEWS_VIEW_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .as[NewsAction]
      .toDF()
      .cache() // 使用 cache 优化性能
    // view表，分组后按照时间戳排序
    newsViewDF
      .withColumn("name", concat_ws("-", Array("userId", "newsId").map(key => col(key)): _*))
      .orderBy("name")
      .select("name", "timestamp")
      .createTempView("view")

    val viewDF = spark.sql("select * from view")
    val viewFunc = Window.partitionBy($"name").orderBy($"timestamp")
    val viewReverseFunc = Window.partitionBy($"name").orderBy($"timestamp".desc)
    val min = viewDF.withColumn("rn", row_number.over(viewFunc)).where($"rn" === 1).drop("rn").withColumnRenamed("timestamp", "viewMin")
    val max = viewDF.withColumn("rn", row_number.over(viewReverseFunc)).where($"rn" === 1).drop("rn").withColumnRenamed("timestamp", "viewMax")
    min.join(max, Seq("name"), "left")
      .withColumn("diff", max.col("viewMax") - min.col("viewMin"))
      .join(viewDF.groupBy("name").count(), Seq("name"), "left")
      .createOrReplaceTempView("view")

    spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_NEWS_LIKE_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .withColumn("name", concat_ws("-", Array("userId", "newsId").map(key => col(key)): _*))
      .orderBy("name")
      .select("name", "timestamp").createTempView("like") // UserLike表

    spark.read
      .option("uri", mongoConfig.uri)
      .option("collection", MONGODB_NEWS_COLLECT_COLLECTION)
      .format("com.mongodb.spark.sql")
      .load()
      .withColumn("name", concat_ws("-", Array("userId", "newsId").map(key => col(key)): _*))
      .orderBy("name")
      .select("name", "timestamp")
      .createTempView("collect") // UserCollect表

    newsViewDF = spark.sql("select * from view")
      .join(spark.sql("select name, timestamp as like from like"), Seq("name"), "left")
      .join(spark.sql("select name, timestamp as collect from collect"), Seq("name"), "left")

    newsViewDF.show()
    val newsRatingDF = newsViewDF.map {
      row => {
        val arr: Array[String] = row.getAs[String]("name").split("-")
        val userId = arr(0)
        val newsId = arr(1)
        val viewTime = row.getLong(1)
        val diff = row.getLong(3)
        val count = row.getLong(4)
        val like = if (row.get(5)!=null) row.getLong(5) else 0
        val collect = if (row.get(6)!=null) row.getLong(6) else 0
        val rate = calRate(viewTime, diff, count, like, collect).formatted("%.4f").toDouble
        Rate(userId, newsId, rate, new Date().getTime)
      }
    }.toDF()

    newsRatingDF.write
      .option("uri", mongoConfig.uri)
      .option("collection", RATING)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()
    spark.stop()
  }

  /*
    多维度计算用户新闻评分
    浏览次数，浏览时间间隔
    点赞，点赞时间与浏览时间差值的最小值
    收藏，收藏时间与浏览时间差值的最小值
   */
  def calRate(viewTime: Long, diff: Long, count: Long, like: Long, collect: Long): Double = {
    var viewRate: Double = 10.0
    var likeRate: Double = 0.0
    var collectRate: Double = 0.0

    val viewLastLikeTime: Long = viewTime + diff / 2
    val viewLastCollectTime: Long = viewTime + diff / 2

    if (count > 1) {
      /* viewRate
        浏览次数大于1， 计算所有浏览时间间隔时长的平均毫秒数，将其换算为秒数：reduceValue
       */
      val reduceValue = diff / 1000 / count
      viewRate = viewRate + cal(reduceValue)
    }
    if (like!=0) {
      likeRate = cal(Math.abs(like - viewLastLikeTime).toInt / 1000.0, 1.1)
    }
    if (collect!=0) {
      collectRate = cal(Math.abs(collect - viewLastCollectTime).toInt / 1000.0, 1.2)
    }
    println("\nRating ========> " + viewRate + "  ===  " + likeRate + "  ===  " + collectRate)
    viewRate + likeRate + collectRate
  }

  // 计算指定底数的对数值
  def log(value: Double, base: Double): Double = {
    Math.log(value) / Math.log(base)
  }

  def cal(value: Double, rate: Double = 1.0): Double = {
    log(value + 16, 2) * rate
  }

}
