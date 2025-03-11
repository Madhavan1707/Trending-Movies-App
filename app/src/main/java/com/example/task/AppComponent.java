package com.example.task;

import com.example.task.MainApplication;
import com.example.task.HomeActivity;
import com.example.task.SearchActivity;
import com.example.task.BookmarksActivity;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component(modules = {NetworkModule.class, DatabaseModule.class, RepositoryModule.class})
public interface AppComponent {
    void inject(MainApplication application);
    void inject(HomeActivity homeActivity);
    void inject(SearchActivity searchActivity);
    void inject(BookmarksActivity bookmarksActivity); // Added this line
    MovieRepository injectRepository();
}
