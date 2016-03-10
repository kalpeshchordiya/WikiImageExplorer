package com.kc.WikiImageExplorer.model;

import java.io.Serializable;

import javax.crypto.spec.SecretKeySpec;

/**
 * Created by kalpesh.chordiya on 4/3/16.
 */
public class PageObj implements Serializable{
    private String index;
    private String title;
    private String ns;
    private String pageid;
    private Thumbnail thumbnail;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNs() {
        return ns;
    }

    public void setNs(String ns) {
        this.ns = ns;
    }

    public String getPageid() {
        return pageid;
    }

    public void setPageid(String pageid) {
        this.pageid = pageid;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "PageObj{" +
                "index='" + index + '\'' +
                ", title='" + title + '\'' +
                ", ns='" + ns + '\'' +
                ", pageid='" + pageid + '\'' +
                ", thumbnail=" + thumbnail +
                '}';
    }
}
