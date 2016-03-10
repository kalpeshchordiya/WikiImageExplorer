package com.kc.WikiImageExplorer.activity;

import android.app.Fragment;
import android.os.Bundle;

import com.kc.WikiImageExplorer.model.WikiSearchResponse;

/**
 * Created by kalpesh.chordiya on 9/3/16.
 */
public class WikiSearchRetainerFragment extends Fragment {

    // data objects we want to retain
    private WikiSearchResponse mWikiSearchResponse;
    private String mSearchText = "";

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setWikiSearchData(WikiSearchResponse data) {
        this.mWikiSearchResponse = data;
    }

    public WikiSearchResponse getWikiSearchData() {
        return mWikiSearchResponse;
    }

    public String getSearchText() {
        return mSearchText;
    }

    public void setSearchText(String searchText) {
        this.mSearchText = searchText;
    }
}
