package com.kc.WikiImageExplorer.application;

import android.app.Application;

import com.kc.WikiImageExplorer.controller.VolleyManager;

/**
 * Created by kalpesh.chordiya on 3/3/16.
 */
public class WikiImageExplorer extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Create ControllerManager instance.
        VolleyManager.createInstance(getApplicationContext());
    }
}
