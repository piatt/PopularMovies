package com.piatt.udacity.popularmovies;

import android.app.Application;
import android.content.Context;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PopularMoviesApplication extends Application {
    private static PopularMoviesApplication application;

    public static PopularMoviesApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}