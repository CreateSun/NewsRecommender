package com.tqz.business.utils;

public class Constant {

    /************** FOR MONGODB ****************/

    public static String MONGODB_DATABASE = "recommend_news";

    public static String MONGODB_USER_COLLECTION = "User";

    public static String MONGODB_USER_LIKES_COLLECTION = "UserLikes";

    public static String MONGODB_USER_VIEWS_COLLECTION = "UserViews";

    public static String MONGODB_USER_COLLECTS_COLLECTION = "UserCollects";

    public static String MONGODB_NEWS_COLLECTION = "News";

    public static String MONGODB_RATING_COLLECTION = "Rating";

    public static String MONGODB_NEWS_STATISTIC_COLLECTION = "NewsStatistic";

    public static String MONGODB_AVERAGE_NEWS_SCORE_COLLECTION = "AverageNews";

    public static String MONGODB_NEWS_RECS_COLLECTION = "NewsRecs";

    public static String MONGODB_RATE_MORE_NEWS_COLLECTION = "RateMoreNews";

    public static String MONGODB_ACTION_MORE_NEWS_COLLECTION = "MostActionNews";

    public static String MONGODB_HOT_NEWS_RECENTLY_COLLECTION = "HotRecentlyNews";

    public static String MONGODB_STREAM_RECS_COLLECTION = "StreamRecs";

    public static String MONGODB_USER_RECS_COLLECTION = "UserRecs";

    public static String MONGODB_ITEMCF_COLLECTION = "ItemCFNewsRecs";

    public static String MONGODB_CONTENTBASED_COLLECTION = "ContentBasedNewsRecs";

    /************** FOR NEWS RATING ******************/

    public static String NEWS_RATING_PREFIX = "NEWS_RATING_PREFIX";

    public static int REDIS_NEWS_RATING_QUEUE_SIZE = 40;
}
