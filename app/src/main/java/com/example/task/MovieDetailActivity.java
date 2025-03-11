package com.example.task;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "movie_id";

    private ImageView imageViewPoster;
    private TextView textViewTitle;
    private TextView textViewOverview;
    private com.google.android.material.button.MaterialButton buttonBookmark;
    private MaterialToolbar topAppBar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private MovieRepository movieRepository;
    private Movie currentMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        imageViewPoster = findViewById(R.id.imageViewPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOverview = findViewById(R.id.textViewOverview);
        buttonBookmark = findViewById(R.id.buttonBookmark);
        topAppBar = findViewById(R.id.topAppBarDetail);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);

        // Setup the toolbar
        setSupportActionBar(topAppBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        // Retrieve repository
        movieRepository = ((MainApplication) getApplication())
                .getAppComponent().injectRepository();

        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        if (movieId == -1) {
            Toast.makeText(this, "Movie ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load movie data
        movieRepository.getMovieById(movieId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movie -> {
                    currentMovie = movie;
                    updateUI();
                }, throwable -> {
                    Toast.makeText(this, "Error loading movie details", Toast.LENGTH_SHORT).show();
                });

        // Bookmark toggle
        buttonBookmark.setOnClickListener(v -> {
            if (currentMovie != null) {
                boolean newStatus = !currentMovie.bookmarked;
                movieRepository.updateBookmarkStatus(currentMovie.id, newStatus);
                currentMovie.bookmarked = newStatus;
                updateBookmarkButton();
                Toast.makeText(
                        this,
                        newStatus ? "Bookmarked" : "Bookmark removed",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void updateUI() {
        textViewTitle.setText(currentMovie.title);
        textViewOverview.setText(currentMovie.overview);
        updateBookmarkButton();

        // Collapsing Toolbar Title
        collapsingToolbarLayout.setTitle(currentMovie.title);

        // Poster Image
        String posterUrl = "https://image.tmdb.org/t/p/w500" + currentMovie.posterPath;
        Glide.with(this).load(posterUrl).into(imageViewPoster);
    }

    private void updateBookmarkButton() {
        if (currentMovie.bookmarked) {
            buttonBookmark.setText("Unbookmark");
        } else {
            buttonBookmark.setText("Bookmark");
        }
    }
}
