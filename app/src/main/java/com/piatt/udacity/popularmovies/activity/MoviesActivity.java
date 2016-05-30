package com.piatt.udacity.popularmovies.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.piatt.udacity.popularmovies.MoviesApplication;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.event.EventBusUnregisterEvent;
import com.piatt.udacity.popularmovies.event.MovieSelectionEvent;
import com.piatt.udacity.popularmovies.event.MoviesUpdateEvent;
import com.piatt.udacity.popularmovies.fragment.MovieFragment;
import com.piatt.udacity.popularmovies.fragment.MoviesFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Unbinder;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MoviesActivity extends AppCompatActivity {
    @BindView(R.id.back_button) ImageView backButton;
    @BindView(R.id.title_view) TextView titleView;
    @BindView(R.id.sort_spinner) Spinner sortSpinner;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(MoviesApplication.getApp().isLargeLayout() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.movies_activity);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        updateToolbar(MoviesFragment.class.getSimpleName());
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
        EventBus.getDefault().post(new EventBusUnregisterEvent());
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 1) {
            getFragmentManager().popBackStackImmediate();
            updateToolbar(MoviesFragment.class.getSimpleName());
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.back_button)
    public void onClick() {
        onBackPressed();
    }

    @OnItemSelected(R.id.sort_spinner)
    public void onItemSelected(int position) {
        EventBus.getDefault().post(new MoviesUpdateEvent(position));
    }

    @Subscribe
    public void updateMovieFragment(MovieSelectionEvent event) {
        if (!MoviesApplication.getApp().isLargeLayout()) {
            updateToolbar(MovieFragment.class.getSimpleName());
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, MovieFragment.newInstance(event.getMovieId())).addToBackStack(MovieFragment.class.getSimpleName()).commit();
        }
    }

    private void updateToolbar(String fragmentTag) {
        boolean isMoviesFragment = fragmentTag.equals(MoviesFragment.class.getSimpleName());
        backButton.setVisibility(isMoviesFragment ? View.GONE : View.VISIBLE);
        titleView.setVisibility(isMoviesFragment ? View.VISIBLE : View.GONE);
        sortSpinner.setVisibility(isMoviesFragment ? View.VISIBLE : View.GONE);
    }
}