package com.example.task;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "movie_id";

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private MaterialToolbar topAppBarDetail;
    private TextView textViewTitle, textViewOverview, textViewReleaseDate, textViewRating;
    private RatingBar ratingBar;
    private MaterialButton buttonTrailer;
    private androidx.recyclerview.widget.RecyclerView recyclerViewCast, recyclerViewSimilar;

    private MovieRepository movieRepository;
    private MovieDetailResponse currentMovie;
    private ArrayList<CastMember> castList = new ArrayList<>();
    private ArrayList<Movie> similarMovies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        topAppBarDetail = findViewById(R.id.topAppBarDetail);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOverview = findViewById(R.id.textViewOverview);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        ratingBar = findViewById(R.id.ratingBar);
        textViewRating = findViewById(R.id.textViewRating);
        buttonTrailer = findViewById(R.id.buttonTrailer);
        // Assuming buttonBookmark is used as the Trailer button
        recyclerViewCast = findViewById(R.id.recyclerViewCast);
        recyclerViewSimilar = findViewById(R.id.recyclerViewSimilar);

        setSupportActionBar(topAppBarDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        topAppBarDetail.setNavigationOnClickListener(v -> onBackPressed());

        // Retrieve the repository instance from Dagger
        movieRepository = ((MainApplication) getApplication()).getAppComponent().injectRepository();

        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        if (movieId == -1) {
            Toast.makeText(this, "Movie ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch movie details
        movieRepository.getMovieDetails(movieId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(detail -> {
                    currentMovie = detail;
                    updateBasicUI();
                }, throwable -> {
                    Toast.makeText(this, "Error loading movie details", Toast.LENGTH_SHORT).show();
                });

        // Fetch cast list
        movieRepository.getCreditsForMovie(movieId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(credits -> {
                    castList.clear();
                    castList.addAll(credits.cast);
                    if (recyclerViewCast.getAdapter() instanceof CastAdapter) {
                        ((CastAdapter) recyclerViewCast.getAdapter()).updateCast(castList);
                    }
                }, throwable -> {
                    Toast.makeText(this, "Error loading cast", Toast.LENGTH_SHORT).show();
                });

        // Fetch similar movies
        movieRepository.getSimilarMovies(movieId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    similarMovies.clear();
                    similarMovies.addAll(movies);
                    if (recyclerViewSimilar.getAdapter() instanceof MovieAdapter) {
                        ((MovieAdapter) recyclerViewSimilar.getAdapter()).updateMovies(similarMovies);
                    }
                }, throwable -> {
                    Toast.makeText(this, "Error loading similar movies", Toast.LENGTH_SHORT).show();
                });

        // Set up Trailer button click
        buttonTrailer.setOnClickListener(v -> {
            // Replace with the actual trailer URL from movie details if available.
            // For now, using a sample URL.
            String trailerUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl)));
        });

        // Set up Cast RecyclerView
        recyclerViewCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        CastAdapter castAdapter = new CastAdapter(castList);
        recyclerViewCast.setAdapter(castAdapter);

        // Set up Similar Movies RecyclerView
        recyclerViewSimilar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        MovieAdapter similarAdapter = new MovieAdapter(similarMovies);
        recyclerViewSimilar.setAdapter(similarAdapter);
    }

    private void updateBasicUI() {
        collapsingToolbarLayout.setTitle(currentMovie.title);
        textViewTitle.setText(currentMovie.title);
        textViewOverview.setText(currentMovie.overview);
        textViewReleaseDate.setText("Release Date: " + currentMovie.releaseDate);
        float normalizedRating = currentMovie.voteAverage / 2.0f;
        ratingBar.setRating(normalizedRating);
        textViewRating.setText(String.valueOf(currentMovie.voteAverage));
        String posterUrl = "https://image.tmdb.org/t/p/w500" + currentMovie.posterPath;
        Glide.with(this).load(posterUrl).into((android.widget.ImageView) findViewById(R.id.imageViewPoster));
    }
}
