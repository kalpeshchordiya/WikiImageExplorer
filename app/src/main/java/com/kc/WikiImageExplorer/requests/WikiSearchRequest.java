package com.kc.WikiImageExplorer.requests;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.InputStreamRequest;

public class WikiSearchRequest extends InputStreamRequest {

    private static final String TAG = "WikiSearchRequest";

    public WikiSearchRequest(int method, String url, Listener<byte[]> listener,
                                   ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }
}
