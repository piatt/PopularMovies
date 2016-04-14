package com.piatt.udacity.popularmovies.fragment;

import android.app.Fragment;
import android.os.Bundle;
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

import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.adapter.DetailAdapter;
import com.piatt.udacity.popularmovies.manager.ContextManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class DetailFragment extends Fragment {
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    @Bind(R.id.detail_title) TextView titleView;
    @Bind(R.id.detail_extras) ExpandableListView extrasView;

    private DetailAdapter detailAdapter;
    private DetailViewHolder detailViewHolder;
    private int movieId;

    public DetailFragment() {}

    public static DetailFragment newInstance(int movieId) {
        DetailFragment fragment = new DetailFragment();
        fragment.movieId = movieId;
        return fragment;
    }

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

    /**
     * Called on fragment creation or rotation, this method smartly updates the existing bound views, if available.
     * Additionally, although it appears to make a network call each time the method is invoked via the updateMovieDetailView method,
     * the ContextManager handles response caching, delivering the data to the movieDetailsCallback instantly from cache, if available, even offline.
     * This eliminates the need for the fragment itself to handle instance state.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        View detailView = inflater.inflate(R.layout.detail_header, null, false);
        ButterKnife.bind(this, view);

        if (detailAdapter == null) {
            detailAdapter = new DetailAdapter(getActivity());
        }

        extrasView.setAdapter(detailAdapter);
        extrasView.addHeaderView(detailView);
        detailViewHolder = new DetailViewHolder(detailView);

        updateMovieDetailView(movieId);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (!ContextManager.getInstance().isLargeLayout()) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!ContextManager.getInstance().isLargeLayout() && item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateMovieDetailView(int movieId) {
//        if (movieId > 0) {
//            ContextManager.movieDataService.getMovieDetails(movieId, movieDetailsCallback);
//            ContextManager.movieDataService.getMovieExtras(movieId, ContextManager.API_REVIEWS_ENDPOINT, movieReviewsCallback);
//            ContextManager.movieDataService.getMovieExtras(movieId, ContextManager.API_VIDEOS_ENDPOINT, movieVideosCallback);
//        }
    }

//    /**
//     * Called by the ContextManager in response to a request to update the detail view,
//     * this callback method is invoked upon receipt of data either from the network or cache,
//     * and populates a new DetailItem for use in the DetailFragment.
//     */
//    private Callback<JsonObject> movieDetailsCallback = new Callback<JsonObject>() {
//        @Override
//        public void success(JsonObject jsonObject, Response response) {
//            DetailItem detailItem = new DetailItem(jsonObject);
//
//            titleView.setText(detailItem.getTitle());
//            detailViewHolder.releaseDateView.setText(detailItem.getReleaseDate());
//            detailViewHolder.runtimeView.setText(detailItem.getRuntime());
//            detailViewHolder.ratingView.setText(detailItem.getRating());
//            detailViewHolder.favoriteToggleButton.setChecked(ContextManager.isFavorite(movieId));
//
//            Picasso.with(getActivity()).load(detailItem.getPosterUrl()).into(detailViewHolder.posterView);
//
//            detailAdapter.notifyDataSetInvalidated();
//            detailAdapter.setSynopsis(detailItem.getSynopsis());
//
//            if (!extrasView.isGroupExpanded(0)) {
//                extrasView.expandGroup(0);
//            }
//        }
//
//        @Override
//        public void failure(RetrofitError error) {
//            Log.d(LOG_TAG, error.getMessage());
//        }
//    };
//
//    /**
//     * Called by the ContextManager in response to a request to update the detail view,
//     * this callback method is invoked upon receipt of data either from the network or cache,
//     * and populates the list of reviews for use in the DetailFragment.
//     */
//    private Callback<JsonObject> movieReviewsCallback = new Callback<JsonObject>() {
//        @Override
//        public void success(JsonObject jsonObject, Response response) {
//            JsonArray reviews = jsonObject.get(ContextManager.API_RESULTS_FILTER).getAsJsonArray();
//            ArrayList<ReviewItem> reviewItems = new ArrayList<>();
//
//            for (int i = 0; i < reviews.size(); i++) {
//                ReviewItem reviewItem = new ReviewItem(reviews.get(i).getAsJsonObject());
//                reviewItems.add(reviewItem);
//            }
//
//            detailAdapter.setReviewItems(reviewItems);
//        }
//
//        @Override
//        public void failure(RetrofitError error) {
//            Log.d(LOG_TAG, error.getMessage());
//        }
//    };
//
//    /**
//     * Called by the ContextManager in response to a request to update the detail view,
//     * this callback method is invoked upon receipt of data either from the network or cache,
//     * and populates the list of trailers for use in the DetailFragment.
//     */
//    private Callback<JsonObject> movieVideosCallback = new Callback<JsonObject>() {
//        @Override
//        public void success(JsonObject jsonObject, Response response) {
//            JsonArray videos = jsonObject.get(ContextManager.API_RESULTS_FILTER).getAsJsonArray();
//            ArrayList<VideoItem> videoItems = new ArrayList<>();
//
//            for (int i = 0; i < videos.size(); i++) {
//                VideoItem videoItem = new VideoItem(videos.get(i).getAsJsonObject());
//                videoItems.add(videoItem);
//            }
//
//            detailAdapter.setVideoItems(videoItems);
//        }
//
//        @Override
//        public void failure(RetrofitError error) {
//            Log.d(LOG_TAG, error.getMessage());
//        }
//    };

    public class DetailViewHolder {
        @Bind(R.id.detail_release_date) TextView releaseDateView;
        @Bind(R.id.detail_runtime) TextView runtimeView;
        @Bind(R.id.detail_rating) TextView ratingView;
        @Bind(R.id.detail_poster) ImageView posterView;
        @Bind(R.id.detail_favorite_toggle) ToggleButton favoriteToggleButton;

        public DetailViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.detail_favorite_toggle)
        public void onFavoriteToggle(ToggleButton toggleButton) {
//            if (toggleButton.isChecked()) {
//                ContextManager.addFavorite(movieId);
//            } else {
//                ContextManager.removeFavorite(movieId);
//            }
        }
    }
}