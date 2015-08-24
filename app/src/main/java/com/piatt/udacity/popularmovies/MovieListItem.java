package com.piatt.udacity.popularmovies;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class MovieListItem {
    private int id;
    private JsonElement posterUrl;
    private String posterBaseUrl;

    public MovieListItem(JsonObject movie) {
        id = movie.get(MovieListService.DETAIL_ID).getAsInt();
        posterUrl = movie.get(MovieListService.DETAIL_POSTER_URL);
        posterBaseUrl = MovieListService.DETAIL_POSTER_BASE_URL;
    }

    public int getId() {
        return id;
    }

    public String getPosterUrl() {
        return posterUrl instanceof JsonNull ? posterBaseUrl : posterBaseUrl.concat(posterUrl.getAsString());
    }
}