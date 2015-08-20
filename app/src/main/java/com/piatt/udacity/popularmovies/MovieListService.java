package com.piatt.udacity.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
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
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;

public abstract class MovieListService {
    private static final String LOG_TAG = MovieListService.class.getSimpleName();

    private static Context appContext;
    private static GridView gridView;
    private static MovieDataService movieDataService;
    private static ConnectivityManager connectivityManager;
    private static SharedPreferences preferences;
    private static String API_URL;
    private static String API_BASE_URL;
    private static String API_ENDPOINT;
    private static String API_SORT_FILTER;
    private static String API_SORT_POPULARITY;
    private static String API_SORT_RATING;
    private static String API_KEY_FILTER;
    private static String API_KEY;
    private static String API_RESULTS_FILTER;
    private static String CACHE_DIR;
    private static String CACHE_HEADER;
    private static String CACHE_HEADER_ONLINE;
    private static String CACHE_HEADER_OFFLINE;
    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final long CACHE_MAX_AGE = 60 * 60 * 24; // 1 DAY
    private static final long CACHE_MAX_STALE = 60 * 60 * 24 * 7; // 1 WEEK
    private static String PREFERENCES_TAG;
    private static String PREFERENCES_SORT_FILTER;
    private static String PREFERENCES_ITEM_POSITION;
    public static String DETAIL_ID;
    public static String DETAIL_TITLE;
    public static String DETAIL_RELEASE_DATE;
    public static String DETAIL_RATING;
    public static String DETAIL_SYNOPSIS;
    public static String DETAIL_POSTER_URL;
    public static String DETAIL_POSTER_BASE_URL;

    /**
     * This interceptor checks network status and applies a different cache control header respectively.
     * This enables subsequent calls to the network for data to be served up from cache if available, even offline.
     */
    private static RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            if (isNetworkAvailable()) {
                request.addHeader(CACHE_HEADER, String.format(CACHE_HEADER_ONLINE, CACHE_MAX_AGE));
            } else {
                request.addHeader(CACHE_HEADER, String.format(CACHE_HEADER_OFFLINE, CACHE_MAX_STALE));
            }
        }
    };

    /**
     * Upon receipt of data, either from the network or cache,
     * this callback method parses and sends the movie objects to the MovieListAdapter.
     */
    private static Callback<JsonObject> getMoviesCallback = new Callback<JsonObject>() {
        @Override
        public void success(JsonObject jsonObject, Response response) {
            JsonArray movies = jsonObject.get(API_RESULTS_FILTER).getAsJsonArray();
            ArrayList<MovieDetailItem> movieDetailItems = new ArrayList<>();
            MovieListAdapter adapter = (MovieListAdapter) gridView.getAdapter();

            for (int i = 0; i < movies.size(); i++) {
                movieDetailItems.add(new MovieDetailItem(movies.get(i).getAsJsonObject()));
            }

            adapter.setMovieDetailItems(movieDetailItems);
            adapter.notifyDataSetChanged();

            if (((MovieListActivity) appContext).isDualPane()) {
                gridView.requestFocusFromTouch();
                gridView.setSelection(getCurrentMovieDetailItem());
                gridView.performItemClick(adapter.getView(getCurrentMovieDetailItem(), null, null), getCurrentMovieDetailItem(), getCurrentMovieDetailItem());
                adapter.getCurrentView(getCurrentMovieDetailItem());
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e(LOG_TAG, error.getMessage());
        }
    };

    /**
     * Called from within the getMovieData method on first launch, this method initializes resources
     * which will be available to the activity and it's fragments outside of the activity's lifecycle.
     */
    public static void init(Context context) {
        API_URL = context.getString(R.string.api_url);
        API_BASE_URL = context.getString(R.string.api_base_url);
        API_ENDPOINT = context.getString(R.string.api_endpoint);
        API_SORT_FILTER = context.getString(R.string.api_sort_filter);
        API_SORT_POPULARITY = context.getString(R.string.api_sort_popularity);
        API_SORT_RATING = context.getString(R.string.api_sort_rating);
        API_KEY_FILTER = context.getString(R.string.api_key_filter);
        API_KEY = context.getString(R.string.api_key);
        API_RESULTS_FILTER = context.getString(R.string.api_results_filter);
        CACHE_DIR = context.getString(R.string.cache_dir);
        CACHE_HEADER = context.getString(R.string.cache_header);
        CACHE_HEADER_ONLINE = context.getString(R.string.cache_header_online);
        CACHE_HEADER_OFFLINE = context.getString(R.string.cache_header_offline);
        PREFERENCES_TAG = context.getString(R.string.preferences_tag);
        PREFERENCES_SORT_FILTER = context.getString(R.string.preferences_sort_filter);
        PREFERENCES_ITEM_POSITION = context.getString(R.string.preferences_item_position);
        DETAIL_ID = context.getString(R.string.detail_id);
        DETAIL_TITLE = context.getString(R.string.detail_title);
        DETAIL_RELEASE_DATE = context.getString(R.string.detail_release_date);
        DETAIL_RATING = context.getString(R.string.detail_rating);
        DETAIL_SYNOPSIS = context.getString(R.string.detail_synopsis);
        DETAIL_POSTER_URL = context.getString(R.string.detail_poster_url);
        DETAIL_POSTER_BASE_URL = context.getString(R.string.detail_poster_base_url);

        Cache cache = new Cache(new File(context.getCacheDir(), CACHE_DIR), CACHE_SIZE);
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setCache(cache);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(API_BASE_URL)
                .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .build();

        appContext = context;
        movieDataService = restAdapter.create(MovieDataService.class);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        preferences = context.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
    }

    public static int getCurrentMovieDetailItem() {
        return preferences.getInt(PREFERENCES_ITEM_POSITION, 0);
    }

    public static void setCurrentMovieDetailItem(int itemPosition) {
        preferences.edit().putInt(PREFERENCES_ITEM_POSITION, itemPosition).commit();
    }

    public static int getCurrentSortFilter() {
        return preferences.getInt(PREFERENCES_SORT_FILTER, 0);
    }

    public static void setCurrentSortFilter(int sortFilter) {
        preferences.edit().putInt(PREFERENCES_SORT_FILTER, sortFilter).commit();
    }

    /**
     * Called by the MovieListFragment either on startup on sort filter toggle,
     * this method calls the appropriate RestAdapter method to request data from either the network or cache.
     */
    public static void getMovieData(Context context, GridView gridView) {
        if (appContext == null) {
            init(context);
        }

        MovieListService.gridView = gridView;

        if (getCurrentSortFilter() == 0) {
            movieDataService.getMovies(getMoviesUrl(API_SORT_POPULARITY), getMoviesCallback);
        } else {
            movieDataService.getMovies(getMoviesUrl(API_SORT_RATING), getMoviesCallback);
        }
    }

    private static boolean isNetworkAvailable() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private static String getMoviesUrl(String sortParam) {
        return String.format(API_URL, API_ENDPOINT, API_SORT_FILTER, sortParam, API_KEY_FILTER, API_KEY);
    }

    private interface MovieDataService {
        @GET("/{url}")
        void getMovies(@Path(value = "url", encode = false) String url, Callback<JsonObject> callback);
    }
}