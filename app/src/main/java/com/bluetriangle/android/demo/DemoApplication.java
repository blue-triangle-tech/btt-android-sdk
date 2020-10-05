package com.bluetriangle.android.demo;

import android.app.Application;
import com.bluetriangle.analytics.Tracker;

public class DemoApplication extends Application {

    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        //demosports
        //testsite5one
        tracker = Tracker.init(getApplicationContext(), "demosports", null);
    }
}


