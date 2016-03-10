package com.kc.WikiImageExplorer.controller;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.android.volley.Request;
import com.kc.WikiImageExplorer.listener.WikiSearchListener;
import com.kc.WikiImageExplorer.requests.WikiSearchRequest;
import com.kc.WikiImageExplorer.transport.TransportManager;
import com.kc.WikiImageExplorer.utils.Constants;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerManager {
    private static final String TAG = ControllerManager.class.getSimpleName();

    private static int requestId = 0;

    private Context mContext;

    private ConcurrentHashMap<Integer, ControllerCallback> mListenersMap = new
            ConcurrentHashMap<Integer, ControllerCallback>();

    private TransportManager mTransportManager = null;

    private MainThread mhThread = null;

    MainHandler mHandler = null;

    private static volatile ControllerManager controllerInstance;

    public static ControllerManager getInstance() {
        if (null == controllerInstance) {
            throw new ExceptionInInitializerError();
        }
        return controllerInstance;
    }

    private void initModules() {
        mTransportManager = (TransportManager) new TransportManager(mContext, getMainHandler());
    }

    // Should be called only Once
    public static ControllerManager createInstance(Context context) {
        if (null != controllerInstance) {
            throw new ExceptionInInitializerError();
        }
        synchronized (ControllerManager.class) {
            if (null != controllerInstance) {
                throw new ExceptionInInitializerError();
            }
            controllerInstance = new ControllerManager(context);
            return controllerInstance;
        }
    }

    private ControllerManager(Context context) {
        mContext = context;
        initialize();
        initModules();
    }

    public synchronized int getNextRequestId() {
        /* -1 would be never be part of valid request number */
        if (requestId == -2) ++requestId;
        return ++requestId;
    }

    /**
     * Adds a listener to the {@link #mListenersMap} so that callback can be
     * sent properly to the right listener.
     *
     * @param requestId against which listener needs to be added into the
     *                  {@link #mListenersMap}.
     * @param listener  which needs to be added into the {@link #mListenersMap}.
     */
    public void addListener(int requestId, ControllerCallback listener) {
        if (listener == null) {
            throw new NullPointerException("Listener should not be NULL");
        }
        if ((mListenersMap != null) && (mListenersMap.get(requestId) != null)) {
            mListenersMap.remove(requestId);
        }
        mListenersMap.put(requestId, listener);
    }

    /**
     * Removes a listener to the {@link #mListenersMap} so that callback will
     * not be recieved by listener as now he don't wants to.
     *
     * @param requestId against which listener needs to be removed from the
     *                  {@link #mListenersMap}.
     */
    public void removeListener(int requestId) {
        if (mListenersMap != null && mListenersMap.get(requestId) != null) {
            // Indirectly Asking GC to collect callbackListener as it is of
            // no use now
            ControllerCallback callback = mListenersMap.remove(requestId);
            Log.w(TAG, "Cancelling Request [" + requestId + "] result = " + (mListenersMap.get
                    (requestId) == null));
            callback = null;
        }
    }

    public ControllerCallback getControllerCallback(int requestId) {
        ControllerCallback callback = null;
        if (mListenersMap != null) {
            callback = mListenersMap.get(requestId);
        }
        // LOG should be shown if Callback is Null
        if (null == callback) Log.e(TAG, "ControllerCallback is NULL");
        return callback;
    }

    public int cancelRequest(int requestId) {
        removeListener(requestId);
        mTransportManager.cancelPendingRequests(requestId);
        return 0;
    }

    public TransportManager getTransportManager() {
        return mTransportManager;
    }

    /**
     * Initialize core manager.
     */
    private void initialize() {
        Log.d(TAG, "Initialization started ..........");
        mhThread = new MainThread("Lime ControllerManager");
        mhThread.start();
        mHandler = new MainHandler(this, mhThread.getLooper());
        Log.d(TAG, "Initialization done.");
    }

    private class MainThread extends HandlerThread {
        public MainThread(String name) {
            super(name);
        }
    }

    static class MainHandler extends Handler {

        private final WeakReference<ControllerManager> activityInstance;

        public MainHandler(ControllerManager instance, Looper looper) {
            super(looper);
            activityInstance = new WeakReference<ControllerManager>(instance);
        }
    }

    public MainHandler getMainHandler() {
        return mHandler;
    }

    public int sendWikiSearch(String keyword, int noOfPages, int thumbnailSize,
                              ControllerCallback listener) {
        String url = Constants.WIKI_IMAGE_SEARCH_URL;
        keyword = keyword.replaceAll(" ", "%20");
        url = url + "&gpssearch=" + keyword + "&gpslimit=" + noOfPages + "&pithumbsize=" +
                thumbnailSize;
        final int requestId = getNextRequestId();
        // Create new listener for response and error
        WikiSearchListener wikiSearchListener = new WikiSearchListener(requestId);
        WikiSearchRequest request = new WikiSearchRequest(Request.Method.GET, url,
                wikiSearchListener, wikiSearchListener);
        Log.i(TAG, "Sending sendGetWikiPages Request " + requestId);
        mTransportManager.execute(request, requestId);
        addListener(requestId, listener);
        return requestId;
    }
}
