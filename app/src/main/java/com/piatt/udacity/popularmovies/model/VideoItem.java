package com.piatt.udacity.popularmovies.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class VideoItem {
    private JsonElement name;
    private JsonElement videoUrl;
    private String videoBaseUrl;

    public VideoItem(JsonObject video) {
//        name = video.get(ContextManager.DETAIL_VIDEO_NAME);
//        videoUrl = video.get(ContextManager.DETAIL_VIDEO_URL);
//        videoBaseUrl = ContextManager.DETAIL_VIDEO_BASE_URL;
    }

    public String getName() {
        return name instanceof JsonNull ? "" : name.getAsString();
    }

    public String getVideoUrl() {
        return videoUrl instanceof JsonNull ? "" : videoBaseUrl.concat(videoUrl.getAsString());
    }
}