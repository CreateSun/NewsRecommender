package com.tqz.business.model.request;

public class UserRecommendationRequest {

    private String userId;

    private int sum;

    public UserRecommendationRequest(String userId, int sum) {
        this.userId = userId;
        this.sum = sum;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
