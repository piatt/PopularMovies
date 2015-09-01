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
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieDetailFragment extends Fragment {
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    @Bind(R.id.detail_title) TextView titleView;
    @Bind(R.id.detail_extras) ExpandableListView extrasView;

    private MovieDetailAdapter movieDetailAdapter;
    private DetailViewHolder detailViewHolder;
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
        View detailView = inflater.inflate(R.layout.detail_header, null, false);
        ButterKnife.bind(this, view);

        if (movieDetailAdapter == null) {
            movieDetailAdapter = new MovieDetailAdapter(getActivity());
        }

        extrasView.setAdapter(movieDetailAdapter);
        extrasView.addHeaderView(detailView);
        detailViewHolder = new DetailViewHolder(detailView);

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
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateMovieDetailView(int movieId) {
        if (movieId > 0) {
            MovieListService.movieDataService.getMovieDetails(movieId, movieDetailsCallback);
            MovieListService.movieDataService.getMovieExtras(movieId, MovieListService.API_REVIEWS_ENDPOINT, movieReviewsCallback);
            MovieListService.movieDataService.getMovieExtras(movieId, MovieListService.API_VIDEOS_ENDPOINT, movieVideosCallback);
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
            detailViewHolder.releaseDateView.setText(movieDetailItem.getReleaseDate());
            detailViewHolder.runtimeView.setText(movieDetailItem.getRuntime());
            detailViewHolder.ratingView.setText(movieDetailItem.getRating());

            Picasso.with(getActivity()).load(movieDetailItem.getPosterUrl()).into(detailViewHolder.posterView);

            movieDetailAdapter.setSynopsis(movieDetailItem.getSynopsis());

            if (!extrasView.isGroupExpanded(0)) {
                extrasView.expandGroup(0);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, error.getMessage());
        }
    };

    /**
     * Called by the MovieListService in response to a request to update the detail view,
     * this callback method is invoked upon receipt of data either from the network or cache,
     * and populates the list of reviews for use in the MovieDetailFragment.
     */
    private Callback<JsonObject> movieReviewsCallback = new Callback<JsonObject>() {
        @Override
        public void success(JsonObject jsonObject, Response response) {
            JsonArray reviews = jsonObject.get(MovieListService.API_RESULTS_FILTER).getAsJsonArray();
            ArrayList<MovieReviewItem> movieReviewItems = new ArrayList<>();

            for (int i = 0; i < reviews.size(); i++) {
                MovieReviewItem movieReviewItem = new MovieReviewItem(reviews.get(i).getAsJsonObject());
                movieReviewItems.add(movieReviewItem);
            }

            movieDetailAdapter.setMovieReviewItems(movieReviewItems);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, error.getMessage());
        }
    };

    /**
     * Called by the MovieListService in response to a request to update the detail view,
     * this callback method is invoked upon receipt of data either from the network or cache,
     * and populates the list of trailers for use in the MovieDetailFragment.
     */
    private Callback<JsonObject> movieVideosCallback = new Callback<JsonObject>() {
        @Override
        public void success(JsonObject jsonObject, Response response) {
            JsonArray videos = jsonObject.get(MovieListService.API_RESULTS_FILTER).getAsJsonArray();
            ArrayList<MovieVideoItem> movieVideoItems = new ArrayList<>();

            for (int i = 0; i < videos.size(); i++) {
                MovieVideoItem movieVideoItem = new MovieVideoItem(videos.get(i).getAsJsonObject());
                movieVideoItems.add(movieVideoItem);
            }

            movieDetailAdapter.setMovieVideoItems(movieVideoItems);
        }

        @Override
        public void failure(RetrofitError error) {
            Log.d(LOG_TAG, error.getMessage());
        }
    };

    public class DetailViewHolder {
        @Bind(R.id.detail_release_date) TextView releaseDateView;
        @Bind(R.id.detail_runtime) TextView runtimeView;
        @Bind(R.id.detail_rating) TextView ratingView;
        @Bind(R.id.detail_poster) ImageView posterView;
        @Bind(R.id.detail_favorite_toggle) ToggleButton favoriteToggleButton;

        public DetailViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}