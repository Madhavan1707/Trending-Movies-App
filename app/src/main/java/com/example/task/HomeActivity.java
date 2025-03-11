package com.example.task;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Inject via Dagger
        ((MainApplication) getApplication()).getAppComponent().inject(this);

        // Set up top app bar
        topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);  // optional if you want default menu handling

        recyclerView = findViewById(R.id.recyclerViewMovies);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(8)); // 8px spacing


        // Use a 2-column grid
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        // Navigate to detail screen on item click
        movieAdapter.setOnItemClickListener(movie -> {
            Intent intent = new Intent(HomeActivity.this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.id);
            startActivity(intent);
        });

        // Set up ViewModel
        movieViewModel = new MovieViewModel(movieRepository);
        movieViewModel.getTrendingMovies().observe(this, movies -> {
            if (movies != null && !movies.isEmpty()) {
                movieAdapter.updateMovies(movies);
            }
        });
    }
}
