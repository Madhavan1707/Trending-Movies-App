package com.example.task;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CreditsResponse {
    @SerializedName("cast")
    public List<CastMember> cast;
}
