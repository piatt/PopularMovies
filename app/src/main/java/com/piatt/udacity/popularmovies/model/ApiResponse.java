package com.piatt.udacity.popularmovies.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ApiResponse<T> {
    @Getter @Setter private List<T> results;
}