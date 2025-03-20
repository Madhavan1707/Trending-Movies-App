package com.example.task;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class BookmarksActivity extends AppCompatActivity {

    @Inject
    MovieRepository movieRepository;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private TextView textViewNoBookmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        // Inject dependencies
        ((MainApplication) getApplication()).getAppComponent().inject(this);

        // Find views (note that swipeRefreshLayout must be present in the XML)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        textViewNoBookmarks = findViewById(R.id.textViewNoBookmarks);
        recyclerView = findViewById(R.id.recyclerViewBookmarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        // Set up the pull-to-refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d("BookmarksActivity", "Swipe-to-refresh triggered");
            loadBookmarkedMovies();
        });

        // Load bookmarks initially
        loadBookmarkedMovies();

        // Add click listener to launch the movie details activity when a bookmarked movie is tapped
        movieAdapter.setOnItemClickListener(movie -> {
            Intent intent = new Intent(BookmarksActivity.this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.id);
            startActivity(intent);
        });
    }

    private void loadBookmarkedMovies() {
        movieRepository.getBookmarkedMovies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    swipeRefreshLayout.setRefreshing(false);
                    if (movies == null || movies.isEmpty()) {
                        textViewNoBookmarks.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        Log.d("BookmarksActivity", "No bookmarked movies found");
                    } else {
                        textViewNoBookmarks.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        movieAdapter.updateMovies(movies);
                        Log.d("BookmarksActivity", "Loaded " + movies.size() + " bookmarked movies");
                    }
                }, throwable -> {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(this, "Error loading bookmarks", Toast.LENGTH_SHORT).show();
                    Log.e("BookmarksActivity", "Error loading bookmarks", throwable);
                });
    }
}
