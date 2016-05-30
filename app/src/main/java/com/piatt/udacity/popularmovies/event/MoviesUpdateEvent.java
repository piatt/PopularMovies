package com.piatt.udacity.popularmovies.event;

import com.piatt.udacity.popularmovies.model.MovieFilter;

import lombok.Getter;
import lombok.Setter;

public class MoviesUpdateEvent {
    @Getter @Setter private MovieFilter movieFilter;

    public MoviesUpdateEvent(int position) {
        switch (position) {
            case 0: setMovieFilter(MovieFilter.POPULAR);
                break;
            case 1: setMovieFilter(MovieFilter.TOP_RATED);
                break;
            case 2: setMovieFilter(MovieFilter.FAVORITES);
                break;
        }
    }

    public MoviesUpdateEvent(MovieFilter movieFilter) {
        setMovieFilter(movieFilter);
    }
}