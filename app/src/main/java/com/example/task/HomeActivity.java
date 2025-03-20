package com.example.task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import javax.inject.Inject;

public class HomeActivity extends AppCompatActivity {

    @Inject
    MovieRepository movieRepository;
    private MovieViewModel movieViewModel;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private MaterialToolbar topAppBar;
    private SwipeRefreshLayout swipeRefreshLayout; // New: SwipeRefreshLayout reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ((MainApplication) getApplication()).getAppComponent().inject(this);

        // Set up the top app bar
        topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Trending Movies");
        }

        // Get reference to SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d("HOMEACTIVITY", "Pull-to-refresh triggered");
            // On swipe down, re-fetch trending movies
            movieViewModel.fetchTrendingMovies();
        });

        recyclerView = findViewById(R.id.recyclerViewMovies);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(16));

        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        movieAdapter.setOnItemClickListener(movie -> {
            Intent intent = new Intent(HomeActivity.this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.id);
            startActivity(intent);
        });

        movieViewModel = new MovieViewModel(movieRepository);
        movieViewModel.getTrendingMovies().observe(this, movies -> {
            if (movies != null && !movies.isEmpty()) {
                movieAdapter.updateMovies(movies);
                Log.d("HA", "New data retrieved: " + movies.size() + " movies");
            }
            else{
                Log.d("HA", "No new movies retrieved");
            }
            // Stop the refresh animation when data is loaded
            if (swipeRefreshLayout.isRefreshing()) {
                Log.d("HA", "closing refresh animation when data is loaded");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        movieViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                // Stop refreshing even on error
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        } else if (id == R.id.action_bookmarks) {
            startActivity(new Intent(this, BookmarksActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
