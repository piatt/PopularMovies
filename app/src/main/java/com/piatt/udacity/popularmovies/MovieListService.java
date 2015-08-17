package com.piatt.udacity.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.GridView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.http.GET;

public abstract class MovieListService {
    private static final String LOG_TAG = MovieListService.class.getSimpleName();

    private static Context context;
    private static MovieDataService movieDataService;
    private static ConnectivityManager connectivityManager;
    private static MovieDetailItem currentMovieDetailItem;
    private static final String BASE_URL = "http://api.themoviedb.org/3/discover";
    private static final String SORT_POPULARITY = "popularity.desc";
    private static final String SORT_RATING = "vote_average.desc";
    private static final String API_KEY = "f0d27daafdedd38c2396470e6656e862";
    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final long CACHE_MAX_AGE = 60 * 60 * 24; // 1 DAY
    private static final long CACHE_MAX_STALE = 60 * 60 * 24 * 7; // 1 WEEK

    private static final RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            if (isNetworkAvailable()) {
                request.addHeader("Cache-Control", String.format("public, max-age=%d", CACHE_MAX_AGE));
            } else {
                request.addHeader("Cache-Control", String.format("public, only-if-cached, max-stale=%d", CACHE_MAX_STALE));
            }
        }
    };

    public static void init(Context context) {
        MovieListService.context = context;

        Cache cache = new Cache(new File(context.getCacheDir(), "http"), CACHE_SIZE);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setCache(cache);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        movieDataService = restAdapter.create(MovieDataService.class);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static void setCurrentMovieDetailItem(MovieDetailItem movieDetailItem) {
        currentMovieDetailItem = movieDetailItem;
    }

    public static void getMovieData(Context context, final GridView gridView) {
        if (MovieListService.context == null) {
            init(context);
        }

        Log.d(LOG_TAG, "Starting data download...");
        movieDataService.getMoviesByPopularity(new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, retrofit.client.Response response) {
                JsonArray movies = jsonObject.get("results").getAsJsonArray();
                ArrayList<MovieDetailItem> movieDetailItems = new ArrayList<>();
                MovieListAdapter adapter = (MovieListAdapter) gridView.getAdapter();

                for (int i = 0; i < movies.size(); i++) {
                    movieDetailItems.add(new MovieDetailItem(movies.get(i).getAsJsonObject()));
                }

                Log.d(LOG_TAG, "Finished data download");
                adapter.setMovieDetailItems(movieDetailItems);
                adapter.notifyDataSetChanged();
                Log.d(LOG_TAG, "Notified adapter with data");

                if (((MovieListActivity) MovieListService.context).isDualPane()) {
//                    gridView.requestFocusFromTouch();
//                    gridView.setSelection(1);
//                    gridView.performItemClick(adapter.getView(1, null, null), 1, 1);
                    adapter.getCurrentView(currentMovieDetailItem);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(LOG_TAG, error.getMessage());
            }
        });
    }

    private static boolean isNetworkAvailable() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private interface MovieDataService {
        @GET("/movie?sort_by=" + SORT_POPULARITY + "&api_key=" + API_KEY)
        void getMoviesByPopularity(Callback<JsonObject> cb);

        @GET("/movie?sort_by=" + SORT_RATING + "&api_key=" + API_KEY)
        void getMoviesByRating(Callback<JsonObject> cb);
    }
}