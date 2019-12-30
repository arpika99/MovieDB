package com.example.moviedb.Interfaces;

import com.example.moviedb.Classes.Genre.Genre;

import java.util.List;

public interface OnGetGenresCallback {

    void onSuccess(List<Genre> genres);

    void onError();
}
