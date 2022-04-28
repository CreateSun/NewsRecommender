package com.tqz.business.model.request;

public class NewsRatingRequest {

    private String userId;

    private String newsId;

    private Double score;

    public NewsRatingRequest(String userId, String newsId, Double score) {
        this.userId = userId;
        this.newsId = newsId;
        this.score = score;
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

    public Double getScore() {
//        return Double.parseDouble(String.format("%.2f", score / 2D));
        return Double.parseDouble(String.format("%.2f", score));
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
