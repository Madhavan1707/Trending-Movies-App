package com.example.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import javax.inject.Inject;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class BookmarksActivity extends AppCompatActivity {

    @Inject
    MovieRepository movieRepository;

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private TextView textViewNoBookmarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        // Inject dependencies
        ((MainApplication) getApplication()).getAppComponent().inject(this);

        // Find views
        textViewNoBookmarks = findViewById(R.id.textViewNoBookmarks);
        recyclerView = findViewById(R.id.recyclerViewBookmarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the adapter
        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        // Add click listener to launch the movie details activity when a bookmarked movie is tapped
        movieAdapter.setOnItemClickListener(movie -> {
            Intent intent = new Intent(BookmarksActivity.this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.id);
            startActivity(intent);
        });

        // Fetch bookmarked movies and update UI accordingly
        movieRepository.getBookmarkedMovies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (movies == null || movies.isEmpty()) {
                        textViewNoBookmarks.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        textViewNoBookmarks.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        movieAdapter.updateMovies(movies);
                    }
                }, throwable -> {
                    // Optionally handle the error (e.g., show a Toast)
                });
    }
}
