package com.piatt.udacity.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

public class MovieListing {
    private final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342";
    @Getter private int id;
    @SerializedName("poster_path") private String posterUrl;

    public String getPosterUrl() {
        return POSTER_BASE_URL.concat(posterUrl);
    }
}