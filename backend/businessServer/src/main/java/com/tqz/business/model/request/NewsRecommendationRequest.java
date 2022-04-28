package com.tqz.business.model.request;

public class NewsRecommendationRequest {

    private String newsId;

    private int sum;

    public NewsRecommendationRequest(String newsId, int sum) {
        this.newsId = newsId;
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }
}
