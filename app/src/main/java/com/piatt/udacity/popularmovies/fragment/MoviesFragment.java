package com.piatt.udacity.popularmovies.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.adapter.MovieListingsAdapter;
import com.piatt.udacity.popularmovies.event.FavoritesUpdateEvent;
import com.piatt.udacity.popularmovies.model.ApiResponse;
import com.piatt.udacity.popularmovies.model.MovieDetail;
import com.piatt.udacity.popularmovies.model.MovieListing;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesFragment extends Fragment {
    private final int POPULAR = 0, TOP_RATED = 1, FAVORITES = 2;
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.filter_spinner) Spinner filterSpinner;
    @BindView(R.id.loading_view) ProgressBar loadingView;
    @BindView(R.id.message_layout) LinearLayout messageLayout;
    @BindView(R.id.icon_view) TextView iconView;
    @BindView(R.id.message_view) TextView messageView;
    @BindView(R.id.movie_list) RecyclerView movieList;
    @Getter @Setter private MovieListingsAdapter movieListingsAdapter;
    @Getter @Setter private boolean spinnerInitialized;

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
        movieListingsAdapter = new MovieListingsAdapter();
        movieList.setHasFixedSize(true);
        movieList.setLayoutManager(new GridLayoutManager(movieList.getContext(), MoviesApplication.getApp().isLargeLayout() ? 4 : 2));
        movieList.setAdapter(movieListingsAdapter);
    }

    private void getFavoriteMovies() {
        List<Integer> favoriteMovies = MoviesApplication.getApp().getFavoritesManager().getFavoriteMovies();
        movieListingsAdapter.clearMovieListings();
        Stream.of(favoriteMovies).forEach(movieId -> MoviesApplication.getApp().getApiManager().getEndpoints().getMovieDetails(movieId).enqueue(movieDetailCallback));
    }

    private Callback<MovieDetail> movieDetailCallback = new Callback<MovieDetail>() {
        @Override
        public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
            if (response.isSuccessful()) {
                movieListingsAdapter.addMovieListing(response.body());
            }
        }

        @Override
        public void onFailure(Call<MovieDetail> call, Throwable t) {}
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
            if (response.isSuccessful() && !response.body().getResults().isEmpty()) {
                movieListingsAdapter.setMovieListings(response.body().getResults());
            } else {
                MoviesApplication.getApp().showErrorMessage(response.message());
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<MovieListing>> call, Throwable t) {
            MoviesApplication.getApp().showErrorMessage(t.getMessage());
        }
    };

    @OnItemSelected(R.id.filter_spinner)
    public void onFilterSpinnerItemSelected(int position) {
        if (isSpinnerInitialized()) {
            movieList.getLayoutManager().scrollToPosition(0);
            switch (position) {
                case POPULAR: MoviesApplication.getApp().getApiManager().getEndpoints().getPopularMovies().enqueue(movieListingCallback);
                    break;
                case TOP_RATED: MoviesApplication.getApp().getApiManager().getEndpoints().getTopRatedMovies().enqueue(movieListingCallback);
                    break;
                case FAVORITES: getFavoriteMovies();
                    break;
            }
        } else {
            setSpinnerInitialized(true);
        }
    }

    @Subscribe
    public void onFavoritesUpdate(FavoritesUpdateEvent event) {
        if (filterSpinner.getSelectedItemPosition() == FAVORITES) {
            onFilterSpinnerItemSelected(FAVORITES);
        }
    }
}