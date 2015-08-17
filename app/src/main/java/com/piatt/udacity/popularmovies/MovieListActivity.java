package com.piatt.udacity.popularmovies;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

public class MovieListActivity extends Activity {
    private static final String LOG_TAG = MovieListActivity.class.getSimpleName();

    private String LIST_FRAGMENT_TAG;
    private String DETAIL_FRAGMENT_TAG;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LIST_FRAGMENT_TAG = getString(R.string.list_fragment_tag);
        DETAIL_FRAGMENT_TAG = getString(R.string.detail_fragment_tag);
        fragmentManager = getFragmentManager();

        if (fragmentManager.findFragmentByTag(LIST_FRAGMENT_TAG) == null) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new MovieListFragment(), LIST_FRAGMENT_TAG).commit();
        }
    }

    public void viewMovieDetails(MovieDetailItem movieDetailItem) {
        MovieDetailFragment movieDetailFragment = (MovieDetailFragment) fragmentManager.findFragmentByTag(DETAIL_FRAGMENT_TAG);

        if (!isDualPane()) {
            Log.d(LOG_TAG, "PHONE - Starting new MovieDetailFragment");
            fragmentManager.beginTransaction().replace(R.id.fragment_container, MovieDetailFragment.newInstance(movieDetailItem), DETAIL_FRAGMENT_TAG).addToBackStack(DETAIL_FRAGMENT_TAG).commit();
        } else if (isDualPane() && movieDetailFragment != null) {
            Log.d(LOG_TAG, "TABLET - Updating existing MovieDetailFragment");
            movieDetailFragment.updateMovieDetailView(movieDetailItem);
        }
    }

    public boolean isDualPane() {
        return findViewById(R.id.detail_fragment) != null;
    }
}