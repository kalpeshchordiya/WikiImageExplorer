package com.kc.WikiImageExplorer.model;

import java.io.Serializable;

/**
 * Created by kalpesh.chordiya on 4/3/16.
 */
public class Thumbnail implements Serializable {
    private String height;
    private String source = "";
    private String width;

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    @Override
    public String toString() {
        return new StringBuilder("Thumbnail { height=").append(height).append(", source=").append
                (source).append(", width=").append(width).append("}").toString();
    }
}
