package com.tqz.business.model.recom;

import com.tqz.business.model.domain.News;

public class HotRecommendation {

    private String newsId;

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setHot(Double hot) {
        this.hot = hot;
    }

    private Long timestamp;

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    private News news;

    public Long getTimestamp() {
        return timestamp;
    }

    public Double getHot() {
        return hot;
    }

    private Double hot;

    public HotRecommendation(String newsId, Double hot, Long timestamp) {
        this.newsId = newsId;
        this.hot = hot;
        this.timestamp = timestamp;
        this.news = null;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

}
