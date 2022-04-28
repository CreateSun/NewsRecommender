package com.tqz.recommender

import java.util.Date
import java.text.SimpleDateFormat

import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.{MongoClient, MongoClientURI}
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable
import scala.util.Random

/**
 * News数据集
 * newsId,           新闻ID
 * category,     新闻分类
 * timeline,     发布时间
 * origin,       发布来源
 * keywords,     关键词
 * title,        标题
 * content       内容
 */
case class News(newsId: String, category: String,
                timeline: String, origin: String,
                keywords: Array[String], title: String,
                content: String,
                timestamp: Long
               )
/*
  新闻ID  浏览量、点赞量、收藏量 热度
 */
case class NewsStatistic(newsId:String, views: Int, likes: Int, collections: Int, hot: Int, timestamp: Long)

/**
 * Rating数据集
 * 4867        用户ID
 * 457976      新闻ID
 * 5.0         评分
 * 1395676800  时间戳
 */
case class Rating(userId: String, newsId: Int, score: Double, timestamp: Int)

case class Category(cate_name: String)

/**
 * MongoDB连接配置
 *
 * @param uri MongoDB的连接uri
 * @param db  要操作的db
 */
case class MongoConfig(uri: String, db: String)

object DataLoader {
  // 定义数据文件路径
  val NEWS_DATA_PATH = "D:\\Download\\ECommerceRecommendSystem-master\\backend\\recommender\\DataLoader\\src\\main\\resources\\news.csv"
  //  val NEWS_DATA_PATH = "D:\\VS Code Project\\recommendsystem\\backend\\recommender\\DataLoader\\src\\main\\resources\\newss.csv"
  val RATING_DATA_PATH = "D:\\Download\\ECommerceRecommendSystem-master\\backend\\recommender\\DataLoader\\src\\main\\resources\\ratings.csv"
  //  val RATING_DATA_PATH = "D:\\VS Code Project\\recommendsystem\\backend\\recommender\\DataLoader\\src\\main\\resources\\ratings.csv"
  // 定义mongodb中存储的表名
  val MONGODB_RATING_COLLECTION = "Rating"
  val MONGODB_NEWS_COLLECTION = "News"
  val MONGODB_CATEGORIES_COLLECTION = "Categories"
  val MONGODB_NEWS_STATISTIC_COLLECTION = "NewsStatistic";

  def main(args: Array[String]): Unit = {
    val config = Map(
      "spark.cores" -> "local[*]",
      "mongo.uri" -> "mongodb://127.0.0.1:27017/recommend_news",
      "mongo.db" -> "recommend_news"
    )
    // 配置DATALOADER的行为
    val action = Map(
      "LOAD_NEWS" -> false,
      "CREATE_CATEGORY_INDEX" -> false,
      "LOAD_CATEGORY" -> false,
      "LOAD_USER_CATE" -> false,
      "LOAD_NEWS_STATISTIC" -> true
    )
    // 创建一个spark config
    //    val masterName = config.get("spark.cores").get
    /**
     * setName指定spark的启动方式，此处为本地启动
     */
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("DataLoader")
    // 创建spark session
    val spark = SparkSession.builder().config(sparkConf).getOrCreate()
    implicit val mongoConfig: MongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))
    // 加载初始新闻数据集
    if (action("LOAD_NEWS")) loadOriginData(spark, config)
    if (action("CREATE_CATEGORY_INDEX")) createIndex()
    if (action("LOAD_CATEGORY")) loadCategory(spark)
    if (action("LOAD_NEWS_STATISTIC")) loadStatisticData(spark, config)
    /*
      //    划分评分-数据集
    val ratingRDD = spark.sparkContext.textFile(RATING_DATA_PATH)
    val ratingDF = ratingRDD.map(item => {
      val attr = item.split(",")
      Rating(attr(0).toInt, attr(1).toInt, attr(2).toDouble, attr(3).toInt)
    }).toDF()
     */
  }
  def loadStatisticData(spark: SparkSession, config: Map[String, String])(implicit mongoConfig: MongoConfig): Unit ={
    val list = mutable.Set[NewsStatistic]()
    var mongoClient = MongoClient()
    try {
      // 新建一个mongodb的连接，客户端
      mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))
      // 定义要操作的mongodb表，可以理解为 db.News
      val newsCollection = mongoClient(mongoConfig.db)(MONGODB_NEWS_COLLECTION)
      printf("总计：" + newsCollection.count().toString + "条数据")
      val mongoCursor: newsCollection.CursorType = newsCollection.find()
      var i = 0
      while (mongoCursor.hasNext) {
        val news = mongoCursor.next()
        val timestamp = news.get("timestamp").toString.toLong
        val item = NewsStatistic(news.get("newsId").toString, Random.nextInt(1200), Random.nextInt(1200), Random.nextInt(1200), 0, timestamp);
        list.add(item)
      }
    } catch {
      case e: Exception =>
        println(e.getClass.getName + ": " + e.getMessage)
    }
    finally {
      import spark.implicits._
      val RDD: RDD[NewsStatistic] = spark.sparkContext.parallelize(list.toList)
      val newRDD = RDD.toDF()
      storeDataInMongoDB(newRDD, drop = false, MONGODB_NEWS_STATISTIC_COLLECTION)
      mongoClient.close()
    }
  }

  def loadOriginData(spark: SparkSession, config: Map[String, String]): Unit = {
    import spark.implicits._
    // 加载数据----划分数据集
    val newsRDD: RDD[String] = spark.sparkContext.textFile(NEWS_DATA_PATH)
    val simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
    try {
      val newsDF = newsRDD
        .filter(action => {
          val data: Array[String] = action.split(",")
          var data_is_valid: Boolean = true
          if (data.length > 7) {
            var str = ""
            var i = 0
            for (i <- 7 until data.length) {
              data(6) = data(6).concat(data(i))
            }
            data(6) = data(6).trim
          }
          if (data.length < 7 || data(0) == "id") {
            data_is_valid = false
          }
          data_is_valid
        })
        .map(item => {
          // news数据通过^分隔，切分出来并转换为DataFrame
          val attr = item.split(",")
          // 转换成News
          // newsId, categories, timeline, origin, keywords, title, content, likes, collections
          val newsId: String = attr(0).trim
          val category: String = attr(1).trim
          val timeline: String = attr(2).trim.replace("/", "-")
          val origin: String = attr(3).trim
          val keywords: Array[String] = attr(4).trim.split("[|]")
//          val keywords: String = attr(4).trim.split("[|]")
          val title: String = attr(5).trim
          val content: String = attr(6).trim
          val timestamp:Long =  simpleDateFormat.parse(attr(2).trim.replace("/", "-")).getTime
          News(newsId, category, timeline, origin, keywords, title, content, timestamp)
        })
        .toDF()
      implicit val mongoConfig: MongoConfig = MongoConfig(config("mongo.uri"), config("mongo.db"))
      storeDataInMongoDB(newsDF, drop = true, MONGODB_NEWS_COLLECTION) //  todo 先清洗并保存数据
      spark.stop()
    } catch {
      case arrayIndexOutOfBoundsException: ArrayIndexOutOfBoundsException => arrayIndexOutOfBoundsException.printStackTrace()
    }
  }

  def loadCategory(spark: SparkSession)(implicit mongoConfig: MongoConfig): Unit = {
    val cate_set = mutable.Set[String]()
    var mongoClient = MongoClient()
    try {
      // 新建一个mongodb的连接，客户端
      mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))
      // 定义要操作的mongodb表，可以理解为 db.News
      val newsCollection = mongoClient(mongoConfig.db)(MONGODB_NEWS_COLLECTION)
      printf("总计：" + newsCollection.count().toString + "条数据")
      val mongoCursor: newsCollection.CursorType = newsCollection.find()
      while (mongoCursor.hasNext) {
        println(mongoCursor.next())
        if (!cate_set.apply(mongoCursor.next().get("category").toString))
          cate_set.add(mongoCursor.next().get("category").toString)
      }
    } catch {
      case e: Exception =>
        println(e.getClass.getName + ": " + e.getMessage)
    }
    finally {
      import spark.implicits._
      val RDD: RDD[String] = spark.sparkContext.makeRDD(cate_set.toList)
      val new1 = RDD.flatMap(line => line.split(" "))
      val newRDD = new1.map(item => {
        val attr = item
        Category(attr.toString)
      }).toDF()
      storeDataInMongoDB(newRDD, drop = false, MONGODB_CATEGORIES_COLLECTION)
      mongoClient.close()
    }
  }

  def createIndex()(implicit mongoConfig: MongoConfig): Unit = {
    var mongoClient = MongoClient()
    try {
      // 新建一个mongodb的连接，客户端
      mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))
      // 定义要操作的mongodb表，可以理解为 db.News
      val newsCollection = mongoClient(mongoConfig.db)(MONGODB_NEWS_COLLECTION)
      newsCollection.createIndex(MongoDBObject("newsId" -> 1))
      newsCollection.createIndex(MongoDBObject("category" -> 1))

    } catch {
      case e: Exception =>
        println(e.getClass.getName + ": " + e.getMessage)
    }
    finally {
      mongoClient.close()
    }
  }

  // 将数据保存到Mongodb中
  def storeDataInMongoDB(DF: DataFrame, drop: Boolean = true, COLLECTION: String )(implicit mongoConfig: MongoConfig): Unit = {
    // 新建一个mongodb的连接，客户端
    val mongoClient = MongoClient(MongoClientURI(mongoConfig.uri))
    // 定义要操作的mongodb表，可以理解为 db.News
    val newsCollection = mongoClient(mongoConfig.db)(COLLECTION)

    // 如果表已经存在，则删掉
    if(drop) newsCollection.dropCollection()

    // 将当前数据存入对应的表中
    DF.write
      .option("uri", mongoConfig.uri)
      .option("collection", COLLECTION)
      .mode("overwrite")
      .format("com.mongodb.spark.sql")
      .save()

    /*
      val ratingCollection = mongoClient(mongoConfig.db)(MONGODB_RATING_COLLECTION)
      ratingCollection.dropCollection()
      ratingDF.write
        .option("uri", mongoConfig.uri)
        .option("collection", MONGODB_RATING_COLLECTION)
        .mode("overwrite")
        .format("com.mongodb.spark.sql")
        .save()
      // 对表创建索引
      ratingCollection.createIndex(MongoDBObject("newsId" -> 1))
      ratingCollection.createIndex(MongoDBObject("userId" -> 1))
     */

    mongoClient.close()
  }
}
