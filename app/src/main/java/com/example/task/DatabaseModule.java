package com.example.task;
import android.content.Context;
import androidx.room.Room;
import com.example.task.MovieDatabase;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class DatabaseModule {
    private Context context;

    public DatabaseModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    MovieDatabase provideMovieDatabase() {
        return Room.databaseBuilder(context, MovieDatabase.class, "movies_db").build();
    }
}