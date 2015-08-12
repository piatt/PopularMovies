package com.piatt.udacity.popularmovies;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.http.GET;

public class MovieListService {
    private static final String LOG_TAG = MovieListService.class.getSimpleName();

    private static Picasso picasso;
    private RestAdapter restAdapter;
    private OkHttpClient okHttpClient;
    private MovieDataService movieDataService;
    private ArrayList<MovieDetailItem> movieDetailItems = new ArrayList<>();
    private static final String MOVIE_BASE = "http://api.themoviedb.org/3/discover";
    private static final String SORT_POPULARITY = "popularity.desc";
    private static final String SORT_RATING = "vote_average.desc";
    private static final String API_KEY = "f0d27daafdedd38c2396470e6656e862";

    public MovieListService(final Context context, final MovieListAdapter adapter) {
        okHttpClient = new OkHttpClient();
        picasso = new Picasso.Builder(context).downloader(new OkHttpDownloader(okHttpClient)).build();
        restAdapter = new RestAdapter.Builder().setEndpoint(MOVIE_BASE).setClient(new OkClient(okHttpClient)).build();
        movieDataService = restAdapter.create(MovieDataService.class);

        Log.d(LOG_TAG, "Starting data download...");
        movieDataService.getMoviesByPopularity(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                JsonArray movies = jsonObject.get("results").getAsJsonArray();

                for (int i = 0; i < movies.size(); i++) {
                    movieDetailItems.add(new MovieDetailItem(movies.get(i).getAsJsonObject()));
                }

                Log.d(LOG_TAG, "Finished data download");
                adapter.setMovieDetailItems(movieDetailItems);
                adapter.notifyDataSetChanged();
                Log.d(LOG_TAG, "Notified adapter with data");

                if (((MovieListActivity) context).isDualPane()) {
                    adapter.getDefaultView();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, error.getMessage());
            }
        });
    }

    public static Picasso getPicasso() {
        return picasso;
    }

    public ArrayList<MovieDetailItem> getMovieDetailItems() {
        return movieDetailItems;
    }

    private interface MovieDataService {
        @GET("/movie?sort_by=" + SORT_POPULARITY + "&api_key=" + API_KEY)
        void getMoviesByPopularity(Callback<JsonObject> cb);

        @GET("/movie?sort_by=" + SORT_RATING + "&api_key=" + API_KEY)
        void getMoviesByRating(Callback<JsonObject> cb);
    }
}