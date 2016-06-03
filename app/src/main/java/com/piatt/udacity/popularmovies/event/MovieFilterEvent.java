package com.piatt.udacity.popularmovies.event;

import com.piatt.udacity.popularmovies.model.MovieFilter;

import lombok.Getter;
import lombok.Setter;

public class MovieFilterEvent {
    @Getter @Setter private MovieFilter movieFilter;

    public MovieFilterEvent(int position) {
        switch (position) {
            case 0: setMovieFilter(MovieFilter.POPULAR);
                break;
            case 1: setMovieFilter(MovieFilter.TOP_RATED);
                break;
            case 2: setMovieFilter(MovieFilter.FAVORITES);
                break;
        }
    }

    public MovieFilterEvent(MovieFilter movieFilter) {
        setMovieFilter(movieFilter);
    }
}