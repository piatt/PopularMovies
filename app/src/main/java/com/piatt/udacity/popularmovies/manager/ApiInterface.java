package com.piatt.udacity.popularmovies.manager;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {
    String API_PARAM_ID = "id";
    String API_ENDPOINT_POPULAR = "popular";
    String API_ENDPOINT_TOP_RATED ="top_rated";
    String API_ENDPOINT_FAVORITES = "favorites";
    String API_ENDPOINT_DETAILS = "{" + API_PARAM_ID + "}";
    String API_ENDPOINT_REVIEWS = API_ENDPOINT_DETAILS + "/reviews";
    String API_ENDPOINT_VIDEOS = API_ENDPOINT_DETAILS + "/videos";

    @GET(API_ENDPOINT_POPULAR)
    Call<JsonObject> getPopularMovies();

    @GET(API_ENDPOINT_TOP_RATED)
    Call<JsonObject> getTopRatedMovies();

    @GET(API_ENDPOINT_FAVORITES)
    Call<JsonObject> getFavoriteMovies();

    @GET(API_ENDPOINT_DETAILS)
    Call<JsonObject> getMovieDetails(@Path(API_PARAM_ID) int id);

    @GET(API_ENDPOINT_REVIEWS)
    Call<JsonObject> getMovieReviews(@Path(API_PARAM_ID) int id);

    @GET(API_ENDPOINT_VIDEOS)
    Call<JsonObject> getMovieVideos(@Path(API_PARAM_ID) int id);
}