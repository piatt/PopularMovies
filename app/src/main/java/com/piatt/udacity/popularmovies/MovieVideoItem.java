package com.piatt.udacity.popularmovies;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class MovieVideoItem {
    private JsonElement name;
    private JsonElement videoUrl;
    private String videoBaseUrl;

    public MovieVideoItem(JsonObject video) {
        name = video.get(MovieListService.DETAIL_VIDEO_NAME);
        videoUrl = video.get(MovieListService.DETAIL_VIDEO_URL);
        videoBaseUrl = MovieListService.DETAIL_VIDEO_BASE_URL;
    }

    public String getName() {
        return name instanceof JsonNull ? "" : name.getAsString();
    }

    public String getVideoUrl() {
        return videoUrl instanceof JsonNull ? "" : videoBaseUrl.concat(videoUrl.getAsString());
    }
}