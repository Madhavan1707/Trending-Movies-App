package com.example.task;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("id")
    public String id;

    @SerializedName("key")
    public String key;

    @SerializedName("site")
    public String site;

    @SerializedName("type")
    public String type;
}
