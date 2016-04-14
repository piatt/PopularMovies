package com.piatt.udacity.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.adapter.MovieListAdapter.MovieViewHolder;
import com.piatt.udacity.popularmovies.event.EventBusUnregisterEvent;
import com.piatt.udacity.popularmovies.event.MoviesUpdateEvent;
import com.piatt.udacity.popularmovies.manager.ApiManager;
import com.piatt.udacity.popularmovies.manager.ContextManager;
import com.piatt.udacity.popularmovies.manager.NetworkManager;
import com.piatt.udacity.popularmovies.model.Movie;
import com.piatt.udacity.popularmovies.util.Constants;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {
    private static final String LOG_TAG = MovieListAdapter.class.getSimpleName();

    private ArrayList<Movie> movies = new ArrayList<>();

    public MovieListAdapter() {
        EventBus.getDefault().register(this);
    }

    public void onEvent(EventBusUnregisterEvent event) {
        EventBus.getDefault().unregister(this);
    }

    /**
     * This event handler is triggered by a change to the movie list sort spinner
     * and fetches the appropriate data for the sort type.
     */
    public void onEvent(MoviesUpdateEvent event) {
        switch (event.getSortPosition()) {
            case 0: ApiManager.getInstance().getEndpoints().getPopularMovies().enqueue(moviesCallback);
                break;
            case 1: ApiManager.getInstance().getEndpoints().getTopRatedMovies().enqueue(moviesCallback);
                break;
            case 2: ApiManager.getInstance().getEndpoints().getFavoriteMovies().enqueue(moviesCallback);
                break;
        }
    }

    /**
     * Upon receipt of data, either from the network or cache,
     * this callback method populates the MovieListAdapter with data.
     * Additionally, prefetching of MovieDetailItems for each Movie object is done in the background
     * to support performance and offline access.
     */
    private Callback<JsonObject> moviesCallback = new Callback<JsonObject>() {
        @Override
        public void onResponse(Response<JsonObject> response) {
            if (response.isSuccessful()) {
                movies.clear();
                movies = ApiManager.getInstance().getGson().fromJson(response.body().get(Constants.API_RESULTS_FILTER), new TypeToken<ArrayList<Movie>>(){}.getType());
                fetchMovieDetails();
                notifyDataSetChanged();
            } else {
                Log.e(LOG_TAG, response.message());
                String message = NetworkManager.getInstance().isNetworkAvailable() ? Constants.ERROR_MESSAGE : Constants.ERROR_NETWORK;
                Toast.makeText(ContextManager.getInstance().getContext(), message, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Log.e(LOG_TAG, t.getMessage());
            String message = NetworkManager.getInstance().isNetworkAvailable() ? Constants.ERROR_MESSAGE : Constants.ERROR_NETWORK;
            Toast.makeText(ContextManager.getInstance().getContext(), message, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * No action is taken in this callback,
     * since the same calls will be made later by movie detail views when the data needs to be displayed.
     */
    private Callback<JsonObject> prefetchCallback = new Callback<JsonObject>() {
        @Override
        public void onResponse(Response<JsonObject> response) {}

        @Override
        public void onFailure(Throwable t) {}
    };

    private void fetchMovieDetails() {
        for (Movie movie : movies) {
            Picasso.with(ContextManager.getInstance().getContext()).load(movie.getPosterUrl()).fetch();
            ApiManager.getInstance().getEndpoints().getMovieDetails(movie.getId()).enqueue(prefetchCallback);
            ApiManager.getInstance().getEndpoints().getMovieReviews(movie.getId()).enqueue(prefetchCallback);
            ApiManager.getInstance().getEndpoints().getMovieVideos(movie.getId()).enqueue(prefetchCallback);
        }
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Picasso.with(holder.itemView.getContext()).load(movies.get(position).getPosterUrl()).into(holder.posterView);
        holder.indicatorView.setVisibility(movies.get(position).isSelected() ? View.VISIBLE : View.INVISIBLE);
        holder.movieId = movies.get(position).getId();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.poster_view) ImageView posterView;
        @Bind(R.id.indicator_view) View indicatorView;
        private int movieId;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.poster_view)
        public void onClick() {
            ContextManager.getInstance().startFragment(Constants.FRAGMENT_TAG_DETAIL, movieId);
        }
    }
}