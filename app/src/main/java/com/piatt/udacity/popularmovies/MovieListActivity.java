package com.piatt.udacity.popularmovies;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

public class MovieListActivity extends Activity {
    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    private String LIST_FRAGMENT_TAG;
    private String DETAIL_FRAGMENT_TAG;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MovieListService.init(this);

        setContentView(R.layout.activity_main);

        LIST_FRAGMENT_TAG = getString(R.string.list_fragment_tag);
        DETAIL_FRAGMENT_TAG = getString(R.string.detail_fragment_tag);

        fragmentManager = getFragmentManager();

        if (fragmentManager.findFragmentByTag(LIST_FRAGMENT_TAG) == null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new MovieListFragment(), LIST_FRAGMENT_TAG).commit();
        }
    }

    /**
     * Using a single activity, the master/detail flow is attained by checking for the existence of a fragment only available in xml
     * in a layout specific to tablets. If it does not exist, the new fragment must be started to replace the existing one.
     * If that layout exists, the fragment is visible and its content can simply be updated.
     */
    public void viewMovieDetails(int movieId) {
        MovieDetailFragment movieDetailFragment = (MovieDetailFragment) fragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if (!MovieListService.isDualPane()) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, MovieDetailFragment.newInstance(movieId), DETAIL_FRAGMENT_TAG)
                    .addToBackStack(DETAIL_FRAGMENT_TAG).commit();
        } else if (MovieListService.isDualPane() && movieDetailFragment != null) {
            movieDetailFragment.updateMovieDetailView(movieId);
        }
    }
}