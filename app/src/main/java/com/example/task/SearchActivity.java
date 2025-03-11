package com.example.task;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.EditText;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    @javax.inject.Inject
    MovieRepository movieRepository;

    private SearchViewModel searchViewModel;
    private EditText searchInput;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter; // Use same adapter to show search results

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ((MainApplication) getApplication()).getAppComponent().inject(this);

        searchViewModel = new SearchViewModel(movieRepository);

        searchInput = findViewById(R.id.search_input);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieAdapter(new ArrayList<>());
        recyclerView.setAdapter(movieAdapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchViewModel.setSearchQuery(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
                // No action
            }
        });

        searchViewModel.getSearchResults().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                if (movies != null) {
                    movieAdapter.updateMovies(movies);
                }
            }
        });
    }
}
