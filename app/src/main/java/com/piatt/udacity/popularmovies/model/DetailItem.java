package com.piatt.udacity.popularmovies.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class DetailItem {
    private int id;
    private JsonElement title;
    private JsonElement releaseDate;
    private JsonElement runtime;
    private JsonElement rating;
    private JsonElement synopsis;
    private JsonElement posterUrl;
    private String posterBaseUrl;

    public DetailItem(JsonObject movie) {
//        id = movie.get(ContextManager.DETAIL_ID).getAsInt();
//        title = movie.get(ContextManager.DETAIL_TITLE);
//        releaseDate = movie.get(ContextManager.DETAIL_RELEASE_DATE);
//        runtime = movie.get(ContextManager.DETAIL_RUNTIME);
//        rating = movie.get(ContextManager.DETAIL_RATING);
//        synopsis = movie.get(ContextManager.DETAIL_SYNOPSIS);
//        posterUrl = movie.get(ContextManager.DETAIL_POSTER_URL);
//        posterBaseUrl = ContextManager.DETAIL_POSTER_BASE_URL;
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

    public String getRuntime() {
        return runtime instanceof JsonNull ? "" : runtime.getAsString().concat(" min");
    }

    public String getRating() {
        String newRating = rating instanceof JsonNull ? "" : rating.getAsString();
        return newRating.equals("10.0") ? "10/10" : newRating.concat("/10");
    }

    public String getSynopsis() {
        return synopsis instanceof JsonNull ? "" : synopsis.getAsString();
    }

    public String getPosterUrl() {
        return posterUrl instanceof JsonNull ? posterBaseUrl : posterBaseUrl.concat(posterUrl.getAsString());
    }
}