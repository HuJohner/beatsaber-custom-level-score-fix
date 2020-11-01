package org.hujohner.model;

import com.google.gson.annotations.SerializedName;

public class Score {

    @SerializedName(value = "_score")
    public int score;
    @SerializedName(value = "_playerName")
    public String playerName;
    @SerializedName(value = "_fullCombo")
    public boolean fullCombo;
    @SerializedName(value = "_timestamp")
    public long timestamp;
}
