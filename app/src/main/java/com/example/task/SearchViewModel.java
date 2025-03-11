package com.example.task;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.task.Movie;
import com.example.task.MovieRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.PublishSubject;

public class SearchViewModel extends ViewModel {
    private MovieRepository movieRepository;
    private CompositeDisposable disposables = new CompositeDisposable();
    private MutableLiveData<List<Movie>> searchResults = new MutableLiveData<>();
    private PublishSubject<String> querySubject = PublishSubject.create();

    public SearchViewModel(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        disposables.add(querySubject.debounce(500, TimeUnit.MILLISECONDS)
                .switchMap(query -> movieRepository.searchMovies(query).toObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> searchResults.setValue(movies),
                        throwable -> {
                            // Handle error
                        }));
    }

    public void setSearchQuery(String query) {
        querySubject.onNext(query);
    }

    public LiveData<List<Movie>> getSearchResults() {
        return searchResults;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}