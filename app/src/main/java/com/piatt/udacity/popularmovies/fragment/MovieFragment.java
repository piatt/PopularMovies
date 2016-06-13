package com.piatt.udacity.popularmovies.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.adapter.MovieReviewsAdapter;
import com.piatt.udacity.popularmovies.adapter.MovieVideosAdapter;
import com.piatt.udacity.popularmovies.event.FavoritesUpdateEvent;
import com.piatt.udacity.popularmovies.event.MovieMessageEvent;
import com.piatt.udacity.popularmovies.event.MovieVideoShareEvent;
import com.piatt.udacity.popularmovies.model.ApiResponse;
import com.piatt.udacity.popularmovies.model.MessageType;
import com.piatt.udacity.popularmovies.model.MovieDetail;
import com.piatt.udacity.popularmovies.model.MovieReview;
import com.piatt.udacity.popularmovies.model.MovieVideo;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragment extends Fragment {
    private static final String MOVIE_ID_KEY = "MOVIE_ID";
    @BindString(R.string.favorite_on_icon) String favoriteOnIcon;
    @BindString(R.string.favorite_off_icon) String favoriteOffIcon;
    @BindString(R.string.contract_icon) String contractIcon;
    @BindString(R.string.expand_icon) String expandIcon;
    @BindString(R.string.add_favorite_message) String addFavoriteMessage;
    @BindString(R.string.remove_favorite_message) String removeFavoriteMessage;
    @BindView(R.id.back_button) TextView backButton;
    @BindView(R.id.title_view) TextView titleView;
    @BindView(R.id.share_button) TextView shareButton;
    @BindView(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.poster_view) ImageView posterView;
    @BindView(R.id.release_date_view) TextView releaseDateView;
    @BindView(R.id.rating_view) TextView ratingView;
    @BindView(R.id.runtime_view) TextView runtimeView;
    @BindView(R.id.overview_view) TextView overviewView;
    @BindView(R.id.favorite_button) TextView favoriteButton;
    @BindView(R.id.videos_layout) LinearLayout videosLayout;
    @BindView(R.id.videos_toggle_view) TextView videosToggleView;
    @BindView(R.id.video_list) RecyclerView videoList;
    @BindView(R.id.reviews_layout) LinearLayout reviewsLayout;
    @BindView(R.id.reviews_toggle_view) TextView reviewsToggleView;
    @BindView(R.id.review_list) RecyclerView reviewList;
    private int movieId;
    private Unbinder unbinder;

    public static MovieFragment newInstance(int movieId) {
        MovieFragment fragment = new MovieFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(MOVIE_ID_KEY, movieId);
        fragment.setArguments(arguments);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !MoviesApplication.getApp().isLargeLayout()) {
            fragment.setEnterTransition(new Slide(Gravity.RIGHT));
        }
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        movieId = getArguments().getInt(MOVIE_ID_KEY);
    }

    /**
     * Called on fragment creation or rotation, this method smartly updates the existing bound views, if available.
     * Additionally, although it appears to make a network call each time the method is invoked via the updateMovieDetailView method,
     * the ContextManager handles response caching, delivering the data to the movieDetailsCallback instantly from cache, if available, even offline.
     * This eliminates the need for the fragment itself to handle instance state.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.movie_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        configureView();
        return view;
    }

    private void configureView() {
        if (!MoviesApplication.getApp().isLargeLayout()) {
            backButton.setVisibility(View.VISIBLE);
        }
        MoviesApplication.getApp().getApiManager().getEndpoints().getMovieDetails(movieId).enqueue(movieDetailCallback);
        MoviesApplication.getApp().getApiManager().getEndpoints().getMovieVideos(movieId).enqueue(movieVideoCallback);
        MoviesApplication.getApp().getApiManager().getEndpoints().getMovieReviews(movieId).enqueue(movieReviewCallback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private Callback<MovieDetail> movieDetailCallback = new Callback<MovieDetail>() {
        @Override
        public void onResponse(Call<MovieDetail> call, Response<MovieDetail> response) {
            if (response.isSuccessful()) {
                MovieDetail movieDetail = response.body();
                Picasso.with(posterView.getContext()).load(movieDetail.getPosterUrl()).into(posterView);
                titleView.setText(movieDetail.getTitle());
                releaseDateView.setText(movieDetail.getReleaseDate());
                ratingView.setText(movieDetail.getRating());
                runtimeView.setText(movieDetail.getRuntime());
                overviewView.setText(movieDetail.getOverview());
                favoriteButton.setText(MoviesApplication.getApp().getFavoritesManager().isFavoriteMovie(movieId) ? favoriteOnIcon : favoriteOffIcon);
            }
        }

        @Override
        public void onFailure(Call<MovieDetail> call, Throwable t) {
            MessageType messageType = MoviesApplication.getApp().isNetworkAvailable() ? MessageType.API : MessageType.CONNECTION;
            EventBus.getDefault().post(new MovieMessageEvent(messageType));
            if (!MoviesApplication.getApp().isLargeLayout()) {
                onBackButtonClick();
            }
        }
    };

    private Callback<ApiResponse<MovieVideo>> movieVideoCallback = new Callback<ApiResponse<MovieVideo>>() {
        @Override
        public void onResponse(Call<ApiResponse<MovieVideo>> call, Response<ApiResponse<MovieVideo>> response) {
            if (response.isSuccessful() && !response.body().getResults().isEmpty()) {
                videoList.setHasFixedSize(true);
                videoList.setLayoutManager(new LinearLayoutManager(videoList.getContext()));
                videoList.setAdapter(new MovieVideosAdapter(response.body().getResults()));
                videosLayout.setVisibility(View.VISIBLE);
                shareButton.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<MovieVideo>> call, Throwable t) {}
    };

    private Callback<ApiResponse<MovieReview>> movieReviewCallback = new Callback<ApiResponse<MovieReview>>() {
        @Override
        public void onResponse(Call<ApiResponse<MovieReview>> call, Response<ApiResponse<MovieReview>> response) {
            if (response.isSuccessful() && !response.body().getResults().isEmpty()) {
                reviewList.setHasFixedSize(true);
                reviewList.setLayoutManager(new LinearLayoutManager(reviewList.getContext()));
                reviewList.setAdapter(new MovieReviewsAdapter(response.body().getResults()));
                reviewsLayout.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<MovieReview>> call, Throwable t) {}
    };

    @OnClick(R.id.back_button)
    public void onBackButtonClick() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.share_button)
    public void onShareButtonClick() {
        EventBus.getDefault().post(new MovieVideoShareEvent());
    }

    @OnClick(R.id.favorite_button)
    public void onFavoriteButtonClick() {
        String snackbarMessage;
        if (MoviesApplication.getApp().getFavoritesManager().isFavoriteMovie(movieId)) {
            favoriteButton.setText(favoriteOffIcon);
            MoviesApplication.getApp().getFavoritesManager().removeFavoriteMovie(movieId);
            EventBus.getDefault().post(new FavoritesUpdateEvent(movieId, false));
            snackbarMessage = String.format(removeFavoriteMessage, titleView.getText().toString());
        } else {
            favoriteButton.setText(favoriteOnIcon);
            MoviesApplication.getApp().getFavoritesManager().addFavoriteMovie(movieId);
            EventBus.getDefault().post(new FavoritesUpdateEvent(movieId, true));
            snackbarMessage = String.format(addFavoriteMessage, titleView.getText().toString());
        }
        Snackbar.make(favoriteButton, snackbarMessage, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.videos_toggle_button)
    public void onVideosToggleButtonClick() {
        videosToggleView.setText(videoList.isShown() ? expandIcon : contractIcon);
        videoList.setVisibility(videoList.isShown() ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.reviews_toggle_button)
    public void onReviewsToggleButtonClick() {
        reviewsToggleView.setText(reviewList.isShown() ? expandIcon : contractIcon);
        reviewList.setVisibility(reviewList.isShown() ? View.GONE : View.VISIBLE);
    }
}