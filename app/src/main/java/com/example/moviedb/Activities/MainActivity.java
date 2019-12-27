package com.example.moviedb.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviedb.Adapters.MoviesAdapter;
import com.example.moviedb.Classes.Genre;
import com.example.moviedb.Classes.Movie;
import com.example.moviedb.Classes.MoviesRepository;
import com.example.moviedb.Interfaces.OnGetGenresCallback;
import com.example.moviedb.Interfaces.OnGetMoviesCallback;
import com.example.moviedb.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView moviesList;
    private MoviesAdapter adapter;
    private MoviesRepository moviesRepository;
    private List<Genre> movieGenres;

    /**
     * Flag that we will use to determine if we are currently fetching the next page.
     * Without this flag, if the we scrolled half above it will fetch the same page
     * multiple times and causes duplication.
     */
    private boolean isFetchingMovies;

    /**
     * Initialized to page 1.
     * Every time we scrolled half of the list we increment it by one which is the next page.
     */
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesRepository = MoviesRepository.getInstance();

        moviesList = findViewById(R.id.movies_list);

        setupOnScrollListener();

        getGenres();
    }

    /**
     * OnScrollListener is needed for evading pagination to the application
     * When we reach the half of the movies then we load the next page.
     */
    private void setupOnScrollListener() {
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        moviesList.setLayoutManager(manager);
        moviesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = manager.getItemCount();
                int visibleItemCount = manager.getChildCount();
                int firstVisibleItem = manager.findFirstVisibleItemPosition();

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    if (!isFetchingMovies) {
                        getMovies(currentPage + 1);
                    }
                }
            }
        });
    }

    private void getMovies(int page){
        /**
         * Set to true to stop fetching movies for the meantime when scrolling
         */
        isFetchingMovies = true;
        moviesRepository.getMovies(page, new OnGetMoviesCallback() {
            @Override
            public void onSuccess(int page, List<Movie> movies) {
                Log.d("MoviesRepository", "Current Page = " + page);
                /**
                 * if adapter == null then we initialize it
                 * else append the movies that we receive from the page that we specified
                 */
                if (adapter == null) {
                    adapter = new MoviesAdapter(movies, movieGenres);
                    moviesList.setAdapter(adapter);
                } else {
                    adapter.appendMovies(movies);
                }
                /**
                 * set currentPage = page which was next page that we requested
                 */
                currentPage = page;
                /**
                 * set isFetchingMovies to false to allow for fetching of movies again
                 */
                isFetchingMovies = false;
            }

            @Override
            public void onError() {
                showError();
            }
        });
    }

    private void getGenres() {
        moviesRepository.getGenres(new OnGetGenresCallback() {
            @Override
            public void onSuccess(List<Genre> genres) {
                movieGenres = genres;
                getMovies(currentPage);
            }

            @Override
            public void onError() {
                showError();
            }
        });
    }

    private void showError() {
        Toast.makeText(MainActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
    }
}
