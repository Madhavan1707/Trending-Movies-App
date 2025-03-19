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
import io.reactivex.schedulers.Schedulers;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE_ID = "movie_id";
    // Key for passing "hide recommendations" (optional)
    private static final String EXTRA_HIDE_SIMILAR = "HIDE_SIMILAR";

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private MaterialToolbar topAppBarDetail;
    private TextView textViewTitle, textViewOverview, textViewReleaseDate, textViewRating;
    private RatingBar ratingBar;
    private MaterialButton buttonTrailer, buttonBookmark;
    private androidx.recyclerview.widget.RecyclerView recyclerViewCast, recyclerViewSimilar;

    private MovieRepository movieRepository;
    private MovieDetailResponse currentMovie;
    private ArrayList<CastMember> castList = new ArrayList<>();
    private ArrayList<Movie> similarMovies = new ArrayList<>();

    // Local field to track bookmark status (default false)
    private boolean isBookmarked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Find views
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        topAppBarDetail = findViewById(R.id.topAppBarDetail);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOverview = findViewById(R.id.textViewOverview);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        ratingBar = findViewById(R.id.ratingBar);
        textViewRating = findViewById(R.id.textViewRating);
        buttonTrailer = findViewById(R.id.buttonTrailer);
        buttonBookmark = findViewById(R.id.buttonBookmark);
        recyclerViewCast = findViewById(R.id.recyclerViewCast);
        recyclerViewSimilar = findViewById(R.id.recyclerViewSimilar);

        setSupportActionBar(topAppBarDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        topAppBarDetail.setNavigationOnClickListener(v -> onBackPressed());

        // Retrieve the repository instance from Dagger
        movieRepository = ((MainApplication) getApplication()).getAppComponent().injectRepository();

        // Get movie ID from intent
        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        if (movieId == -1) {
            Toast.makeText(this, "Movie ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Optionally check if we should hide the recommendations section
        boolean hideSimilar = getIntent().getBooleanExtra(EXTRA_HIDE_SIMILAR, false);

        // Fetch movie details from network
        movieRepository.getMovieDetails(movieId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(detail -> {
                    currentMovie = detail;
                    // Update basic UI with details
                    updateBasicUI();
                    // After details are loaded, check local DB for bookmark status
                    checkBookmarkStatus(movieId);
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

        // Fetch similar movies (if not hidden)
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
            // Hide similar movies section if needed
            findViewById(R.id.textViewSimilarLabel).setVisibility(android.view.View.GONE);
            recyclerViewSimilar.setVisibility(android.view.View.GONE);
        }

        // Set up Trailer button click
        buttonTrailer.setOnClickListener(v -> {
            movieRepository.getMovieVideos(currentMovie.id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(videosResponse -> {
                        if (videosResponse != null && videosResponse.results != null) {
                            for (Video video : videosResponse.results) {
                                if ("YouTube".equalsIgnoreCase(video.site) && "Trailer".equalsIgnoreCase(video.type)) {
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

        // Set up Cast RecyclerView
        recyclerViewCast.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        CastAdapter castAdapter = new CastAdapter(castList);
        recyclerViewCast.setAdapter(castAdapter);

        // Set up Similar Movies RecyclerView
        recyclerViewSimilar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        MovieAdapter similarAdapter = new MovieAdapter(similarMovies);
        recyclerViewSimilar.setAdapter(similarAdapter);

        // When a similar movie is clicked, launch its details (hide recommendations on the new screen)
        similarAdapter.setOnItemClickListener(movie -> {
            Intent intent = new Intent(MovieDetailActivity.this, MovieDetailActivity.class);
            intent.putExtra(EXTRA_MOVIE_ID, movie.id);
            intent.putExtra(EXTRA_HIDE_SIMILAR, true);
            startActivity(intent);
        });

        // Set up Bookmark button click to toggle status
        buttonBookmark.setOnClickListener(v -> {
            isBookmarked = !isBookmarked;
            // Update local database
            movieRepository.updateBookmarkStatus(currentMovie.id, isBookmarked);
            // Update UI button text
            updateBookmarkButton();
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

    // Check the current bookmark status from the local DB
    private void checkBookmarkStatus(int movieId) {
        movieRepository.getMovieById(movieId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movie -> {
                    // If movie exists locally, set the bookmark flag
                    isBookmarked = movie.bookmarked;
                    updateBookmarkButton();
                }, throwable -> {
                    // If error (e.g., movie not in local DB), assume not bookmarked
                    isBookmarked = false;
                    updateBookmarkButton();
                });
    }

    // Update the bookmark button text based on the current status
    private void updateBookmarkButton() {
        if (isBookmarked) {
            buttonBookmark.setText("Bookmarked");
        } else {
            buttonBookmark.setText("Bookmark");
        }
    }
}
