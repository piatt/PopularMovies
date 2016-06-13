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

    public void addMovieListing(MovieListing listing) {
        movieListings.add(listing);
        notifyItemInserted(movieListings.size() - 1);
        fetchMovieDetails();
        if (MoviesApplication.getApp().isLargeLayout() && movieListings.size() == 1) {
            selectMovie(0);
        }
    }

    public void removeMovieListing(int movieId) {
        Optional<MovieListing> movieListing = Stream.of(movieListings).filter(listing -> listing.getId() == movieId).findFirst();
        if (movieListing.isPresent()) {
            int position = movieListings.indexOf(movieListing.get());
            movieListings.remove(movieListing.get());
            notifyItemRemoved(position);
        }
    }

    public void setMovieListings(List<MovieListing> listings) {
        movieListings.clear();
        movieListings = listings;
        notifyDataSetChanged();
        if (MoviesApplication.getApp().isLargeLayout()) {
            selectMovie(0);
        }
    }

    public void clearMovieListings() {
        movieListings.clear();
        notifyDataSetChanged();
    }

    private void fetchMovieDetails() {
        Stream.of(movieListings).forEach(movieListing -> {
            Picasso.with(MoviesApplication.getApp()).load(movieListing.getPosterUrl()).fetch();
            MoviesApplication.getApp().getApiManager().getEndpoints().getMovieDetails(movieListing.getId()).enqueue(movieDetailCallback);
            MoviesApplication.getApp().getApiManager().getEndpoints().getMovieVideos(movieListing.getId()).enqueue(movieVideoCallback);
            MoviesApplication.getApp().getApiManager().getEndpoints().getMovieReviews(movieListing.getId()).enqueue(movieReviewCallback);
        });
    }

    private void selectMovie(int position) {
        EventBus.getDefault().post(new MovieSelectEvent(movieListings.get(position).getId()));
        if (MoviesApplication.getApp().isLargeLayout()) {
            notifyItemChanged(selectedPosition);
            selectedPosition = position;
            notifyItemChanged(selectedPosition);
        }
    }

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