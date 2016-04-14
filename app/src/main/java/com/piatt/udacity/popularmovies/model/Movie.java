package com.piatt.udacity.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class Movie {
    private final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w342";
    @Getter private int id;
    @SerializedName("poster_path") private String posterUrl;
    @Getter @Setter private boolean selected;

    public String getPosterUrl() {
        return POSTER_BASE_URL.concat(posterUrl);
    }
}