package com.tqz.business.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

public class News {

    @JsonIgnore
    private String _id;

    private int newsId;

    private String category;

    private String timeline;

    private String origin;

    private String content;

    private ArrayList keywords;

    private String title;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    private Long timestamp;

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public String getTimeline() {
        return timeline;
    }

    public String getOrigin() {
        return origin;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

    public String getCategories() {
        return category;
    }

    public void setCategories(String category) {
        this.category = category;
    }

    public ArrayList getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList keywords) {
        this.keywords = keywords;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}