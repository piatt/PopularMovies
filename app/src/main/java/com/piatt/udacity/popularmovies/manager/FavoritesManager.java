package com.piatt.udacity.popularmovies.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.annimon.stream.Stream;
import com.piatt.udacity.popularmovies.MoviesApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesManager {
    private final String PREFERENCES_KEY = "MOVIES_PREFERENCES";
    private final String FILTER_KEY = "FILTER";
    private final String FAVORITES_KEY = "FAVORITES";
    private SharedPreferences sharedPreferences;

    public FavoritesManager() {
        sharedPreferences = MoviesApplication.getApp().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public int getMoviesFilter() {
        return sharedPreferences.getInt(FILTER_KEY, 0);
    }

    public void setMoviesFilter(int filter) {
        sharedPreferences.edit().putInt(FILTER_KEY, filter).commit();
    }

    public List<Integer> getFavoriteMovies() {
        List<Integer> favorites = new ArrayList<>();
        Set<String> storedFavorites = sharedPreferences.getStringSet(FAVORITES_KEY, new HashSet<>());
        Stream.of(storedFavorites).forEach(favorite -> favorites.add(Integer.parseInt(favorite)));
        return favorites;
    }

    public void addFavoriteMovie(int movieId) {
        Set<String> storedFavorites = sharedPreferences.getStringSet(FAVORITES_KEY, new HashSet<>());
        storedFavorites.add(String.valueOf(movieId));
        sharedPreferences.edit().putStringSet(FAVORITES_KEY, storedFavorites).commit();
    }

    public void removeFavoriteMovie(int movieId) {
        Set<String> storedFavorites = sharedPreferences.getStringSet(FAVORITES_KEY, new HashSet<>());
        storedFavorites.remove(String.valueOf(movieId));
        sharedPreferences.edit().putStringSet(FAVORITES_KEY, storedFavorites).commit();
    }

    public boolean isFavoriteMovie(int movieId) {
        return getFavoriteMovies().contains(Integer.valueOf(movieId));
    }
}