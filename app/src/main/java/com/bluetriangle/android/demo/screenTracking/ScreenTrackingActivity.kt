package com.bluetriangle.android.demo.screenTracking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluetriangle.android.demo.R

class ScreenTrackingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_tracking)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FirstFragment.newInstance())
                .commitNow()
        }
    }
}