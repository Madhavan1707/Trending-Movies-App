package com.example.task;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "movies") 
public class Movie {
    @PrimaryKey
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

    // Optionally, add a bookmarked field if needed
    public boolean bookmarked;
}
