package com.piatt.udacity.popularmovies.event;

public class DetailUpdateEvent {
    public int movieId;

    public DetailUpdateEvent(int movieId) {
        this.movieId = movieId;
    }
}