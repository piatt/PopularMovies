package com.piatt.udacity.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;
import retrofit.http.Path;

public abstract class MovieListService {
    private static final String LOG_TAG = MovieListService.class.getSimpleName();

    private static Context appContext;
    private static ConnectivityManager connectivityManager;
    private static SharedPreferences preferences;
    public static MovieDataService movieDataService;
    private static boolean prefetched;
    private static String API_BASE_URL;
    private static String API_POPULARITY_ENDPOINT;
    private static String API_RATING_ENDPOINT;
    public static String API_FAVORITES_ENDPOINT;
    public static String API_REVIEWS_ENDPOINT;
    public static String API_VIDEOS_ENDPOINT;
    private static String API_KEY_FILTER;
    private static String API_KEY;
    public static String API_RESULTS_FILTER;
    private static String CACHE_DIR;
    private static String CACHE_HEADER;
    private static String CACHE_HEADER_ONLINE;
    private static String CACHE_HEADER_OFFLINE;
    private static final long CACHE_SIZE = 10 * 1024 * 1024; // 10 MB
    private static final long CACHE_MAX_AGE = 60 * 60 * 24; // 1 DAY
    private static final long CACHE_MAX_STALE = 60 * 60 * 24 * 7; // 1 WEEK
    private static final int REQUEST_RETRY_MAX = 3;
    private static final int REQUEST_RETRY_DELAY = 10000; // 10 SECONDS
    private static String PREFERENCES_TAG;
    private static String PREFERENCES_SORT_FILTER;
    private static String PREFERENCES_ITEM_POSITION;
    private static String PREFERENCES_FAVORITES;
    public static String ERROR_MESSAGE;
    public static String ERROR_NETWORK;
    public static String DETAIL_ID;
    public static String DETAIL_TITLE;
    public static String DETAIL_RELEASE_DATE;
    public static String DETAIL_RUNTIME;
    public static String DETAIL_RATING;
    public static String DETAIL_SYNOPSIS;
    public static String DETAIL_POSTER_URL;
    public static String DETAIL_POSTER_BASE_URL;
    public static String DETAIL_REVIEW_CONTENT;
    public static String DETAIL_REVIEW_AUTHOR;
    public static String DETAIL_REVIEW_NO_AUTHOR;
    public static String DETAIL_VIDEO_NAME;
    public static String DETAIL_VIDEO_URL;
    public static String DETAIL_VIDEO_BASE_URL;
    public static String DETAIL_SYNOPSIS_LABEL;
    public static String DETAIL_REVIEWS_LABEL;
    public static String DETAIL_VIDEOS_LABEL;
    public static String DETAIL_CONTRACT_INDICATOR;
    public static String DETAIL_EXPAND_INDICATOR;

    /**
     * This interceptor checks network status and applies a different cache control header respectively.
     * This enables subsequent calls to the network for data to be served up from cache if available, even offline.
     * Additionally, it adds the API key to each request to properly authenticate it.
     */
    private static RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            if (isNetworkAvailable()) {
                request.addHeader(CACHE_HEADER, String.format(CACHE_HEADER_ONLINE, CACHE_MAX_AGE));
            } else {
                request.addHeader(CACHE_HEADER, String.format(CACHE_HEADER_OFFLINE, CACHE_MAX_STALE));
            }
            request.addQueryParam(API_KEY_FILTER, API_KEY);
        }
    };

    /**
     * This interceptor retries requests if the response returns a particular set of status codes.
     * This is done to make sure prefetching can still be done successfully in batches while in the background,
     * making the user experience smoother as they browse quickly between detail items.
     */
    private static Interceptor responseInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);

            int retries = 0;
            while (!response.isSuccessful() && retries < REQUEST_RETRY_MAX) {
                retries++;

                Log.d(LOG_TAG, String.format("Retrying %s", request.urlString()));

                try {
                    Thread.sleep(REQUEST_RETRY_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                response = chain.proceed(request);
            }

            String location = response.cacheResponse() == null ? "NETWORK" : "CACHE";
            Log.d(LOG_TAG, String.format("Loaded %s from %s", request.urlString(), location));

            return response;
        }
    };

    /**
     * Called from the MovieListActivity on first launch, this method initializes resources
     * which will be available to the activity and it's fragments outside of the activity's lifecycle.
     */
    public static void init(Context context) {
        if (appContext == null) {
            Log.d(LOG_TAG, "Initializing resources");

            API_BASE_URL = context.getString(R.string.api_base_url);
            API_POPULARITY_ENDPOINT = context.getString(R.string.api_popularity_endpoint);
            API_RATING_ENDPOINT = context.getString(R.string.api_rating_endpoint);
            API_FAVORITES_ENDPOINT = context.getString(R.string.api_favorites_endpoint);
            API_REVIEWS_ENDPOINT = context.getString(R.string.api_reviews_endpoint);
            API_VIDEOS_ENDPOINT = context.getString(R.string.api_videos_endpoint);
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
            PREFERENCES_FAVORITES = context.getString(R.string.preferences_favorites);
            ERROR_MESSAGE = context.getString(R.string.error_message);
            ERROR_NETWORK = context.getString(R.string.error_network);
            DETAIL_ID = context.getString(R.string.detail_id);
            DETAIL_TITLE = context.getString(R.string.detail_title);
            DETAIL_RELEASE_DATE = context.getString(R.string.detail_release_date);
            DETAIL_RUNTIME = context.getString(R.string.detail_runtime);
            DETAIL_RATING = context.getString(R.string.detail_rating);
            DETAIL_SYNOPSIS = context.getString(R.string.detail_synopsis);
            DETAIL_POSTER_URL = context.getString(R.string.detail_poster_url);
            DETAIL_POSTER_BASE_URL = context.getString(R.string.detail_poster_base_url);
            DETAIL_REVIEW_CONTENT = context.getString(R.string.detail_review_content);
            DETAIL_REVIEW_AUTHOR = context.getString(R.string.detail_review_author);
            DETAIL_REVIEW_NO_AUTHOR = context.getString(R.string.detail_review_no_author);
            DETAIL_VIDEO_NAME = context.getString(R.string.detail_video_name);
            DETAIL_VIDEO_URL = context.getString(R.string.detail_video_url);
            DETAIL_VIDEO_BASE_URL = context.getString(R.string.detail_video_base_url);
            DETAIL_SYNOPSIS_LABEL = context.getString(R.string.detail_synopsis_label);
            DETAIL_REVIEWS_LABEL = context.getString(R.string.detail_reviews_label);
            DETAIL_VIDEOS_LABEL = context.getString(R.string.detail_videos_label);
            DETAIL_CONTRACT_INDICATOR = context.getString(R.string.detail_contract_indicator);
            DETAIL_EXPAND_INDICATOR = context.getString(R.string.detail_expand_indicator);

            Cache cache = new Cache(new File(context.getCacheDir(), CACHE_DIR), CACHE_SIZE);
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setCache(cache);
            okHttpClient.interceptors().add(responseInterceptor);
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(API_BASE_URL)
                    .setClient(new OkClient(okHttpClient))
                    .setRequestInterceptor(requestInterceptor)
                    .build();

            appContext = context;
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            preferences = context.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
            movieDataService = restAdapter.create(MovieDataService.class);
        }
    }

    public static int getCurrentMovieItem() {
        return preferences.getInt(PREFERENCES_ITEM_POSITION, 0);
    }

    public static void setCurrentMovieItem(int itemPosition) {
        preferences.edit().putInt(PREFERENCES_ITEM_POSITION, itemPosition).commit();
    }

    public static String getCurrentSortFilter() {
        return preferences.getString(PREFERENCES_SORT_FILTER, API_POPULARITY_ENDPOINT);
    }

    public static int getCurrentSortFilterPosition() {
        String sortFilter = getCurrentSortFilter();
        int sortFilterPosition = 0;

        if (sortFilter.equals(API_POPULARITY_ENDPOINT)) {
            sortFilterPosition = 0;
        } else if (sortFilter.equals(API_RATING_ENDPOINT)) {
            sortFilterPosition = 1;
        } else if (sortFilter.equals(API_FAVORITES_ENDPOINT)) {
            sortFilterPosition = 2;
        }

        return sortFilterPosition;
    }

    public static void setCurrentSortFilter(int sortPosition) {
        String sortFilter;

        switch (sortPosition) {
            case 0: sortFilter = API_POPULARITY_ENDPOINT; break;
            case 1: sortFilter = API_RATING_ENDPOINT; break;
            case 2: sortFilter = API_FAVORITES_ENDPOINT; break;
            default: sortFilter = API_POPULARITY_ENDPOINT; break;
        }

        preferences.edit().putString(PREFERENCES_SORT_FILTER, sortFilter).commit();
    }

    public static ArrayList<Integer> getCurrentFavorites() {
        ArrayList<Integer> favorites = new ArrayList<>();
        Set<String> storedFavorites = preferences.getStringSet(PREFERENCES_FAVORITES, new HashSet<String>());
        for (String favorite : storedFavorites) {
            favorites.add(Integer.parseInt(favorite));
        }
        return favorites;
    }

    public static void addFavorite(int favorite) {
        Set<String> storedFavorites = preferences.getStringSet(PREFERENCES_FAVORITES, new HashSet<String>());
        storedFavorites.add(String.valueOf(favorite));
        preferences.edit().putStringSet(PREFERENCES_FAVORITES, storedFavorites);
    }

    public static void removeFavorite(int favorite) {
        Set<String> storedFavorites = preferences.getStringSet(PREFERENCES_FAVORITES, new HashSet<String>());
        storedFavorites.remove(String.valueOf(favorite));
        preferences.edit().putStringSet(PREFERENCES_FAVORITES, storedFavorites);
    }

    public static void getFavoritesList() {
        Toast.makeText(appContext, "FAVORITES", Toast.LENGTH_SHORT).show();
    }

    public static void getMovieDetails(Context context, int movieId) {
        ((MovieListActivity) context).viewMovieDetails(movieId);
    }

    public static boolean isDualPane() {
        return ((MovieListActivity) appContext).findViewById(R.id.detail_fragment) != null;
    }

    public static boolean isNetworkAvailable() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isPrefetched() {
        return prefetched;
    }

    public static void setPrefetched(boolean prefetched) {
        MovieListService.prefetched = prefetched;
    }

    public interface MovieDataService {
        @GET("/{endpoint}")
        void getMovieList(@Path("endpoint") String endpoint, Callback<JsonObject> callback);

        @GET("/{id}")
        void getMovieDetails(@Path("id") int id, Callback<JsonObject> callback);

        @GET("/{id}/{endpoint}")
        void getMovieExtras(@Path("id") int id, @Path("endpoint") String endpoint, Callback<JsonObject> callback);
    }
}