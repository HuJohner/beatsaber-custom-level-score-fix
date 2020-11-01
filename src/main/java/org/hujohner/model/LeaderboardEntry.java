package org.hujohner.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LeaderboardEntry {

    @SerializedName(value = "_leaderboardId")
    public String id;
    @SerializedName(value = "_scores")
    public List<Score> scores;
}
