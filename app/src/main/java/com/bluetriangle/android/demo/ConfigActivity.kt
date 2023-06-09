package com.bluetriangle.android.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bluetriangle.android.demo.databinding.ActivityConfigBinding
import com.google.android.material.snackbar.Snackbar

class ConfigActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityConfigBinding>(this, R.layout.activity_config)

        val savedId = DemoApplication.tinyDB.getString("BttSiteId")
        if (!savedId.isNullOrBlank())
            binding.edtSiteId.setText(savedId)

        binding.btnSave.setOnClickListener {
            val siteId = binding.edtSiteId.text.toString()
            if (siteId.isBlank()) {
                Snackbar.make(it, "Please enter SiteId", Snackbar.LENGTH_LONG).show()
            } else {
                DemoApplication.tinyDB.setString("BttSiteId", siteId)
                (application as DemoApplication).intTracker(siteId)

                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }
}