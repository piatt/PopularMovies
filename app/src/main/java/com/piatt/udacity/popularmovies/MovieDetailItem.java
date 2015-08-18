package com.piatt.udacity.popularmovies;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class MovieDetailItem {
    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";

    private int id;
    private JsonElement title;
    private JsonElement releaseDate;
    private JsonElement rating;
    private JsonElement synopsis;
    private JsonElement posterUrl;

    public MovieDetailItem(JsonObject movie) {
        id = movie.get("id").getAsInt();
        title = movie.get("title");
        releaseDate = movie.get("release_date");
        rating = movie.get("vote_average");
        synopsis = movie.get("overview");
        posterUrl = movie.get("poster_path");
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title instanceof JsonNull ? "" : title.getAsString();
    }

    public String getReleaseDate() {
        String newReleaseDate = releaseDate instanceof JsonNull ? "" : releaseDate.getAsString();
        return newReleaseDate.isEmpty() ? "" : newReleaseDate.substring(0, newReleaseDate.indexOf("-"));
    }

    public String getRating() {
        String newRating = rating instanceof JsonNull ? "" : rating.getAsString();
        return newRating.equals("10.0") ? "10/10" : newRating.concat("/10");
    }

    public String getSynopsis() {
        return synopsis instanceof JsonNull ? "" : synopsis.getAsString();
    }

    public String getPosterUrl() {
        return posterUrl instanceof JsonNull ? "" : POSTER_BASE_URL.concat(posterUrl.getAsString());
    }
}