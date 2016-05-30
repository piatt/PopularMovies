package com.piatt.udacity.popularmovies;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.piatt.udacity.popularmovies.manager.ApiManager;
import com.piatt.udacity.popularmovies.manager.PreferencesManager;

import lombok.Getter;

public class MoviesApplication extends Application {
    private final String LOG_TAG = getClass().getSimpleName();

    private ConnectivityManager connectivityManager;
    @Getter private ApiManager apiManager;
    @Getter private PreferencesManager preferencesManager;
    @Getter private static MoviesApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        apiManager = new ApiManager();
        preferencesManager = new PreferencesManager();
    }

    public boolean isLargeLayout() {
        return getResources().getBoolean(R.bool.large_layout);
    }

    public void showErrorMessage(String errorMessage) {
        Log.e(LOG_TAG, errorMessage);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isNetworkAvailable = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        String message = isNetworkAvailable ? getString(R.string.error_generic) : getString(R.string.error_network);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}