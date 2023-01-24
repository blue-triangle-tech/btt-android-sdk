package com.bluetriangle.android.demo;

import android.app.Application;

import com.bluetriangle.analytics.BlueTriangleConfiguration;
import com.bluetriangle.analytics.Tracker;

public class DemoApplication extends Application {

    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();

        final BlueTriangleConfiguration configuration = new BlueTriangleConfiguration();
        configuration.setTrackCrashesEnabled(true);
        configuration.setSiteId("mobelux3271241z");
        tracker = Tracker.init(getApplicationContext(), configuration);

        tracker.setSessionTrafficSegmentName("Demo Traffic Segment");
    }
}


