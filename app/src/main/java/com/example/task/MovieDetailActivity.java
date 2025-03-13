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
    // Key for passing "hide recommendations"
    private static final String EXTRA_HIDE_SIMILAR = "HIDE_SIMILAR";

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

        // 1) Find Views
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        topAppBarDetail = findViewById(R.id.topAppBarDetail);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOverview = findViewById(R.id.textViewOverview);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        ratingBar = findViewById(R.id.ratingBar);
        textViewRating = findViewById(R.id.textViewRating);

        // If your trailer button ID is 'buttonTrailer'
        buttonTrailer = findViewById(R.id.buttonTrailer);

        recyclerViewCast = findViewById(R.id.recyclerViewCast);
        recyclerViewSimilar = findViewById(R.id.recyclerViewSimilar);

        setSupportActionBar(topAppBarDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        topAppBarDetail.setNavigationOnClickListener(v -> onBackPressed());

        // 2) Retrieve the repository instance from Dagger
        movieRepository = ((MainApplication) getApplication()).getAppComponent().injectRepository();

        // 3) Get movieId from Intent
        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        if (movieId == -1) {
            Toast.makeText(this, "Movie ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 4) Check if we should hide the "You might also like" section
        boolean hideSimilar = getIntent().getBooleanExtra(EXTRA_HIDE_SIMILAR, false);

        // 5) Fetch movie details
        movieRepository.getMovieDetails(movieId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(detail -> {
                    currentMovie = detail;
                    updateBasicUI();
                }, throwable -> {
                    Toast.makeText(this, "Error loading movie details", Toast.LENGTH_SHORT).show();
                });

        // 6) Fetch cast list
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

        // 7) If we are NOT hiding recommendations, fetch & show them
        if (!hideSimilar) {
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
        } else {
            // Hide the label & RecyclerView for similar movies if present
            findViewById(R.id.textViewSimilarLabel).setVisibility(android.view.View.GONE);
            recyclerViewSimilar.setVisibility(android.view.View.GONE);
        }

        // 8) Trailer button click
        buttonTrailer.setOnClickListener(v -> {
            // Fetch movie videos
            movieRepository.getMovieVideos(currentMovie.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(videosResponse -> {
                        if (videosResponse != null && videosResponse.results != null) {
                            // Find a video that is a YouTube trailer (you may adjust criteria as needed)
                            for (Video video : videosResponse.results) {
                                if ("YouTube".equalsIgnoreCase(video.site) && "Trailer".equalsIgnoreCase(video.type)) {
                                    // Construct the YouTube URL
                                    String trailerUrl = "https://www.youtube.com/watch?v=" + video.key;
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl)));
                                    return;
                                }
                            }
                            Toast.makeText(MovieDetailActivity.this, "Trailer not available", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MovieDetailActivity.this, "Trailer not available", Toast.LENGTH_SHORT).show();
                        }
                    }, throwable -> {
                        Toast.makeText(MovieDetailActivity.this, "Error fetching trailer", Toast.LENGTH_SHORT).show();
                    });
        });


        // 9) Set up Cast RecyclerView
        recyclerViewCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        CastAdapter castAdapter = new CastAdapter(castList);
        recyclerViewCast.setAdapter(castAdapter);

        // 10) Set up Similar Movies RecyclerView
        recyclerViewSimilar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        MovieAdapter similarAdapter = new MovieAdapter(similarMovies);
        recyclerViewSimilar.setAdapter(similarAdapter);

        // KEY: Add a click listener to recommended movies
        similarAdapter.setOnItemClickListener(movie -> {
            // Launch a new detail screen with "HIDE_SIMILAR" = true
            Intent intent = new Intent(MovieDetailActivity.this, MovieDetailActivity.class);
            intent.putExtra(EXTRA_MOVIE_ID, movie.id);
            intent.putExtra(EXTRA_HIDE_SIMILAR, true);
            startActivity(intent);
        });
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
