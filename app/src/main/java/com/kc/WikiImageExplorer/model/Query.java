package com.kc.WikiImageExplorer.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by kalpesh.chordiya on 4/3/16.
 */
public class Query implements Serializable {
    private Map<String, PageObj> pages;

    public Map<String, PageObj> getPages() {
        return pages;
    }

    public void setPages(Map<String, PageObj> pages) {
        this.pages = pages;
    }

    @Override
    public String toString() {
        return new StringBuilder("Query { pages=").append(pages).append("}").toString();
    }
}
