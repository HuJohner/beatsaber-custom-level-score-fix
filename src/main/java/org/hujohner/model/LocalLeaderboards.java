package org.hujohner.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LocalLeaderboards {

    @SerializedName(value = "_leaderboardsData")
    public List<LeaderboardEntry> data;
}
