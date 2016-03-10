package com.kc.WikiImageExplorer.transport;

import android.content.Context;
import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class TransportManager{
    private static final String TAG = "TransportManager";
    private RequestQueue mRequestQueue = null;
    private Context mContext = null;
    private static TransportManager sInstance = null;
    private Handler mHandler;

    public TransportManager(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    public void execute(Request<?> request, int tag) {
        // Add this request in queue.
        addToRequestQueue(request, tag);
    }

    /**
     * Add request in queue with given tag.
     * 
     * @param request
     * @param tag
     */
    private void addToRequestQueue(Request<?> request, int tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    /**
     * Return request queue. Return existing queue if it is already present.
     * 
     * @return
     */
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext, null, mHandler);
        }
        return mRequestQueue;
    }

    /**
     * Cancel requests of given tag.
     *
     * @param tag
     */
    public void cancelPendingRequests(Object tag) {
        if (getRequestQueue() != null) {
            getRequestQueue().cancelAll(tag);
        }
    }
}
