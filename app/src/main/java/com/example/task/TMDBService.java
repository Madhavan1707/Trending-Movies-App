package com.example.task;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TMDBService {
    @GET("trending/movie/day")
    Single<MoviesResponse> getTrendingMovies(@Query("api_key") String apiKey);

    @GET("movie/now_playing")
    Single<MoviesResponse> getNowPlayingMovies(@Query("api_key") String apiKey);

    @GET("search/movie")
    Single<MoviesResponse> searchMovies(@Query("api_key") String apiKey, @Query("query") String query);

    // New endpoints:
    @GET("movie/{movie_id}")
    Single<MovieDetailResponse> getMovieDetails(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/credits")
    Single<CreditsResponse> getCreditsForMovie(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/similar")
    Single<MoviesResponse> getSimilarMovies(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Single<VideosResponse> getMovieVideos(@Path("movie_id") int movieId, @Query("api_key") String apiKey);

}
