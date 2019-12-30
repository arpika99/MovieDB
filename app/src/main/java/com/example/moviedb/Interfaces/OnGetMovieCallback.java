package com.example.moviedb.Interfaces;

import com.example.moviedb.Classes.Movie.Movie;

public interface OnGetMovieCallback {
    void onSuccess(Movie movie);

    void onError();
}
