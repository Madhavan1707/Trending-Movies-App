package com.example.task;

import com.google.gson.annotations.SerializedName;

public class MovieDetailResponse {
    @SerializedName("id")
    public int id;
    @SerializedName("title")
    public String title;
    @SerializedName("overview")
    public String overview; 
    @SerializedName("poster_path")
    public String posterPath;
    @SerializedName("release_date")
    public String releaseDate;
    @SerializedName("vote_average")
    public float voteAverage;
    // Add additional fields if needed (e.g., runtime, genres, etc.)
}
