package com.bluetriangle.android.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ScreenTrackingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_tracking)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ScreenTrackingFragment.newInstance())
                .commitNow()
        }
    }
}