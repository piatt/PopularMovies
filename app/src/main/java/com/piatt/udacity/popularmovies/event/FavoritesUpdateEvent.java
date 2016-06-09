package com.piatt.udacity.popularmovies.event;

import lombok.Getter;
import lombok.Setter;

public class FavoritesUpdateEvent {
    @Getter @Setter private int movieId;
    @Getter @Setter private boolean favorite;

    public FavoritesUpdateEvent(int movieId, boolean favorite) {
        setMovieId(movieId);
        setFavorite(favorite);
    }
}