package com.example.task;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.task.Movie;
import com.example.task.MovieRepository;
import java.util.List;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MovieViewModel extends ViewModel {
    private MovieRepository movieRepository;
    private CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<List<Movie>> trendingMovies = new MutableLiveData<>();
    private MutableLiveData<List<Movie>> nowPlayingMovies = new MutableLiveData<>();
    // New LiveData for errors
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public MovieViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        fetchTrendingMovies();
        fetchNowPlayingMovies();
    }

    public LiveData<List<Movie>> getTrendingMovies() {
        return trendingMovies;
    }

    public LiveData<List<Movie>> getNowPlayingMovies() {
        return nowPlayingMovies;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchTrendingMovies() {
        disposables.add(movieRepository.getTrendingMovies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        movies -> {
                            trendingMovies.setValue(movies);
                            Log.d("MovieViewModel", "Fetched " + movies.size() + " trending movies");
                        },
                        throwable -> {
                            String error = throwable.getMessage();
                            Log.e("MovieViewModel", "Error fetching trending movies", throwable);
                            errorMessage.setValue("Error fetching trending movies: " + error);
                        }
                ));
    }

    public void fetchNowPlayingMovies() {
        disposables.add(movieRepository.getNowPlayingMovies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> nowPlayingMovies.setValue(movies),
                        throwable -> {
                            // You can add error handling here as needed
                        }));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
