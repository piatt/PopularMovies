package com.piatt.udacity.popularmovies.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.event.EventBusUnregisterEvent;
import com.piatt.udacity.popularmovies.event.MovieSelectionEvent;
import com.piatt.udacity.popularmovies.fragment.MovieFragment;
import com.piatt.udacity.popularmovies.fragment.MoviesFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MoviesActivity extends AppCompatActivity {
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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onDestroy() {
        Log.d(getClass().getSimpleName(), "EventBusUnregisterEvent");
        EventBus.getDefault().post(new EventBusUnregisterEvent());
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 1) {
            getFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe
    public void updateMovieFragment(MovieSelectionEvent event) {
        if (!MoviesApplication.getApp().isLargeLayout()) {
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, MovieFragment.newInstance(event.getMovieId())).addToBackStack(MovieFragment.class.getSimpleName()).commit();
        }
    }
}