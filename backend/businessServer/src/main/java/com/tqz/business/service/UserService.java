package com.tqz.business.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import com.tqz.business.model.domain.News;
import com.tqz.business.model.domain.User;
import com.tqz.business.model.request.LoginUserRequest;
import com.tqz.business.model.request.RegisterUserRequest;
import com.tqz.business.model.request.UserActionRequset;
import com.tqz.business.utils.Constant;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class UserService {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NewsService newsService;

    private MongoCollection<Document> userCollection;
    private MongoCollection<Document> userLikesCollection;
    private MongoCollection<Document> userCollectsCollection;
    private MongoCollection<Document> userViewsCollection;

    private MongoCollection<Document> newsStatisticCollection;

    private MongoCollection<Document> getNewsStatisticCollection() {
        if (null == newsStatisticCollection)
            newsStatisticCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_NEWS_STATISTIC_COLLECTION);
        return newsStatisticCollection;
    }

    private MongoCollection<Document> getUserCollection() {
        if (null == userCollection)
            userCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_COLLECTION);
        return userCollection;
    }

    private MongoCollection<Document> getUserViewsCollection() {
        if (null == userViewsCollection)
            userViewsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_VIEWS_COLLECTION);
        return userViewsCollection;
    }

    private MongoCollection<Document> getUserLikesCollection() {
        if (null == userLikesCollection)
            userLikesCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_LIKES_COLLECTION);
        return userLikesCollection;
    }

    private MongoCollection<Document> getUserCollectsCollection() {
        if (null == userCollectsCollection)
            userCollectsCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_COLLECTS_COLLECTION);
        return userCollectsCollection;
    }

    public boolean registerUser(RegisterUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setAge(18);
        user.setFirst(true);
        user.setTimestamp(System.currentTimeMillis());
        user.setPrefGenres(new ArrayList<>());
        try {
            getUserCollection().insertOne(Document.parse(objectMapper.writeValueAsString(user)));
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User loginUser(LoginUserRequest request) {
        User user = findByUserName(request.getUsername());
        if (null == user) {
            return null;
        } else if (!user.passwordMatch(request.getPassword())) {
            return null;
        }
        return user;
    }

    private User documentToUser(Document document) {
        try {
            return objectMapper.readValue(JSON.serialize(document), User.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private News documentToNews(Document dom) {
        try {
            return objectMapper.readValue(JSON.serialize(dom), News.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkUserExist(String username) {
        return null != findByUserId(username);
    }

    public User findByUserId(String userId) {
        Document user = getUserCollection().find(new Document("userId", userId)).first();
        if (null == user || user.isEmpty())
            return null;
        return documentToUser(user);
    }

    private User findByUserName(String userName) {
        Document user = getUserCollection().find(new Document("username", userName)).first();
        if (null == user || user.isEmpty())
            return null;
        return documentToUser(user);
    }

    public boolean updateUser(User user) {
        getUserCollection().updateOne(Filters.eq("userId", user.getUserId()), new Document().append("$set", new Document("first", user.isFirst())));
        getUserCollection().updateOne(Filters.eq("userId", user.getUserId()), new Document().append("$set", new Document("prefGenres", user.getPrefGenres())));
        return true;
    }

    //点赞/取消
    public boolean likeNews(UserActionRequset request, Boolean isLike) {
        try {
            FindIterable<Document> isLikend = getUserLikesCollection().find(new Document("newsId",request.getNewsId()).append("userId", request.getUserId()));
            if (isLike) {
                if (!isLikend.iterator().hasNext()) {
                    // 写入用户行为日志
                    getUserLikesCollection().insertOne(Document.parse(objectMapper.writeValueAsString(request)));
                    // 新闻点赞数+1
                    getNewsStatisticCollection().findOneAndUpdate(new Document("newsId", request.getNewsId()), new Document("$inc", new Document("likes", 1)));
                }
                 else return false;
            } else {
                // 删除
                getUserLikesCollection().deleteOne(new Document("newsId",request.getNewsId()).append("userId", request.getUserId()));
                getNewsStatisticCollection().findOneAndUpdate(new Document("newsId", request.getNewsId()), new Document("$inc", new Document("likes", -1)));
            }
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<News> getLikeNewsList(String userId) {
        ArrayList<News> list = new ArrayList<>();
        for (Document item : getUserLikesCollection().find(new Document("userId", userId))) {
            list.add(newsService.findByNewsId(item.getString("newsId")));
        }
        return list;
    }

    //收藏/取消
    public boolean collectNews(UserActionRequset request, Boolean isCollect) {
        try {
            FindIterable<Document> isCollected = getUserCollectsCollection().find(new Document("newsId",request.getNewsId()).append("userId", request.getUserId()));
            if (isCollect) {
                if(!isCollected.iterator().hasNext()) {
                    // 用户收藏记录
                    getUserCollectsCollection().insertOne(Document.parse(objectMapper.writeValueAsString(request)));
                    // 新闻收藏数+1
                    getNewsStatisticCollection().findOneAndUpdate(new Document("newsId", request.getNewsId()), new Document("$inc", new Document("collects", 1)));
                }
                else return false;
            } else {
                // 删除用户点赞记录
                getUserCollectsCollection().deleteOne(new Document("newsId",request.getNewsId()).append("userId", request.getUserId()));
                // 新闻收藏数-1
                getNewsStatisticCollection().findOneAndUpdate(new Document("newsId", request.getNewsId()), new Document("$inc", new Document("collects", -1)));
            }
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<News> getCollectNewsList(String userId) {
        ArrayList<News> list = new ArrayList<>();
        for (Document item : getUserCollectsCollection().find(new Document("userId", userId))) {
            list.add(newsService.findByNewsId(item.getString("newsId")));
        }
        return list;
    }

    // 用户浏览
    public boolean viewNews(UserActionRequset request) {
        try {
            // todo 记录用户浏览历史
             getUserViewsCollection().insertOne(Document.parse(objectMapper.writeValueAsString(request)));
            // 新闻浏览量+1
            getNewsStatisticCollection().findOneAndUpdate(new Document("newsId", request.getNewsId()), new Document("$inc", new Document("views", 1)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
