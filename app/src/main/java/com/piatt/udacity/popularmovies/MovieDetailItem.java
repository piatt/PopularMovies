package com.piatt.udacity.popularmovies;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

public class MovieDetailItem {
    private int id;
    private JsonElement title;
    private JsonElement releaseDate;
    private JsonElement rating;
    private JsonElement synopsis;
    private JsonElement posterUrl;
    private String posterBaseUrl;

    public MovieDetailItem(JsonObject movie) {
        id = movie.get(MovieListService.DETAIL_ID).getAsInt();
        title = movie.get(MovieListService.DETAIL_TITLE);
        releaseDate = movie.get(MovieListService.DETAIL_RELEASE_DATE);
        rating = movie.get(MovieListService.DETAIL_RATING);
        synopsis = movie.get(MovieListService.DETAIL_SYNOPSIS);
        posterUrl = movie.get(MovieListService.DETAIL_POSTER_URL);
        posterBaseUrl = MovieListService.DETAIL_POSTER_BASE_URL;
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
        return posterUrl instanceof JsonNull ? posterBaseUrl : posterBaseUrl.concat(posterUrl.getAsString());
    }
}