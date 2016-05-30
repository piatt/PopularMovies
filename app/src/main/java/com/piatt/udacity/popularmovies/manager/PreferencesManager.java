package com.piatt.udacity.popularmovies.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.piatt.udacity.popularmovies.MoviesApplication;

public class PreferencesManager {
    private final String PREFERENCES_TAG = "POPULAR_MOVIES_PREFERENCES";
    private final String PREFERENCES_SORT_FILTER = "SORT_FILTER";
    private final String PREFERENCES_ITEM_POSITION = "ITEM_POSITION";
    private final String PREFERENCES_FAVORITES = "FAVORITES";
    private SharedPreferences sharedPreferences;

    public PreferencesManager() {
        sharedPreferences = MoviesApplication.getApp().getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE);
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
//    public ArrayList<Integer> getCurrentFavorites() {
//        ArrayList<Integer> favorites = new ArrayList<>();
//        Set<String> storedFavorites = sharedPreferences.getStringSet(PREFERENCES_FAVORITES, new HashSet<String>());
//        for (String favorite : storedFavorites) {
//            favorites.add(Integer.parseInt(favorite));
//        }
//        return favorites;
//    }
//
//    public void addFavorite(int favorite) {
//        Set<String> storedFavorites = sharedPreferences.getStringSet(PREFERENCES_FAVORITES, new HashSet<String>());
//        storedFavorites.add(String.valueOf(favorite));
//        sharedPreferences.edit().putStringSet(PREFERENCES_FAVORITES, storedFavorites).commit();
//    }
//
//    public void removeFavorite(int favorite) {
//        Set<String> storedFavorites = sharedPreferences.getStringSet(PREFERENCES_FAVORITES, new HashSet<String>());
//        storedFavorites.remove(String.valueOf(favorite));
//        sharedPreferences.edit().putStringSet(PREFERENCES_FAVORITES, storedFavorites).commit();
//    }
//
//    public boolean isFavorite(int movieId) {
//        return getCurrentFavorites().contains(Integer.valueOf(movieId));
//    }
}