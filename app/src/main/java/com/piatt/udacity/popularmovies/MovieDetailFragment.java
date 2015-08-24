package com.piatt.udacity.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieDetailFragment extends Fragment {
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    @Bind(R.id.detail_title) TextView titleView;
    @Bind(R.id.detail_release_date) TextView releaseDateView;
    @Bind(R.id.detail_runtime) TextView runtimeView;
    @Bind(R.id.detail_rating) TextView ratingView;
    @Bind(R.id.detail_synopsis) TextView synopsisView;
    @Bind(R.id.detail_poster) ImageView posterView;

    private int movieId;

    public MovieDetailFragment() {}

    public static MovieDetailFragment newInstance(int movieId) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.movieId = movieId;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    /**
     * Called on fragment creation or rotation, this method smartly updates the existing bound views, if available.
     * Additionally, although it appears to make a network call each time the method is invoked via the updateMovieDetailView method,
     * the MovieListService handles response caching, delivering the data to the movieDetailsCallback instantly from cache, if available, even offline.
     * This eliminates the need for the fragment itself to handle instance state.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        updateMovieDetailView(movieId);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (!MovieListService.isDualPane()) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!MovieListService.isDualPane() && item.getItemId() == android.R.id.home) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateMovieDetailView(int movieId) {
        if (movieId > 0) {
            MovieListService.movieDataService.getMovieDetails(movieId, movieDetailsCallback);
        }
    }

    /**
     * Called by the MovieListService in response to a request to update the detail view,
     * this callback method is invoked upon receipt of data either from the network or cache,
     * and populates a new MovieDetailItem for use in the MovieDetailFragment.
     */
    private Callback<JsonObject> movieDetailsCallback = new Callback<JsonObject>() {
        @Override
        public void success(JsonObject jsonObject, Response response) {
            MovieDetailItem movieDetailItem = new MovieDetailItem(jsonObject);

            titleView.setText(movieDetailItem.getTitle());
            releaseDateView.setText(movieDetailItem.getReleaseDate());
            runtimeView.setText(movieDetailItem.getRuntime());
            ratingView.setText(movieDetailItem.getRating());
            synopsisView.setText(movieDetailItem.getSynopsis());

            Picasso.with(getActivity()).load(movieDetailItem.getPosterUrl()).into(posterView);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, error.getMessage());
        }
    };
}