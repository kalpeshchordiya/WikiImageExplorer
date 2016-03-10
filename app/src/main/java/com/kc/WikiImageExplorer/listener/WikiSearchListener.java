package com.kc.WikiImageExplorer.listener;

import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.kc.WikiImageExplorer.controller.ControllerCallback;
import com.kc.WikiImageExplorer.controller.ControllerManager;
import com.kc.WikiImageExplorer.model.WikiSearchResponse;
import com.kc.WikiImageExplorer.utils.Constants;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WikiSearchListener implements Listener<byte[]>, ErrorListener {

    private static final String TAG = "GetWikiPagesListener";
    public int mRequestId;

    public WikiSearchListener(int requestId) {
        mRequestId = requestId;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d(TAG, "onErrorResponse Data = " + error.getMessage());

        ControllerManager controllerManager = ControllerManager.getInstance();
        ControllerCallback controllerCallback = controllerManager.getControllerCallback(mRequestId);
        WikiSearchResponse resp = new WikiSearchResponse();
        resp.setErrorCode(Constants.ERROR_CODE);
        resp.setErrorDescription(error.getMessage());

        // Send callback to all the listeners.
        if (controllerCallback != null) {
            controllerCallback.onErrorResponseProxy(mRequestId, 0, resp);
            controllerManager.removeListener(mRequestId);
        }
    }

    @Override
    public void onResponse(byte[] response) {
        WikiSearchResponse wikiSearchResponse = null;
        try {
            InputStream is = new ByteArrayInputStream(response);
            final InputStreamReader isr = new InputStreamReader(is);
            BufferedReader r = new BufferedReader(isr);
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            Log.d(TAG, "Response as " + total.toString());
            Gson gson = new Gson();
            wikiSearchResponse = gson.fromJson(total.toString(), WikiSearchResponse.class);
            Log.d(TAG, "Response actual " + wikiSearchResponse.toString());

        } catch (Exception e) {
            Log.e(TAG, "Exception in gson parsing = " + e.getMessage());
            e.printStackTrace();
            wikiSearchResponse = new WikiSearchResponse();
            wikiSearchResponse.setErrorDescription("Invalid Response");
        }
        final ControllerManager controllerManager = ControllerManager.getInstance();
        final ControllerCallback controllerCallback = controllerManager.getControllerCallback
                (mRequestId);
        // Send callback to all the listeners.
        if (controllerCallback != null) {
            if (!wikiSearchResponse.getErrorDescription().equalsIgnoreCase("")) {
                controllerCallback.onErrorResponseProxy(mRequestId, Constants.ERROR_CODE,
                        wikiSearchResponse);
            } else {
                controllerCallback.onSuccessResponseProxy(mRequestId, 0, wikiSearchResponse);
            }
            controllerManager.removeListener(mRequestId);
        }
    }
}
