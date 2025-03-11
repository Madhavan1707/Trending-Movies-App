package com.example.task;

import android.app.Application;
import com.example.task.AppComponent;
import com.example.task.DaggerAppComponent;
import com.example.task.DatabaseModule;
import com.example.task.NetworkModule;
import com.example.task.RepositoryModule;

public class MainApplication extends Application {
    private AppComponent appComponent;

    // Updated API key
    public static final String TMDB_API_KEY = "1b80cab3b82a4e82823c0660f4bc7253";

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .networkModule(new NetworkModule())
                .databaseModule(new DatabaseModule(this))
                .repositoryModule(new RepositoryModule(TMDB_API_KEY))
                .build();
        appComponent.inject(this);
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
