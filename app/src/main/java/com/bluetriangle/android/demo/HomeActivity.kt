package com.bluetriangle.android.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bluetriangle.android.demo.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)

        binding.btnJavaTest.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    com.bluetriangle.android.demo.java.TestListActivity::class.java
                )
            )
        }

        binding.btnKotlinTest.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    com.bluetriangle.android.demo.kotlin.TestListActivity::class.java
                )
            )
        }
    }
}