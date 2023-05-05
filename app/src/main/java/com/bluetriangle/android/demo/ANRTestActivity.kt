package com.bluetriangle.android.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.bluetriangle.analytics.Timer
import com.bluetriangle.android.demo.databinding.ActivityAnrTestBinding
import com.bluetriangle.android.demo.tests.ANRTestFactory

class ANRTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityAnrTestBinding>(
            this,
            R.layout.activity_anr_test
        )
        setTitle(R.string.anr_tests)
        binding.lifecycleOwner = this
        binding.adapter = ANRTestAdapter(ANRTestFactory.getANRTests(), this)

        val timerStatus = MutableLiveData(false)
        var timer: Timer? = null

        binding.status = timerStatus
        binding.startStopButton.setOnClickListener {
            if(timerStatus.value == true) {
                timer?.end()?.submit()
                timerStatus.value = false
            } else {
                timer = Timer()
                timer?.start()
                timerStatus.value = true
            }
        }
    }
}