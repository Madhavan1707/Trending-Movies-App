package com.example.task;

import java.util.List;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MovieRepository {
    private TMDBService tmdbService;
    private MovieDao movieDao;
    private String apiKey;

    public MovieRepository(TMDBService tmdbService, MovieDao movieDao, String apiKey) {
        this.tmdbService = tmdbService;
        this.movieDao = movieDao;
        this.apiKey = apiKey;
    }

    public Single<List<Movie>> getTrendingMovies() {
        return tmdbService.getTrendingMovies(apiKey)
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    List<Movie> movies = response.results;
                    movieDao.insertMovies(movies);
                    return movies;
                });
    }

    public Single<List<Movie>> getNowPlayingMovies() {
        return tmdbService.getNowPlayingMovies(apiKey)
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    List<Movie> movies = response.results;
                    movieDao.insertMovies(movies);
                    return movies;
                });
    }

    public Single<VideosResponse> getMovieVideos(int movieId) {
        return tmdbService.getMovieVideos(movieId, apiKey)
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Movie>> searchMovies(String query) {
        return tmdbService.searchMovies(apiKey, query)
                .subscribeOn(Schedulers.io())
                .map(response -> response.results);
    }

    // New methods:
    public Single<MovieDetailResponse> getMovieDetails(int movieId) {
        return tmdbService.getMovieDetails(movieId, apiKey)
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Movie>> getBookmarkedMovies() {
        return movieDao.getBookmarkedMovies();
    }

    public Single<List<Movie>> getLocalMovies() {
        return movieDao.getAllMovies()
                .subscribeOn(Schedulers.io());
    }



    public Single<CreditsResponse> getCreditsForMovie(int movieId) {
        return tmdbService.getCreditsForMovie(movieId, apiKey)
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Movie>> getSimilarMovies(int movieId) {
        return tmdbService.getSimilarMovies(movieId, apiKey)
                .subscribeOn(Schedulers.io())
                .map(response -> response.results);
    }
}




//todo top bar with dynamic search icon and bar, bookmarks page with movies


//todo 1. Pull-to-Refresh for the Movies List
//Why: Gives users an intuitive way to refresh trending movies manually.
//How: Wrap your RecyclerView in a SwipeRefreshLayout. When the user swipes down, call your ViewModel’s fetch method to reload data.


//todo 2. Skeleton or Shimmer Loading
//Why: Improves perceived performance while data loads from the network.
//How: Use Facebook’s Shimmer library or a “skeleton screen” approach to show placeholder cards before real movie data appears.

//todo 4. Multi-Tab Navigation (e.g., “Trending”, “Now Playing”, “Bookmarks”)
//Why: Organizes content for easy access.
//How: Use a ViewPager2 + TabLayout, or a BottomNavigationView, to switch between different movie categories or your bookmarks.

// todo 5. Dark Theme Toggle
//Why: Modern apps often provide a dark mode to reduce eye strain.
//How: Implement a toggle in your settings or top bar that switches the theme from light to dark by changing AppCompatDelegate.setDefaultNightMode(...).

