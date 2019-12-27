package com.example.moviedb.Interfaces;

import com.example.moviedb.Classes.Movie;

import java.util.List;

public interface OnGetMoviesCallback {

    void onSuccess(int page, List<Movie> movies);

    void onError();
}
