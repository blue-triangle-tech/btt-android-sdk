package com.bluetriangle.android.demo.kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.databinding.ActivityCputestBinding
import com.bluetriangle.android.demo.getViewModel

class CPUTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCputestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.cpu_test)

        binding.lifecycleOwner = this
        binding.viewModel = getViewModel()
    }
}