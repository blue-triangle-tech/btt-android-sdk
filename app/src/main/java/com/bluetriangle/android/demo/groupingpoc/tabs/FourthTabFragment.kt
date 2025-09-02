package com.bluetriangle.android.demo.groupingpoc.tabs

import android.content.Intent
import android.os.Bundle
import android.util.AndroidRuntimeException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.compose.ComposeMainActivity
import com.bluetriangle.android.demo.groupingpoc.QuoteRequestHelper

class FourthTabFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_fourth_tab_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        QuoteRequestHelper.instance.setupQuoteUI(lifecycleScope, view)

        view.findViewById<Button>(R.id.crash_button).setOnClickListener {
            throw AndroidRuntimeException("Manual Crash")
        }

        view.findViewById<Button>(R.id.anr_button).setOnClickListener {
            Thread.sleep(6000)
        }
        view.findViewById<Button>(R.id.launch_compose).setOnClickListener {
            startActivity(Intent(context, ComposeMainActivity::class.java))
        }
    }
}