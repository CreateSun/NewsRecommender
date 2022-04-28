package com.tqz.business.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tqz.business.model.recom.HotRecommendation;
import com.tqz.business.model.recom.Recommendation;
import com.tqz.business.model.request.*;
import com.tqz.business.utils.Constant;
import com.mongodb.util.JSON;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.DataInput;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecommenderService {

    @Autowired
    private MongoClient mongoClient;
    private ObjectMapper objectMapper;
    /**
     * 获取最近的热门商品
     *
     * @param request
     * @return
     */
    public List<HotRecommendation> getHotRecommendations(HotRecommendationRequest request) throws IOException {
        MongoCollection<Document> rateMoreNewsRecentlyCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_HOT_NEWS_RECENTLY_COLLECTION);
//        FindIterable<Document> documents = rateMoreNewsRecentlyCollection.find().sort(Sorts.descending("yearmonth")).limit(request.getSum());
        FindIterable<Document> documents = rateMoreNewsRecentlyCollection.find();
        List<HotRecommendation> recommendations = new ArrayList<>();
        if (documents != null) {
            for (Document document : documents) {
                HotRecommendation rec = new HotRecommendation(document.getString("newsId"), document.getDouble("hot"),document.getLong("timestamp"));
                recommendations.add(rec);
            }
        }
        return recommendations;
    }

    /**
     * 获取用户行为最多的商品
     *
     * @param request
     * @return
     */
    public List<Recommendation> getActionMoreRecommendations(RateMoreRecommendationRequest request) {
        MongoCollection<Document> actionMoreNewsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_ACTION_MORE_NEWS_COLLECTION);
        FindIterable<Document> documents = actionMoreNewsCollection.find().limit(request.getSum());
        List<Recommendation> recommendations = new ArrayList<>();
        if (documents != null) {
            for (Document document : documents) {
                recommendations.add(new Recommendation(document.getString("newsId"), 0D));
            }
        }
        return recommendations;
    }

    /**
     * 获取基于物品推荐的相似商品
     *
     * @param request 传入要检索的商品的 newsId，可以查询出 newsId 的相似商品
     * @return
     */
    public List<Recommendation> getItemCFRecommendations(ItemCFRecommendationRequest request) {
        MongoCollection<Document> itemCFNewsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_ITEMCF_COLLECTION);
        Document document = itemCFNewsCollection.find(new Document("newsId", String.valueOf(request.getId()))).first();
        List<Recommendation> recommendations = new ArrayList<>();
        ArrayList<Document> recs = document.get("recs", ArrayList.class);
        if (document != null) {
            for (Document recDoc : recs) {
                recommendations.add(new Recommendation(recDoc.getString("newsId"), recDoc.getDouble("score")));
            }
        }
        return recommendations;
    }

    /**
     * 获取基于内容推荐的相似商品
     *
     * @param request
     * @return
     */
    public List<Recommendation> getContentBasedRecommendations(ContentBasedRecommendationRequest request) {
        MongoCollection<Document> contentBasedNewsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_CONTENTBASED_COLLECTION);
        Document document = contentBasedNewsCollection.find(new Document("newsId", request.getId())).first();
        List<Recommendation> recommendations = new ArrayList<>();
        ArrayList<Document> recs = document.get("recs", ArrayList.class);
        if (document != null) {
            for (Document recDoc : recs) {
                recommendations.add(new Recommendation(recDoc.getString("newsId"), recDoc.getDouble("score")));
            }
        }
        return recommendations;
    }

    /**
     * 获取基于用户的协同过滤推荐列表
     *
     * @param request
     * @return
     */
    public List<Recommendation> getCollaborativeFilteringRecommendations(UserRecommendationRequest request) {
        MongoCollection<Document> userRecsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_RECS_COLLECTION);
        Document document = userRecsCollection.find(new Document("userId", request.getUserId())).first();
        List<Recommendation> recommendations = new ArrayList<>();
        if (document != null) {
            ArrayList<Document> recs = document.get("recs", ArrayList.class);
            for (Document recDoc : recs) {
                recommendations.add(new Recommendation(recDoc.getString("newsId"), recDoc.getDouble("score")));
            }
        }
        return recommendations;
    }

    // todo 实时推荐
    /**
     * 获取用户评分操作后的实时推荐列表
     *
     * @param request
     * @return
     */
    public List<Recommendation> getStreamRecommendations(UserRecommendationRequest request) {
        MongoCollection<Document> userRecsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_STREAM_RECS_COLLECTION);
        Document document = userRecsCollection.find(new Document("userId", request.getUserId())).first();
        List<Recommendation> recommendations = new ArrayList<>();
        if (document != null) {
            ArrayList<Document> recs = document.get("recs", ArrayList.class);
            for (Document recDoc : recs) {
                recommendations.add(new Recommendation(recDoc.getString("newsId"), recDoc.getDouble("score")));
            }
        }
        return recommendations;
    }
}