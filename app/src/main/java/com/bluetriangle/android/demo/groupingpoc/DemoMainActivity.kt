package com.bluetriangle.android.demo.groupingpoc

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bluetriangle.analytics.Tracker
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.groupingpoc.tabs.TabContainerFragment

class DemoMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(1000)
        setContentView(R.layout.activity_grouping_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.base_frame, TabContainerFragment())
            .commit()

        findViewById<TextView>(R.id.session_id).apply {
            text = "Session ID: ${Tracker.instance?.configuration?.sessionId}"

            setOnClickListener {
                val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboardManager.setPrimaryClip(ClipData.newPlainText("Session ID", Tracker.instance?.configuration?.sessionId?:"Unknown"))
            }
        }
    }
}