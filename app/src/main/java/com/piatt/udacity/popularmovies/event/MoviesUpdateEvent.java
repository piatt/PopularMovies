package com.piatt.udacity.popularmovies.event;

import lombok.Getter;
import lombok.Setter;

public class MoviesUpdateEvent {
    @Getter @Setter private int sortPosition;

    public MoviesUpdateEvent(int sortPosition) {
        setSortPosition(sortPosition);
    }
}