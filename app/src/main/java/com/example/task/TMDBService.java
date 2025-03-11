package com.example.task;

import com.example.task.MoviesResponse;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBService {
    @GET("trending/movie/day")
    Single<MoviesResponse> getTrendingMovies(@Query("api_key") String apiKey);

    @GET("movie/now_playing")
    Single<MoviesResponse> getNowPlayingMovies(@Query("api_key") String apiKey);

    @GET("search/movie")
    Single<MoviesResponse> searchMovies(@Query("api_key") String apiKey, @Query("query") String query);
}