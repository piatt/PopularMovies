package com.piatt.udacity.popularmovies;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

public class MovieListFragment extends Fragment implements OnItemClickListener, OnItemSelectedListener {
    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    @Bind(R.id.list_grid) GridView gridView;
    @BindString(R.string.sort_pref) String sortPreference;
    private Spinner spinnerView;

    public MovieListFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (gridView == null) {
            View view = inflater.inflate(R.layout.fragment_list, container, false);
            ButterKnife.bind(this, view);

            gridView.setAdapter(new MovieListAdapter(getActivity()));
            gridView.setOnItemClickListener(this);

            MovieListService.getMovieData(getActivity(), gridView, MovieListService.getCurrentSort());
        }

        return gridView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);
        spinnerView = (Spinner) menu.findItem(R.id.action_sort).getActionView();
        spinnerView.setSelection(MovieListService.getCurrentSort());
        spinnerView.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MovieListService.setCurrentMovieDetailItem((MovieDetailItem) view.getTag());
        ((MovieListActivity) getActivity()).viewMovieDetails((MovieDetailItem) view.getTag());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view != null && position != MovieListService.getCurrentSort()) {
            MovieListService.setCurrentSort(position);
            MovieListService.getMovieData(getActivity(), gridView, position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}