package com.example.moviedb.Interfaces;

import com.example.moviedb.Classes.Trailer.Trailer;

import java.util.List;

public interface OnGetTrailersCallback {
    void onSuccess(List<Trailer> trailers);

    void onError();
}
