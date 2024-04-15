package com.bluetriangle.android.demo.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.databinding.ActivityMemoryTestBinding
import com.bluetriangle.android.demo.getViewModel

class MemoryTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMemoryTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.memory_test)

        binding.lifecycleOwner = this
        binding.viewModel = getViewModel()
    }
}