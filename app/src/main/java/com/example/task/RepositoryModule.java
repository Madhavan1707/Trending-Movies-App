package com.example.task;
import com.example.task.MovieDatabase;
import com.example.task.TMDBService;
import com.example.task.MovieRepository;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class RepositoryModule {
    private String apiKey;

    public RepositoryModule(String apiKey) {
        this.apiKey = apiKey;
    }

    @Singleton
    @Provides
    MovieRepository provideMovieRepository(TMDBService tmdbService, MovieDatabase movieDatabase) {
        return new MovieRepository(tmdbService, movieDatabase.movieDao(), apiKey);
    }
}