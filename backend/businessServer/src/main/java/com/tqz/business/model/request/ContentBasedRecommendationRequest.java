package com.tqz.business.model.request;

public class ContentBasedRecommendationRequest {

    private String id;

    public ContentBasedRecommendationRequest(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
