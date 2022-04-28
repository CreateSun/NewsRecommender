package com.tqz.business.model.request;

public class UserActionRequset {
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public UserActionRequset(String userId, String newsId, Long timestamp) {
        this.userId = userId;
        this.newsId = newsId;
        this.timestamp = timestamp;
    }

    private String userId;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    private Long timestamp;

    public String getUserId() {
        return userId;
    }

    public String getNewsId() {
        return newsId;
    }

    private String newsId;

}
