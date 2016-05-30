package com.piatt.udacity.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.annimon.stream.Stream;
import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.event.EventBusUnregisterEvent;
import com.piatt.udacity.popularmovies.event.MovieSelectionEvent;
import com.piatt.udacity.popularmovies.event.MoviesUpdateEvent;
import com.piatt.udacity.popularmovies.model.ApiResponse;
import com.piatt.udacity.popularmovies.model.MovieDetail;
import com.piatt.udacity.popularmovies.model.MovieFilter;
import com.piatt.udacity.popularmovies.model.MovieListing;
import com.piatt.udacity.popularmovies.model.MovieReview;
import com.piatt.udacity.popularmovies.model.MovieVideo;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListingsAdapter extends RecyclerView.Adapter<MovieListingsAdapter.MovieListingViewHolder> {
    private int selectedPosition;
    private List<MovieListing> movieListings = new ArrayList<>();

    public MovieListingsAdapter() {
        EventBus.getDefault().register(this);
        EventBus.getDefault().post(new MoviesUpdateEvent(MovieFilter.POPULAR));
    }

    @Subscribe
    public void unregisterEventBus(EventBusUnregisterEvent event) {
        EventBus.getDefault().unregister(this);
    }

    /**
     * This event handler is triggered by a change to the movie list sort spinner
     * and fetches the appropriate data for the sort type.
     */
    @Subscribe
    public void updateMovieSort(MoviesUpdateEvent event) {
        switch (event.getMovieFilter()) {
            case POPULAR: MoviesApplication.getApp().getApiManager().getEndpoints().getPopularMovies().enqueue(movieListingCallback);
                break;
            case TOP_RATED: MoviesApplication.getApp().getApiManager().getEndpoints().getTopRatedMovies().enqueue(movieListingCallback);
                break;
            case FAVORITES: MoviesApplication.getApp().getApiManager().getEndpoints().getFavoriteMovies().enqueue(movieListingCallback);
                break;
        }
    }

    /**
     * Upon receipt of data, either from the network or cache,
     * this callback method populates the MovieListingsAdapter with data.
     * Additionally, prefetching of MovieDetailItems for each MovieListing object is done in the background
     * to support performance and offline access.
     */
    private Callback<ApiResponse<MovieListing>> movieListingCallback = new Callback<ApiResponse<MovieListing>>() {
        @Override
        public void onResponse(Call<ApiResponse<MovieListing>> call, Response<ApiResponse<MovieListing>> response) {
            if (response.isSuccessful()) {
                movieListings.clear();
                movieListings = response.body().getResults();
                fetchMovieDetails();
                notifyDataSetChanged();
                if (MoviesApplication.getApp().isLargeLayout()) {
                    selectMovie(0);
                }
            } else {
                MoviesApplication.getApp().showErrorMessage(response.message());
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<MovieListing>> call, Throwable t) {
            MoviesApplication.getApp().showErrorMessage(t.getMessage());
        }
    };

    /**
     * No action is taken in this callback,
     * since the same calls will be made later by movie detail views when the data needs to be displayed.
     */
    private Callback<MovieDetail> movieDetailCallback = new Callback<MovieDetail>() {
        @Override
        public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {}

        @Override
        public void onFailure(Call<MovieDetail> call, Throwable t) {}
    };

    private Callback<ApiResponse<MovieVideo>> movieVideoCallback = new Callback<ApiResponse<MovieVideo>>() {
        @Override
        public void onResponse(Call<ApiResponse<MovieVideo>> call, Response<ApiResponse<MovieVideo>> response) {}

        @Override
        public void onFailure(Call<ApiResponse<MovieVideo>> call, Throwable t) {}
    };

    private Callback<ApiResponse<MovieReview>> movieReviewCallback = new Callback<ApiResponse<MovieReview>>() {
        @Override
        public void onResponse(Call<ApiResponse<MovieReview>> call, Response<ApiResponse<MovieReview>> response) {}

        @Override
        public void onFailure(Call<ApiResponse<MovieReview>> call, Throwable t) {}
    };

    private void fetchMovieDetails() {
        Stream.of(movieListings).forEach(movieListing -> {
            Picasso.with(MoviesApplication.getApp()).load(movieListing.getPosterUrl()).fetch();
            MoviesApplication.getApp().getApiManager().getEndpoints().getMovieDetails(movieListing.getId()).enqueue(movieDetailCallback);
            MoviesApplication.getApp().getApiManager().getEndpoints().getMovieVideos(movieListing.getId()).enqueue(movieVideoCallback);
            MoviesApplication.getApp().getApiManager().getEndpoints().getMovieReviews(movieListing.getId()).enqueue(movieReviewCallback);
        });
    }

    private void selectMovie(int position) {
        EventBus.getDefault().post(new MovieSelectionEvent(movieListings.get(position).getId()));
        if (MoviesApplication.getApp().isLargeLayout()) {
            notifyItemChanged(selectedPosition, false);
            selectedPosition = position;
            notifyItemChanged(selectedPosition, true);
        }
    }

    @Override
    public int getItemCount() {
        return movieListings.size();
    }

    @Override
    public MovieListingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_listing_item, parent, false);
        return new MovieListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieListingViewHolder holder, int position) {
        Picasso.with(holder.itemView.getContext()).load(movieListings.get(position).getPosterUrl()).into(holder.posterView);
    }

    @Override
    public void onBindViewHolder(MovieListingViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else if (MoviesApplication.getApp().isLargeLayout()) {
            holder.indicatorView.setVisibility(position == selectedPosition ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public class MovieListingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.poster_view) ImageView posterView;
        @BindView(R.id.indicator_view) View indicatorView;

        public MovieListingViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.poster_view)
        public void onClick() {
            selectMovie(getAdapterPosition());
        }
    }
}