package com.piatt.udacity.popularmovies.fragment;

import android.app.Fragment;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.annimon.stream.Stream;
import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.adapter.MovieListingsAdapter;
import com.piatt.udacity.popularmovies.event.FavoritesUpdateEvent;
import com.piatt.udacity.popularmovies.event.MovieMessageEvent;
import com.piatt.udacity.popularmovies.model.ApiResponse;
import com.piatt.udacity.popularmovies.model.MessageType;
import com.piatt.udacity.popularmovies.model.MovieDetail;
import com.piatt.udacity.popularmovies.model.MovieListing;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesFragment extends Fragment {
    private final int POPULAR = 0, TOP_RATED = 1, FAVORITES = 2;
    @BindColor(R.color.white) int whiteColor;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.filter_spinner) Spinner filterSpinner;
    @BindView(R.id.loading_view) ProgressBar loadingView;
    @BindView(R.id.movie_list) RecyclerView movieList;
    @Getter @Setter private MovieListingsAdapter movieListingsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (coordinatorLayout == null) {
            View view = inflater.inflate(R.layout.movies_fragment, container, false);
            ButterKnife.bind(this, view);
            configureView();
        }
        return coordinatorLayout;
    }

    private void configureView() {
        loadingView.getIndeterminateDrawable().setColorFilter(whiteColor, PorterDuff.Mode.SRC_IN);
        movieListingsAdapter = new MovieListingsAdapter();
        movieList.setHasFixedSize(true);
        movieList.setLayoutManager(new GridLayoutManager(movieList.getContext(), MoviesApplication.getApp().isLargeLayout() ? 4 : 2));
        movieList.setAdapter(movieListingsAdapter);
        filterSpinner.setOnItemSelectedListener(filterSpinnerSelectionListener);
        filterSpinner.setSelection(MoviesApplication.getApp().getFavoritesManager().getMoviesFilter());
    }

    private void getFavoriteMovies() {
        loadingView.setVisibility(View.GONE);
        List<Integer> favoriteMovies = MoviesApplication.getApp().getFavoritesManager().getFavoriteMovies();
        if (favoriteMovies.isEmpty()) {
            EventBus.getDefault().post(new MovieMessageEvent(MessageType.FAVORITES));
        } else {
            movieListingsAdapter.clearMovieListings();
            movieList.setVisibility(View.VISIBLE);
            Stream.of(favoriteMovies).forEach(movieId -> MoviesApplication.getApp().getApiManager().getEndpoints().getMovieDetails(movieId).enqueue(movieDetailCallback));
        }
    }

    private void showErrorMessage() {
        MessageType messageType = MoviesApplication.getApp().isNetworkAvailable() ? MessageType.API : MessageType.CONNECTION;
        EventBus.getDefault().post(new MovieMessageEvent(messageType));
    }

    private Callback<MovieDetail> movieDetailCallback = new Callback<MovieDetail>() {
        @Override
        public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
            if (response.isSuccessful()) {
                movieListingsAdapter.addMovieListing(response.body());
            }
        }

        @Override
        public void onFailure(Call<MovieDetail> call, Throwable t) {
            showErrorMessage();
        }
    };

    /**
     * Upon receipt of data, either from the network or cache,
     * this callback method populates the MovieListingsAdapter with data.
     * Additionally, prefetching of MovieDetailItems for each MovieListing object is done in the background
     * to support performance and offline access.
     */
    private Callback<ApiResponse<MovieListing>> movieListingCallback = new Callback<ApiResponse<MovieListing>>() {
        @Override
        public void onResponse(Call<ApiResponse<MovieListing>> call, Response<ApiResponse<MovieListing>> response) {
            loadingView.setVisibility(View.GONE);
            if (response.isSuccessful() && !response.body().getResults().isEmpty()) {
                movieListingsAdapter.setMovieListings(response.body().getResults());
                movieList.setVisibility(View.VISIBLE);
            } else {
                showErrorMessage();
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<MovieListing>> call, Throwable t) {
            loadingView.setVisibility(View.GONE);
            showErrorMessage();
        }
    };

    private OnItemSelectedListener filterSpinnerSelectionListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            MoviesApplication.getApp().getFavoritesManager().setMoviesFilter(position);
            movieList.setVisibility(View.INVISIBLE);
            movieList.getLayoutManager().scrollToPosition(0);
            loadingView.setVisibility(View.VISIBLE);
            switch (position) {
                case POPULAR: MoviesApplication.getApp().getApiManager().getEndpoints().getPopularMovies().enqueue(movieListingCallback);
                    break;
                case TOP_RATED: MoviesApplication.getApp().getApiManager().getEndpoints().getTopRatedMovies().enqueue(movieListingCallback);
                    break;
                case FAVORITES: getFavoriteMovies();
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {}
    };

    @Subscribe
    public void onFavoritesUpdate(FavoritesUpdateEvent event) {
        if (filterSpinner.getSelectedItemPosition() == FAVORITES) {
            if (event.isFavorite()) {
                MoviesApplication.getApp().getApiManager().getEndpoints().getMovieDetails(event.getMovieId()).enqueue(movieDetailCallback);
            } else {
                movieListingsAdapter.removeMovieListing(event.getMovieId());
                if (movieListingsAdapter.getMovieListings().isEmpty()) {
                    EventBus.getDefault().post(new MovieMessageEvent(MessageType.FAVORITES));
                }
            }
        }
    }
}