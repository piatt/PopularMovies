package com.piatt.udacity.popularmovies;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class MovieReviewItem {
    private JsonElement content;
    private JsonElement author;

    public MovieReviewItem(JsonObject review) {
        content = review.get(MovieListService.DETAIL_REVIEW_CONTENT);
        author = review.get(MovieListService.DETAIL_REVIEW_AUTHOR);
    }

    public String getContent() {
        return content instanceof JsonNull ? "" : content.getAsString();
    }

    public String getAuthor() {
        return author instanceof JsonNull ? MovieListService.DETAIL_REVIEW_NO_AUTHOR : author.getAsString();
    }
}