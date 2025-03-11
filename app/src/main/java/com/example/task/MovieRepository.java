package com.example.task;

import android.util.Log;

import com.example.task.Movie;
import com.example.task.MoviesResponse;
import com.example.task.MovieDao;
import com.example.task.TMDBService;
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

//    public Single<List<Movie>> getTrendingMovies() {
//        return tmdbService.getTrendingMovies(apiKey)
//                .subscribeOn(Schedulers.io())
//                .map(response -> {
//                    List<Movie> movies = response.results;
//                    // Save movies to local DB for offline caching
//                    movieDao.insertMovies(movies);
//                    return movies;
//                });
//    }

    public Single<List<Movie>> getTrendingMovies() {
        Log.d("MovieRepository", "Using API key: " + apiKey);
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

    public Single<List<Movie>> searchMovies(String query) {
        return tmdbService.searchMovies(apiKey, query)
                .subscribeOn(Schedulers.io())
                .map(response -> response.results);
    }

    public Single<List<Movie>> getLocalMovies() {
        return movieDao.getAllMovies();
    }

    // New method: get a single movie by its ID
    public Single<Movie> getMovieById(int movieId) {
        return movieDao.getMovieById(movieId).subscribeOn(Schedulers.io());
    }

    // New method: get bookmarked movies
    public Single<List<Movie>> getBookmarkedMovies() {
        return movieDao.getBookmarkedMovies().subscribeOn(Schedulers.io());
    }

    // New method: update a movie's bookmark status
    public void updateBookmarkStatus(int movieId, boolean bookmarked) {
        Schedulers.io().scheduleDirect(() -> {
            movieDao.updateBookmarkStatus(movieId, bookmarked);
        });
    }
}
