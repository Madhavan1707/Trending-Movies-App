package com.example.task;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.task.Movie;

@Database(entities = {Movie.class}, version = 1) 
public abstract class MovieDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();
}