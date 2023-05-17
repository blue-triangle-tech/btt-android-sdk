package com.bluetriangle.android.demo.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bluetriangle.analytics.Timer
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.databinding.ActivityNextBinding

class NextActivity : AppCompatActivity() {
    private var timer: Timer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityNextBinding>(
            this,
            R.layout.activity_next
        )
        timer = intent.getParcelableExtra(Timer.EXTRA_TIMER)

        binding.buttonStop.setOnClickListener {
            timer?.end()?.submit()
            binding.buttonStop.isEnabled = false
        }
    }

    override fun onResume() {
        super.onResume()
        timer?.interactive()
    }

}