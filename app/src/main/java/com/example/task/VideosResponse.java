package com.example.task;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VideosResponse {
    @SerializedName("results")
    public List<Video> results;
}
