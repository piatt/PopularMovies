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
import butterknife.ButterKnife;

public class MovieListFragment extends Fragment implements OnItemClickListener, OnItemSelectedListener {
    private static final String LOG_TAG = MovieListFragment.class.getSimpleName();

    @Bind(R.id.list_grid) GridView gridView;
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

            MovieListService.getMovieData(getActivity(), gridView);
        }

        return gridView;
    }

    /**
     * On options menu initialization, the spinner containing the sort filters is created
     * and it's default setting is set based on a stored preference, if it exists.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list, menu);
        spinnerView = (Spinner) menu.findItem(R.id.action_sort).getActionView();
        spinnerView.setSelection(MovieListService.getCurrentSortFilter());
        spinnerView.setOnItemSelectedListener(this);
    }

    /**
     * This handler is invoked when a movie poster item is clicked inside of the gridview.
     * The value of the item clicked is stored as a preference for future reference,
     * and the appropriate action is taken by the MovieListActivity to show the movie item's details.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        MovieListService.setCurrentMovieDetailItem((MovieDetailItem) view.getTag());
        MovieListService.setCurrentMovieDetailItem(position);
        ((MovieListActivity) getActivity()).viewMovieDetails((MovieDetailItem) view.getTag());
    }

    /**
     * This handler is invoked when the sort filter dropdown value changes.
     * Since this handler may be invoked after initial menu creation and on orientation changes,
     * in addition to user toggling of the sort filter, a request for movie data is only made
     * when the user has made the change and when it is determined that the new value is different from the current stored preference.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view != null && position != MovieListService.getCurrentSortFilter()) {
            MovieListService.setCurrentSortFilter(position);
            MovieListService.getMovieData(getActivity(), gridView);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}