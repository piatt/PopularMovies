package com.piatt.udacity.popularmovies.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {
    private final String LOG_TAG = NetworkManager.class.getSimpleName();

    private ConnectivityManager connectivityManager;
    private static NetworkManager networkManager = new NetworkManager();

    public static NetworkManager getInstance() {
        return networkManager;
    }

    public NetworkManager() {
        connectivityManager = (ConnectivityManager) ContextManager.getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isNetworkAvailable() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}