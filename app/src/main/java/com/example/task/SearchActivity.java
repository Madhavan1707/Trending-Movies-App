package com.example.task;

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

        ((MainApplication) getApplication()).getAppComponent().inject(this);

        searchToolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(searchToolbar);

        // Container for search input and RecyclerView
        searchContainer = findViewById(R.id.searchContainer);

        searchInput = findViewById(R.id.search_input);
        // Initially hidden
        searchInput.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        // Set toolbar menu click listener
        searchToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                TransitionManager.beginDelayedTransition((android.view.ViewGroup) searchContainer, new AutoTransition());
                searchInput.setVisibility(View.VISIBLE);
                searchInput.requestFocus();
                return true;
            }
            return false;
        });

        searchViewModel = new SearchViewModel(movieRepository);
        searchViewModel.getSearchResults().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (movies != null) {
                    movieAdapter.updateMovies(movies);
                }
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchViewModel.setSearchQuery(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
}
