package com.piatt.udacity.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

public class MovieDetail extends MovieListing {
    @Getter private String title;
    @Getter private String overview;
    @SerializedName("release_date") private String releaseDate;
    @SerializedName("vote_average") private float rating;
    private int runtime;

    public String getReleaseDate() {
        return releaseDate.substring(0, releaseDate.indexOf("-"));
    }

    public String getRating() {
        return rating == 10.0F ? "10/10" : String.format("%.1f/10", rating);
    }

    public String getRuntime() {
        return String.format("%d min", runtime);
    }
}