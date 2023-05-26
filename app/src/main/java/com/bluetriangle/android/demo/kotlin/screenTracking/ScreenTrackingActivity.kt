package com.bluetriangle.android.demo.kotlin.screenTracking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.databinding.ActivityScreenTrackingBinding

class ScreenTrackingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScreenTrackingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScreenTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FirstFragment.newInstance())
                .commitNow()
        }
    }
}