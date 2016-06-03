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
    private final String PREFERENCES_SORT_FILTER = "SORT_FILTER";
    private final String PREFERENCES_ITEM_POSITION = "ITEM_POSITION";
    private final String FAVORITES_KEY = "FAVORITES";
    private SharedPreferences sharedPreferences;

    public FavoritesManager() {
        sharedPreferences = MoviesApplication.getApp().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

//    public int getCurrentMovieItem() {
//        return sharedPreferences.getInt(PREFERENCES_ITEM_POSITION, 0);
//    }
//
//    public void setCurrentMovieItem(int itemPosition) {
//        sharedPreferences.edit().putInt(PREFERENCES_ITEM_POSITION, itemPosition).commit();
//    }
//
//    public String getCurrentSortFilter() {
//        return sharedPreferences.getString(PREFERENCES_SORT_FILTER, API_POPULARITY_ENDPOINT);
//    }
//
//    public int getCurrentSortFilterPosition() {
//        String sortFilter = getCurrentSortFilter();
//        int sortFilterPosition = 0;
//
//        if (sortFilter.equals(API_POPULARITY_ENDPOINT)) {
//            sortFilterPosition = 0;
//        } else if (sortFilter.equals(API_RATING_ENDPOINT)) {
//            sortFilterPosition = 1;
//        } else if (sortFilter.equals(API_FAVORITES_ENDPOINT)) {
//            sortFilterPosition = 2;
//        }
//
//        return sortFilterPosition;
//    }
//
//    public void setCurrentSortFilter(int sortPosition) {
//        String sortFilter;
//
//        switch (sortPosition) {
//            case 0: sortFilter = API_POPULARITY_ENDPOINT; break;
//            case 1: sortFilter = API_RATING_ENDPOINT; break;
//            case 2: sortFilter = API_FAVORITES_ENDPOINT; break;
//            default: sortFilter = API_POPULARITY_ENDPOINT; break;
//        }
//
//        sharedPreferences.edit().putString(PREFERENCES_SORT_FILTER, sortFilter).commit();
//    }
//
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