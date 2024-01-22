package com.bluetriangle.analytics.okhttp;

import com.bluetriangle.analytics.Tracker;

import okhttp3.OkHttpClient;

public class NetworkTracker {

    public static OkHttpClient.Builder track(OkHttpClient.Builder builder) {
        Tracker tracker = Tracker.getInstance();
        if(tracker != null) {
            builder.addInterceptor(new BlueTriangleOkHttpInterceptor(tracker.getConfiguration()));
            builder.eventListenerFactory(new BlueTriangleOkHttpEventListenerFactory(tracker.getConfiguration()));
        }
        return builder;
    }

}
