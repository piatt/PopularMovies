package com.piatt.udacity.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetailFragment extends Fragment {
    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    @Bind(R.id.detail_title) TextView titleView;
    @Bind(R.id.detail_release_date) TextView releaseDateView;
    @Bind(R.id.detail_rating) TextView ratingView;
    @Bind(R.id.detail_synopsis) TextView synopsisView;
    @Bind(R.id.detail_poster) ImageView posterView;

    private MovieDetailItem movieDetailItem;

    public MovieDetailFragment() {}

    public static MovieDetailFragment newInstance(MovieDetailItem movieDetailItem) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setMovieDetailItem(movieDetailItem);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        updateMovieDetailView(movieDetailItem);

        return view;
    }

    private void setMovieDetailItem(MovieDetailItem movieDetailItem) {
        this.movieDetailItem = movieDetailItem;
    }

    public void updateMovieDetailView(MovieDetailItem movieDetailItem) {
        if (movieDetailItem != null) {
            setMovieDetailItem(movieDetailItem);

            titleView.setText(movieDetailItem.getTitle());
            releaseDateView.setText(movieDetailItem.getReleaseDate());
            ratingView.setText(movieDetailItem.getRating());
            synopsisView.setText(movieDetailItem.getSynopsis());

            Picasso.with(getActivity()).load(movieDetailItem.getPosterUrl()).into(posterView);
        }
    }
}