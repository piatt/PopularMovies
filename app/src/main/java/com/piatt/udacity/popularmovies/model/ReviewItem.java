package com.piatt.udacity.popularmovies.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class ReviewItem {
    private JsonElement content;
    private JsonElement author;

    public ReviewItem(JsonObject review) {
//        content = review.get(ContextManager.DETAIL_REVIEW_CONTENT);
//        author = review.get(ContextManager.DETAIL_REVIEW_AUTHOR);
    }

    public String getContent() {
        return content instanceof JsonNull ? "" : content.getAsString();
    }

    public String getAuthor() {
//        return author instanceof JsonNull ? ContextManager.DETAIL_REVIEW_NO_AUTHOR : author.getAsString();
        return null;
    }
}