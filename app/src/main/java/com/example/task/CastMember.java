package com.example.task;

import com.google.gson.annotations.SerializedName;

public class CastMember {
    @SerializedName("name")
    public String name;
    @SerializedName("profile_path")
    public String profilePath;
    // Optionally, add more fields such as character, id, etc.
}
