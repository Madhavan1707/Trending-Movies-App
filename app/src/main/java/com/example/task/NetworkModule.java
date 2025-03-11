package com.example.task;
import com.example.task.RetrofitClient;
import com.example.task.TMDBService;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit2.Retrofit;

@Module
public class NetworkModule {
    @Singleton
    @Provides
    Retrofit provideRetrofitClient() {
        return RetrofitClient.getClient();
    }

    @Singleton
    @Provides
    TMDBService provideTMDBService(Retrofit retrofit) {
        return retrofit.create(TMDBService.class);
    }
}