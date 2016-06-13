package com.piatt.udacity.popularmovies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.event.MovieSelectEvent;
import com.piatt.udacity.popularmovies.model.ApiResponse;
import com.piatt.udacity.popularmovies.model.MovieDetail;
import com.piatt.udacity.popularmovies.model.MovieListing;
import com.piatt.udacity.popularmovies.model.MovieReview;
import com.piatt.udacity.popularmovies.model.MovieVideo;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.Getter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListingsAdapter extends RecyclerView.Adapter<MovieListingsAdapter.MovieListingViewHolder> {
    private int selectedPosition;
    @Getter private List<MovieListing> movieListings = new ArrayList<>();

    /**
     * This method is called when a favorite is added to the movie listings screen.
     * If this is the first listing to be added and the device in use is a tablet,
     * the listing is selected in order to trigger an update to the detail screen.
     */
    public void addMovieListing(MovieListing listing) {
        movieListings.add(listing);
        notifyItemInserted(movieListings.size() - 1);
        if (MoviesApplication.getApp().isLargeLayout() && movieListings.size() == 1) {
            selectMovie(0);
        }
    }

    /**
     * This method is called when a favorite is removed from the movie listings screen.
     * The listing in question is only removed if it currently exists in the list.
     */
    public void removeMovieListing(int movieId) {
        Optional<MovieListing> movieListing = Stream.of(movieListings).filter(listing -> listing.getId() == movieId).findFirst();
        if (movieListing.isPresent()) {
            int position = movieListings.indexOf(movieListing.get());
            movieListings.remove(movieListing.get());
            notifyItemRemoved(position);
        }
    }

    /**
     * This method is called when a movie filter other than favorites is selected,
     * either by the user or on app launch. If the device in use is a tablet,
     * If this is the first listing to be added and the device in use is a tablet,
     * the first listing is selected in order to trigger an update to the detail screen.
     */
    public void setMovieListings(List<MovieListing> listings) {
        movieListings.clear();
        movieListings = listings;
        notifyDataSetChanged();
        fetchMovieDetails();
        if (MoviesApplication.getApp().isLargeLayout()) {
            selectMovie(0);
        }
    }

    /**
     * This method is called prior to the individual fetching of favorite listings,
     * since those listings will be added one at a time.
     */
    public void clearMovieListings() {
        movieListings.clear();
        notifyDataSetChanged();
    }

    /**
     * When a new batch of movie listings is set, API calls are made to pre-fetch
     * poster images, details, trailers, and reviews, if available.
     * Although these items are needed on the movie listings screen,
     * anything that is pre-fetched will be cached for instant access
     * when the user selects a movie from the list at any point thereafter.
     */
    private void fetchMovieDetails() {
        Stream.of(movieListings).forEach(movieListing -> {
            Picasso.with(MoviesApplication.getApp()).load(movieListing.getPosterUrl()).fetch();
            MoviesApplication.getApp().getApiManager().getEndpoints().getMovieDetails(movieListing.getId()).enqueue(movieDetailCallback);
            MoviesApplication.getApp().getApiManager().getEndpoints().getMovieVideos(movieListing.getId()).enqueue(movieVideoCallback);
            MoviesApplication.getApp().getApiManager().getEndpoints().getMovieReviews(movieListing.getId()).enqueue(movieReviewCallback);
        });
    }

    /**
     * When a movie is selected from the movie listings screen, an event is fired
     * to handle updating the movie details screen accordingly.
     * If the device in use is a tablet, the selected position is tracked and updated to show
     * the currently selected movie to the user.
     */
    private void selectMovie(int position) {
        EventBus.getDefault().post(new MovieSelectEvent(movieListings.get(position).getId()));
        if (MoviesApplication.getApp().isLargeLayout()) {
            notifyItemChanged(selectedPosition);
            selectedPosition = position;
            notifyItemChanged(selectedPosition);
        }
    }

    /**
     * The following three callbacks are invoked when their respective API call returns.
     * Since the data is only being pre-fetched for caching here, nothing is done with the response.
     * If the same API call has already been pre-fetched and cached, the cached version is served up immediately.
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

    @Override
    public int getItemCount() {
        return movieListings.size();
    }

    @Override
    public MovieListingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_listing_item, parent, false);
        return new MovieListingViewHolder(view);
    }

    /**
     * This method binds the poster image for each movie listing to it's grid cell.
     * If the device in use is a tablet, the selection indicator is shown if the position matches the tracked position.
     */
    @Override
    public void onBindViewHolder(MovieListingViewHolder holder, int position) {
        Picasso.with(holder.itemView.getContext()).load(movieListings.get(position).getPosterUrl()).placeholder(R.color.blue).into(holder.posterView);
        if (MoviesApplication.getApp().isLargeLayout()) {
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