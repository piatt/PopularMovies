package com.piatt.udacity.popularmovies;

import com.google.gson.JsonObject;

public class MovieDetailItem {
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";

    private int id;
    private String rating;
    private String title;
    private String releaseDate;
    private String synopsis;
    private String posterUrl;

    public MovieDetailItem(JsonObject movie) {
        id = movie.get("id").getAsInt();
        rating = movie.get("vote_average").getAsString();
        title = movie.get("title").getAsString();
        releaseDate = movie.get("release_date").getAsString();
        synopsis = movie.get("overview").getAsString();
        posterUrl = movie.get("poster_path").getAsString();
    }

    public int getId() {
        return id;
    }

    public String getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getReleaseDate() {
        return releaseDate.substring(0, releaseDate.indexOf("-"));
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getPosterUrl() {
        return POSTER_BASE_URL + posterUrl;
    }
}