package com.bluetriangle.android.demo;

import android.app.Application;
import com.bluetriangle.analytics.Tracker;

public class DemoApplication extends Application {

    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();
        // d.btttag.com => 107.22.227.162
        //"http://107.22.227.162/btt.gif"
        //https://d.btttag.com/analytics.rcv
        //sdkdemo26621z
        //bluetriangledemo500z
        tracker = Tracker.init(getApplicationContext(), "sdkdemo26621z" );
        tracker.trackCrashes();
        tracker.setSessionTrafficSegmentName("Demo Traffic Segment");
        //tracker.raiseTestException();

    }
}


