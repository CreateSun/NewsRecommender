package com.tqz.business.model.recom;

public class Recommendation {

    private String newsId;

    private Double score;

    public Recommendation(String newsId, Double score) {
        this.newsId = newsId;
        this.score = score;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
    
}
