package com.piatt.udacity.popularmovies.manager;

import com.piatt.udacity.popularmovies.BuildConfig;
import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.model.ApiResponse;
import com.piatt.udacity.popularmovies.model.MovieDetail;
import com.piatt.udacity.popularmovies.model.MovieListing;
import com.piatt.udacity.popularmovies.model.MovieReview;
import com.piatt.udacity.popularmovies.model.MovieVideo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class ApiManager {
    private final int CACHE_SIZE = 409600; // 40 MB
    private final String CACHE_DIR = "popular-movies";
    private final String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String API_KEY_PARAM = "api_key";
    private final String API_KEY_VALUE = BuildConfig.API_KEY;
    private CacheControl cacheControl;
    private ApiInterface apiInterface;

    /**
     * By setting up Retrofit with an OkHttpClient which is using a cache, network calls are cached after first hit.
     * This enables subsequent network calls to be served up from cache if available, even offline, as long as max age has not expired.
     * Additionally, the converter factory will automatically parse the response into the specified data model.
     */
    public ApiManager() {
        Cache cache = new Cache(new File(MoviesApplication.getApp().getCacheDir(), CACHE_DIR), CACHE_SIZE);
        cacheControl = new CacheControl.Builder().maxAge(1, TimeUnit.DAYS).build();
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cache(cache).addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
        apiInterface = retrofit.create(ApiInterface.class);
    }

    /**
     * This interceptor adds the API key to each request to properly authenticate it,
     * then adds cache control headers to manage data freshness rules.
     * It also handles retries automatically, unless custom logic is given.
     */
    private Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            HttpUrl url = chain.request().url().newBuilder().addQueryParameter(API_KEY_PARAM, API_KEY_VALUE).build();
            Request request = chain.request().newBuilder().url(url).cacheControl(cacheControl).build();
            return chain.proceed(request);
        }
    };

    public ApiInterface getEndpoints() {
        return apiInterface;
    }

    /**
     * This interface holds all of the API endpoints used in the app.
     * Invokers of these methods use a callback to handle the response asynchronously.
     */
    public interface ApiInterface {
        String API_PARAM_ID = "id";
        String API_ENDPOINT_POPULAR = "popular";
        String API_ENDPOINT_TOP_RATED = "top_rated";
        String API_ENDPOINT_DETAILS = "{" + API_PARAM_ID + "}";
        String API_ENDPOINT_VIDEOS = API_ENDPOINT_DETAILS + "/videos";
        String API_ENDPOINT_REVIEWS = API_ENDPOINT_DETAILS + "/reviews";

        @GET(API_ENDPOINT_POPULAR)
        Call<ApiResponse<MovieListing>> getPopularMovies();

        @GET(API_ENDPOINT_TOP_RATED)
        Call<ApiResponse<MovieListing>> getTopRatedMovies();

        @GET(API_ENDPOINT_DETAILS)
        Call<MovieDetail> getMovieDetails(@Path(API_PARAM_ID) int id);

        @GET(API_ENDPOINT_VIDEOS)
        Call<ApiResponse<MovieVideo>> getMovieVideos(@Path(API_PARAM_ID) int id);

        @GET(API_ENDPOINT_REVIEWS)
        Call<ApiResponse<MovieReview>> getMovieReviews(@Path(API_PARAM_ID) int id);
    }
}