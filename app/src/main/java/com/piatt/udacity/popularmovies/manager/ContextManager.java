package com.piatt.udacity.popularmovies.manager;

import android.content.Context;
import android.util.Log;

import com.piatt.udacity.popularmovies.LaunchActivity;
import com.piatt.udacity.popularmovies.R;
import com.piatt.udacity.popularmovies.event.DetailUpdateEvent;
import com.piatt.udacity.popularmovies.util.Constants;

import de.greenrobot.event.EventBus;
import lombok.Getter;
import lombok.Setter;

public class ContextManager {
    private final String LOG_TAG = ContextManager.class.getSimpleName();

    @Getter @Setter private Context context;
    private static ContextManager contextManager = new ContextManager();

    public static ContextManager getInstance() {
        return contextManager;
    }

    /**
     * Called from the LaunchActivity on first launch, this method stores the application's context,
     * making it available to the rest of the application without needing to cast LaunchActivity or account for activity lifecycle changes.
     */
    public void initialize(Context context) {
        if (getContext() == null || !getContext().equals(context)) {
            Log.d(LOG_TAG, "Starting " + LOG_TAG);
            setContext(context);
        }
    }

    public boolean isLargeLayout() {
        return context.getResources().getBoolean(R.bool.large_layout);
    }

    public void startFragment(String tag) {
        startFragment(tag, null);
    }

    public void startFragment(String tag, Object object) {
        if (isLargeLayout()) {
            if (tag.equals(Constants.FRAGMENT_TAG_DETAIL)) {
                Log.d(LOG_TAG, "Large layout, updating " + tag);
                EventBus.getDefault().post(new DetailUpdateEvent((int) object));
            } else if (tag.equals(Constants.FRAGMENT_TAG_LIST)) {
                Log.d(LOG_TAG, "Large layout, " + tag + " already exists");
            }
        } else {
            Log.d(LOG_TAG, "Normal layout, starting " + tag);
            ((LaunchActivity) context).startFragment(tag, object);
        }
    }

    public void onBackPressed() {
        ((LaunchActivity) context).onBackPressed();
    }
}