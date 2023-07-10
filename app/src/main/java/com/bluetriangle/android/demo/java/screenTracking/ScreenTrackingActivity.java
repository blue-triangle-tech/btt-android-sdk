package com.bluetriangle.android.demo.java.screenTracking;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bluetriangle.android.demo.R;

public class ScreenTrackingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_tracking);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FirstFragment.newInstance())
                    .commitNow();
        }
    }
}
