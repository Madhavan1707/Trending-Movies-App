package com.example.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @javax.inject.Inject
    MovieRepository movieRepository;
    private SearchViewModel searchViewModel;
    private View searchContainer;
    private MaterialToolbar searchToolbar;
    private androidx.appcompat.widget.AppCompatEditText searchInput;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Dagger injection
        ((MainApplication) getApplication()).getAppComponent().inject(this);

        // Toolbar
        searchToolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(searchToolbar);

        // Container & EditText
        searchContainer = findViewById(R.id.searchContainer);
        searchInput = findViewById(R.id.search_input);
        searchInput.setVisibility(View.VISIBLE);
        searchInput.requestFocus();

        // RecyclerView & Adapter
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        // KEY LINE: set the click listener
        movieAdapter.setOnItemClickListener(movie -> {
            Intent intent = new Intent(SearchActivity.this, MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, movie.id);
            startActivity(intent);
        });

        // ViewModel & Observers
        searchViewModel = new SearchViewModel(movieRepository);
        searchViewModel.getSearchResults().observe(this, movies -> {
            if (movies != null) {
                movieAdapter.updateMovies(movies);
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchViewModel.setSearchQuery(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_search);
//
//        // Inject dependencies
//        ((MainApplication) getApplication()).getAppComponent().inject(this);
//
//        // Initialize the toolbar
//        searchToolbar = findViewById(R.id.searchToolbar);
//        setSupportActionBar(searchToolbar);
//
//        // The container that holds the search field and results
//        searchContainer = findViewById(R.id.searchContainer);
//
//        // Grab the EditText
//        searchInput = findViewById(R.id.search_input);
//
//        // -- KEY FIX --
//        // Make the EditText visible right away
//        searchInput.setVisibility(View.VISIBLE);
//        // Give it focus so the keyboard opens
//        searchInput.requestFocus();
//
//        // Set up the RecyclerView
//        recyclerView = findViewById(R.id.recycler_view);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        movieAdapter = new MovieAdapter(new ArrayList<>());
//        recyclerView.setAdapter(movieAdapter);
//
//        // Set up the ViewModel & observe results
//        searchViewModel = new SearchViewModel(movieRepository);
//        searchViewModel.getSearchResults().observe(this, movies -> {
//            if (movies != null) {
//                movieAdapter.updateMovies(movies);
//            }
//        });
//
//        // Trigger search when text changes
//        searchInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                searchViewModel.setSearchQuery(s.toString());
//            }
//            @Override
//            public void afterTextChanged(Editable s) {}
//        });
//    }

}
