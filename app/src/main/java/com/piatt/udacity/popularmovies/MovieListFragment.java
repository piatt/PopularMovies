package com.piatt.udacity.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieListFragment extends Fragment implements OnItemClickListener {
    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    @Bind(R.id.list_grid) GridView gridView;

    public MovieListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (gridView == null) {
            View view = inflater.inflate(R.layout.fragment_list, container, false);
            ButterKnife.bind(this, view);

            gridView.setAdapter(new MovieListAdapter(getActivity()));
            gridView.setOnItemClickListener(this);
            MovieListService.getMovieData(getActivity(), gridView);
        }

        return gridView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MovieListService.setCurrentMovieDetailItem((MovieDetailItem) view.getTag());
        ((MovieListActivity) getActivity()).viewMovieDetails((MovieDetailItem) view.getTag());
    }
}