package com.example.task;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import javax.inject.Inject;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class BookmarksActivity extends AppCompatActivity {

    @Inject
    MovieRepository movieRepository;

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        ((MainApplication) getApplication()).getAppComponent().inject(this);

        recyclerView = findViewById(R.id.recyclerViewBookmarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        movieRepository.getBookmarkedMovies()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    if (movies != null) {
                        movieAdapter.updateMovies(movies);
                    }
                }, throwable -> {
                    // Handle error as needed
                });
    }
}
