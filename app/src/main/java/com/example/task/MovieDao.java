package com.example.task;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.task.Movie;
import java.util.List;
import io.reactivex.Single;

@Dao
public interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<Movie> movies);

    @Query("SELECT * FROM movies")
    Single<List<Movie>> getAllMovies();

    @Query("SELECT * FROM movies WHERE id = :movieId")
    Single<Movie> getMovieById(int movieId);

    // New query: get bookmarked movies
    @Query("SELECT * FROM movies WHERE bookmarked = 1")
    Single<List<Movie>> getBookmarkedMovies();

    // New method: update bookmark status
    @Query("UPDATE movies SET bookmarked = :bookmarked WHERE id = :movieId")
    void updateBookmarkStatus(int movieId, boolean bookmarked);
}
