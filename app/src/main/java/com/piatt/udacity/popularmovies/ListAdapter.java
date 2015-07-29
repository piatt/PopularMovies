package com.piatt.udacity.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.http.GET;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> urls = new ArrayList<>();
    private Picasso picasso;
    private RestAdapter restAdapter;
    private OkHttpClient okHttpClient;
    private ListService listService;
    private static final String ENDPOINT_BASE = "http://api.themoviedb.org/3/discover";
    private static final String SORT_POPULARITY = "popularity.desc";
    private static final String SORT_RATING = "vote_average.desc";
    private static final String API_KEY = "f0d27daafdedd38c2396470e6656e862";
    private static final String IMAGE_BASE = "http://image.tmdb.org/t/p/w185";

    public ListAdapter(Context context) {
        this.context = context;
        okHttpClient = new OkHttpClient();
//        okHttpClient.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        picasso = new Picasso.Builder(context).downloader(new OkHttpDownloader(okHttpClient)).build();
        restAdapter = new RestAdapter.Builder().setEndpoint(ENDPOINT_BASE).setClient(new OkClient(okHttpClient)).build();
        listService = restAdapter.create(ListService.class);

        JsonObject json = listService.getMoviesByPopularity();
        JsonArray movies = json.get("results").getAsJsonArray();

        for (int i = 0; i < movies.size(); i++) {
            JsonObject movie = movies.get(i).getAsJsonObject();
            urls.add(movie.get("poster_path").getAsString());
        }
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (ImageView) convertView;

        if (imageView == null) {
            imageView = new ImageView(context);
            imageView.setAdjustViewBounds(true);
        }

        picasso.with(context).load(IMAGE_BASE + getItem(position)).placeholder(R.drawable.image_placeholder).error(R.drawable.image_error).into(imageView);

        return imageView;
    }

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            return originalResponse.newBuilder()
                    .header("Cache-Control", String.format("max-age=%d, only-if-cached, max-stale=%d", 120, 0))
                    .build();
        }
    };

    private interface ListService {
        @GET("/movie?sort_by=" + SORT_POPULARITY + "&api_key=" + API_KEY)
        JsonObject getMoviesByPopularity();

        @GET("/movie?sort_by=" + SORT_RATING + "&api_key=" + API_KEY)
        JsonObject getMoviesByRating();
    }
}