package com.kc.WikiImageExplorer.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by kalpesh.chordiya on 3/3/16.
 */
public class WikiSearchResponse implements Serializable {
    private int errorCode;
    private String errorDescription = "";
    private String batchcomplete;
    @SerializedName("continue")
    private Continue continueObj;
    private Query query;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getBatchcomplete() {
        return batchcomplete;
    }

    public void setBatchcomplete(String batchcomplete) {
        this.batchcomplete = batchcomplete;
    }

    public Continue getContinueObj() {
        return continueObj;
    }

    public void setContinueObj(Continue continueObj) {
        this.continueObj = continueObj;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return "WikiPagesResponse{" +
                "errorCode=" + errorCode +
                ", errorDescription='" + errorDescription + '\'' +
                ", batchcomplete='" + batchcomplete + '\'' +
                ", continueObj=" + continueObj +
                ", query=" + query +
                '}';
    }
}
