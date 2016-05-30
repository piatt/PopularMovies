package com.piatt.udacity.popularmovies.model;

import lombok.Getter;

public class MovieVideo {
    private final String VIDEO_BASE_URL = "http://www.youtube.com/watch?v=";
    @Getter private String name;
    private String key;

    public String getVideoUrl() {
        return VIDEO_BASE_URL.concat(key);
    }
}