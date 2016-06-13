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

    /**
     * Movies are displayed in a two column grid on phones, and in a 4 column grid on tablets.
     * The movie filter dropdown spinner is initialized with the last selected filter.
     * If no saved filter is available, the first option is selected.
     */
    private void configureView() {
        loadingView.getIndeterminateDrawable().setColorFilter(whiteColor, PorterDuff.Mode.SRC_IN);
        movieListingsAdapter = new MovieListingsAdapter();
        movieList.setHasFixedSize(true);
        movieList.setLayoutManager(new GridLayoutManager(movieList.getContext(), MoviesApplication.getApp().isLargeLayout() ? 4 : 2));
        movieList.setAdapter(movieListingsAdapter);
        filterSpinner.setOnItemSelectedListener(filterSpinnerSelectionListener);
        filterSpinner.setSelection(MoviesApplication.getApp().getFavoritesManager().getMoviesFilter());
    }

    /**
     * Unlike the other two filter options which query an API endpoint,
     * the favorites are fetched individually from the list of saved movie ids.
     * If no saved favorites are available, the user is shown a dialog explaining how to add favorites.
     */
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

    /**
     * This callback is used for individual favorites only.
     * Each successful callback for a particular movie results in it being added to the existing listings.
     * If the API call fails, the user is shown the appropriate error dialog.
     */
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
     * This callback is invoked when the full set of movie listings for a particular filter are fetched.
     * If the response is empty or the API call fails, the user is shown the appropriate error dialog.
     * Otherwise, the adapter is updated with the results.
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

    /**
     * This listener is invoked when a movie listings filter is selected from the dropdown spinner,
     * either by the user or from the last saved filter value on app launch.
     * The new selection is saved to shared preferences and the proper API calls are made.
     */
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

    /**
     * This event handler is invoked when an individual movie is favorited or unfavorited from a detail screen.
     * If the current movie listings filter is favorites, the listings are updated accordingly to show immediate updates.
     * If removal of a favorite results in no favorites remaining, the user is shown a dialog explaining how to add favorites.
     */
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