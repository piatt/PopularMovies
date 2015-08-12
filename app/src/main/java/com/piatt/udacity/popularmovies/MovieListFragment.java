package com.piatt.udacity.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieListFragment extends Fragment {
    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    @Bind(R.id.list_grid) GridView gridView;

    //private MovieListService movieListService = null;

    public MovieListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (gridView == null) {
            View view = inflater.inflate(R.layout.fragment_list, container, false);
            ButterKnife.bind(this, view);

            gridView.setAdapter(new MovieListAdapter(getActivity()));
            new MovieListService(getActivity(), (MovieListAdapter) gridView.getAdapter());
        }

        return gridView;
    }
}