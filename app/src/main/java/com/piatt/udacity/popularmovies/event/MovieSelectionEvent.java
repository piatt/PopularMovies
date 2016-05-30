package com.piatt.udacity.popularmovies.event;

import lombok.Getter;
import lombok.Setter;

public class MovieSelectionEvent {
    @Getter @Setter private int movieId;

    public MovieSelectionEvent(int movieId) {
        setMovieId(movieId);
    }
}