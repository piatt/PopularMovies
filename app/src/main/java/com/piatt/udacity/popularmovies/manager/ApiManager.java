package com.piatt.udacity.popularmovies.manager;

import com.google.gson.Gson;
import com.piatt.udacity.popularmovies.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {
    private final String LOG_TAG = ApiManager.class.getSimpleName();

    private final int CACHE_SIZE = 204800; // 20 MB
    private final String CACHE_DIR = "popular-movies";
    private final String API_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String API_KEY_PARAM = "api_key";
    private final String API_KEY_VALUE = BuildConfig.API_KEY;
    @Getter private Gson gson = new Gson();
    private CacheControl cacheControl;
    private ApiInterface apiInterface;
    private static ApiManager apiManager = new ApiManager();

    public static ApiManager getInstance() {
        return apiManager;
    }

    /**
     * By setting up Retrofit with an OkHttpClient which is using a cache, network calls are cached after first hit.
     * This enables subsequent network calls to be served up from cache if available, even offline, as long as max age has not expired.
     * Additionally, the converter factory will automatically parse the response into the specified data model.
     */
    public ApiManager() {
        Cache cache = new Cache(new File(ContextManager.getInstance().getContext().getCacheDir(), CACHE_DIR), CACHE_SIZE);
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
}