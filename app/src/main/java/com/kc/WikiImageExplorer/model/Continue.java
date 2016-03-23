package com.kc.WikiImageExplorer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kalpesh.chordiya on 4/3/16.
 */
public class Continue implements Serializable {

    private String gpsoffset;
    @SerializedName("continue")
    private String continue1 = "";

    public String getGpsoffset() {
        return gpsoffset;
    }

    public void setGpsoffset(String gpsoffset) {
        this.gpsoffset = gpsoffset;
    }

    public String getContinue1() {
        return continue1;
    }

    public void setContinue1(String continue1) {
        this.continue1 = continue1;
    }

    @Override
    public String toString() {
        return new StringBuilder("Continue{ gpsoffset ='").
                append(gpsoffset).append(" continue1=").append(continue1).append("}").toString();
    }
}
