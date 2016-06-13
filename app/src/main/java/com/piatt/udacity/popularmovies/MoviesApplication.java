package com.piatt.udacity.popularmovies;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.piatt.udacity.popularmovies.manager.ApiManager;
import com.piatt.udacity.popularmovies.manager.FavoritesManager;

import lombok.Getter;

public class MoviesApplication extends Application {
    private ConnectivityManager connectivityManager;
    @Getter private ApiManager apiManager;
    @Getter private FavoritesManager favoritesManager;
    @Getter private static MoviesApplication app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        apiManager = new ApiManager();
        favoritesManager = new FavoritesManager();
    }

    public boolean isLargeLayout() {
        return getResources().getBoolean(R.bool.large_layout);
    }

    public boolean isNetworkAvailable() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}