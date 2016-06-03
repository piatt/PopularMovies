package com.piatt.udacity.popularmovies.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.adapter.MovieListingsAdapter;
import com.piatt.udacity.popularmovies.event.MoviesUpdateEvent;
import com.piatt.udacity.popularmovies.model.MovieFilter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;

public class MoviesFragment extends Fragment {
    @BindView(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.sort_spinner) Spinner sortSpinner;
    @BindView(R.id.movie_list) RecyclerView movieList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MoviesUpdateEvent moviesUpdateEvent = new MoviesUpdateEvent(sortSpinner.getSelectedItemPosition());
        if (moviesUpdateEvent.getMovieFilter().equals(MovieFilter.FAVORITES)) {
            EventBus.getDefault().post(moviesUpdateEvent);
        }
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
            movieList.setHasFixedSize(true);
            movieList.setLayoutManager(new GridLayoutManager(movieList.getContext(), MoviesApplication.getApp().isLargeLayout() ? 4 : 2));
            movieList.setAdapter(new MovieListingsAdapter());
        }
        return coordinatorLayout;
    }

    @Subscribe
    public void updateMovieSort(MoviesUpdateEvent event) {
        movieList.getLayoutManager().scrollToPosition(0);
    }

    @OnItemSelected(R.id.sort_spinner)
    public void onItemSelected(int position) {
        EventBus.getDefault().post(new MoviesUpdateEvent(position));
    }
}