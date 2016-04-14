package com.piatt.udacity.popularmovies;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.piatt.udacity.popularmovies.event.EventBusUnregisterEvent;
import com.piatt.udacity.popularmovies.event.MoviesUpdateEvent;
import com.piatt.udacity.popularmovies.fragment.DetailFragment;
import com.piatt.udacity.popularmovies.fragment.MoviesFragment;
import com.piatt.udacity.popularmovies.manager.ContextManager;
import com.piatt.udacity.popularmovies.util.Constants;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class LaunchActivity extends AppCompatActivity {
    private static final String LOG_TAG = LaunchActivity.class.getSimpleName();

    @Bind(R.id.back_button) ImageView backButton;
    @Bind(R.id.title_view) TextView titleView;
    @Bind(R.id.sort_spinner) Spinner sortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextManager.getInstance().initialize(this);
        setRequestedOrientation(ContextManager.getInstance().isLargeLayout() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.launch_activity);
        ButterKnife.bind(this);
        ContextManager.getInstance().startFragment(Constants.FRAGMENT_TAG_LIST);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().post(new EventBusUnregisterEvent());
        super.onDestroy();
    }

    public void startFragment(String tag, Object object) {
        Fragment fragment = getFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            switch (tag) {
                case Constants.FRAGMENT_TAG_LIST: fragment = new MoviesFragment();
                    break;
                case Constants.FRAGMENT_TAG_DETAIL: fragment = DetailFragment.newInstance((int) object);
                    break;
            }
        }
        if (fragment != null) {
            Log.d(LOG_TAG, "Starting " + tag);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, tag).addToBackStack(tag).commit();
            updateToolbar(tag);
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStackImmediate();
            String tag = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName();
            updateToolbar(tag);
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

    private void updateToolbar(String tag) {
        backButton.setVisibility(tag.equals(Constants.FRAGMENT_TAG_LIST) ? View.GONE : View.VISIBLE);
        titleView.setVisibility(tag.equals(Constants.FRAGMENT_TAG_LIST) ? View.VISIBLE : View.INVISIBLE);
        sortSpinner.setVisibility(tag.equals(Constants.FRAGMENT_TAG_LIST) ? View.VISIBLE : View.INVISIBLE);
    }
}