package com.tqz.business.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class Rating {

    @JsonIgnore
    private String _id;

    private String userId;

    private String newsId;

    private double score;

    private long timestamp;

    public Rating() {
    }

    public Rating(String userId, String newsId, double score) {
        this.userId = userId;
        this.newsId = newsId;
        this.score = score;
        this.timestamp = new Date().getTime();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}