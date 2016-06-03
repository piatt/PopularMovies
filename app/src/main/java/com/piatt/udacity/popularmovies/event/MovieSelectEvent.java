package com.piatt.udacity.popularmovies.event;

import lombok.Getter;
import lombok.Setter;

public class MovieSelectEvent {
    @Getter @Setter private int movieId;

    public MovieSelectEvent(int movieId) {
        setMovieId(movieId);
    }
}