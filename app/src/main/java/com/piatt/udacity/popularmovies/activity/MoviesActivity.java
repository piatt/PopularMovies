package com.piatt.udacity.popularmovies.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.event.EventBusUnregisterEvent;
import com.piatt.udacity.popularmovies.event.MovieMessageEvent;
import com.piatt.udacity.popularmovies.event.MovieSelectEvent;
import com.piatt.udacity.popularmovies.fragment.MessageFragment;
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
    protected void onDestroy() {
        EventBus.getDefault().post(new EventBusUnregisterEvent());
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Subscribe
    public void onMovieSelect(MovieSelectEvent event) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction().replace(R.id.fragment_container, MovieFragment.newInstance(event.getMovieId()));
        if (!MoviesApplication.getApp().isLargeLayout()) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Subscribe
    public void onMovieMessage(MovieMessageEvent event) {
        MessageFragment.newInstance(event.getMessageType()).show(getFragmentManager(), MessageFragment.class.getSimpleName());
    }
}