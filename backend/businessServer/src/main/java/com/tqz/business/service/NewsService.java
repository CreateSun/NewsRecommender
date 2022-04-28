package com.tqz.business.service;

import com.tqz.business.model.domain.News;
import com.tqz.business.model.recom.HotRecommendation;
import com.tqz.business.model.recom.Recommendation;
import com.tqz.business.utils.Constant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.Int;
import scala.math.Ordering;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ObjectMapper objectMapper;

    private String[] UserAction = {"view", "like", "collect"};

    private MongoCollection<Document> newsCollection;
    private MongoCollection<Document> averageNewsScoreCollection;
    private MongoCollection<Document> newsStatisticCollection;

    private MongoCollection<Document> getNewsCollection() {
        if (null == newsCollection)
            newsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_NEWS_COLLECTION);
        return newsCollection;
    }
    private MongoCollection<Document> getNewsStatisticCollection() {
        if (null == newsCollection)
            newsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_NEWS_STATISTIC_COLLECTION);
        return newsCollection;
    }
    private MongoCollection<Document> getAverageNewsScoreCollection() {
        if (null == averageNewsScoreCollection)
            averageNewsScoreCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_AVERAGE_NEWS_SCORE_COLLECTION);
        return averageNewsScoreCollection;
    }

    // 用户行为
    private Boolean userActionToNews(Int action, String newsId) {
        return true;
    }

    public List<News> getRecommendNews(List<Recommendation> recommendations) {
        List<String> ids = new ArrayList<>();
        for (Recommendation rec : recommendations) {
            ids.add(rec.getNewsId());
        }
        return getNews(ids);
    }
    public List<HotRecommendation> getHotRecommendNews(List<HotRecommendation> recommendations) {
        List<String> ids = new ArrayList<>();
        for (HotRecommendation rec : recommendations) {
            rec.setNews(findByNewsId(rec.getNewsId()));
        }
        return recommendations;
    }

    private List<News> getNews(List<String> newsIds) {
        FindIterable<Document> documents = getNewsCollection().find(Filters.in("newsId", newsIds));
        List<News> news = new ArrayList<>();
        for (Document document : documents) {
            news.add(documentToNews(document));
        }
        return news;
    }

    private News documentToNews(Document document) {
        News news = null;
        try {
            news = objectMapper.readValue(JSON.serialize(document), News.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return news;
    }

    public News findByNewsId(String newsId) {
        Document document = getNewsCollection().find(new Document("newsId", newsId)).first();
        if (document == null || document.isEmpty())
            return null;
        return documentToNews(document);
    }

    public List<News> findByNewsName(String name) {
        System.out.println("get search query: "+ name);
//        FindIterable<Document> documents = getNewsCollection().find(new Document("name", name));
        FindIterable<Document> documents = getNewsCollection().find(Filters.regex("title", name));
        List<News> news = new ArrayList<>();
        for (Document document : documents) {
            news.add(documentToNews(document));
        }
        return news;
    }
}
