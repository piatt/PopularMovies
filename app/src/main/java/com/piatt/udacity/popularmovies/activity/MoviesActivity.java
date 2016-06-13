package com.piatt.udacity.popularmovies.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.event.MovieMessageEvent;
import com.piatt.udacity.popularmovies.event.MovieSelectEvent;
import com.piatt.udacity.popularmovies.fragment.MessageFragment;
import com.piatt.udacity.popularmovies.fragment.MovieFragment;
import com.piatt.udacity.popularmovies.fragment.MoviesFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MoviesActivity extends AppCompatActivity {
    /**
     * This app uses a "single activity with multiple fragments" architecture.
     * On phones, the screen is locked to portrait and replaces the activity's container view
     * with either the list or the detail fragment, dependent on the circumstance.
     * On tablets, the screen is locked to landscape and uses both fragments side by side.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(MoviesApplication.getApp().isLargeLayout() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.movies_activity);
        EventBus.getDefault().register(this);
        if (!MoviesApplication.getApp().isLargeLayout()) {
            getFragmentManager().beginTransaction().add(R.id.fragment_container, new MoviesFragment()).commit();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    /**
     * This event handler is invoked when a movie listing is selected either by the user
     * or when the first listing is chosen on app launch or on movie filter change.
     * A new instance of the detail screen is created and replaces the entire screen on phones,
     * or replaces only the right half of the screen on tablets.
     */
    @Subscribe
    public void onMovieSelect(MovieSelectEvent event) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.fragment_container, MovieFragment.newInstance(event.getMovieId()));
        if (!MoviesApplication.getApp().isLargeLayout()) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    /**
     * This event handler is invoked when any part of the app requests that a special purpose dialog be shown.
     * In this app, it is used to alert the user to possible network connection issues,
     * or to let the user know how to add favorites.
     */
    @Subscribe
    public void onMovieMessage(MovieMessageEvent event) {
        MessageFragment.newInstance(event.getMessageType()).show(getFragmentManager(), MessageFragment.class.getSimpleName());
    }
}