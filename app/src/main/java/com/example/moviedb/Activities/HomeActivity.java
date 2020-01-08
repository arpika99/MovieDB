package com.example.moviedb.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviedb.Adapters.MoviesAdapter;
import com.example.moviedb.Classes.Genre.Genre;
import com.example.moviedb.Classes.Movie.Movie;
import com.example.moviedb.Classes.Movie.MoviesRepository;
import com.example.moviedb.Fragments.FavoritesFragment;
import com.example.moviedb.Fragments.NowPlayingFragment;
import com.example.moviedb.Fragments.ProfileFragment;
import com.example.moviedb.Interfaces.OnGetGenresCallback;
import com.example.moviedb.Interfaces.OnGetMoviesCallback;
import com.example.moviedb.Interfaces.OnMoviesClickCallback;
import com.example.moviedb.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

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

    private String sortBy = MoviesRepository.POPULAR;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        BottomNavigationView navigation = findViewById(R.id.navigationBar);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        moviesRepository = MoviesRepository.getInstance();
        moviesList = findViewById(R.id.movies_list);

        setupOnScrollListener();

        getGenres();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    actionBar.setTitle("Home");
//                    loadFragment(new HomeActivity());
//                    return true;
                case R.id.navigation_profile:
                    actionBar.setTitle("Profile");
                    loadFragment(new ProfileFragment());
                    return true;
                case R.id.navigation_favorites:
                    actionBar.setTitle("Favorites");
                    loadFragment(new FavoritesFragment());
                    return true;
                case R.id.navigation_now_playing:
                    actionBar.setTitle("Now Playing");
                    loadFragment(new NowPlayingFragment());
                    return true;
            }
            return false;
        }
    };

    /**
     * Loads the next selected fragment from the bottom actionbar
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Shows the sort icon on top right
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movies, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Options menu for the sorting
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort:
                showSortMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortMenu() {
        PopupMenu sortMenu = new PopupMenu(this, findViewById(R.id.sort));
        sortMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Every time we sort we go back to the first page
                currentPage = 1;
                switch (item.getItemId()) {
                    case R.id.popular:
                        sortBy = MoviesRepository.POPULAR;
                        getMovies(currentPage);
                        return true;
                    case R.id.top_rated:
                        sortBy = MoviesRepository.TOP_RATED;
                        getMovies(currentPage);
                        return true;
                    case R.id.upcoming:
                        sortBy = MoviesRepository.UPCOMING;
                        getMovies(currentPage);
                        return true;
                    default:
                        return false;
                }
            }
        });
        sortMenu.inflate(R.menu.menu_movies_sort);
        sortMenu.show();
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
        moviesRepository.getMovies(page, sortBy, new OnGetMoviesCallback() {
            @Override
            public void onSuccess(int page, List<Movie> movies) {
                Log.d("MoviesRepository", "Current Page = " + page);
                /**
                 * if adapter == null then we initialize it
                 * else append the movies that we receive from the page that we specified
                 */
                if (adapter == null) {
                    adapter = new MoviesAdapter(movies, movieGenres, callback);
                    moviesList.setAdapter(adapter);
                } else {
                    if(page == 1){
                        adapter.clearMovies();
                    }
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

                setTitle();
            }

            @Override
            public void onError() {
                showError();
            }
        });
    }

    OnMoviesClickCallback callback = new OnMoviesClickCallback() {
        @Override
        public void onClick(Movie movie) {
            Intent intent = new Intent(HomeActivity.this, MovieActivity.class);
            intent.putExtra(MovieActivity.MOVIE_ID, movie.getId());
            startActivity(intent);
        }
    };

    private void setTitle() {
        switch (sortBy) {
            case MoviesRepository.POPULAR:
                setTitle(getString(R.string.popular));
                break;
            case MoviesRepository.TOP_RATED:
                setTitle(getString(R.string.top_rated));
                break;
            case MoviesRepository.UPCOMING:
                setTitle(getString(R.string.upcoming));
                break;
        }
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
        Toast.makeText(HomeActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
    }
}
