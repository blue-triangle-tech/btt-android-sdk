package com.bluetriangle.android.demo.groupingpoc.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.compose.TabContent

class SecondTabFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_second_tab_grouping, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<ComposeView>(R.id.composeContainer).setContent {
            TabContent(tabName = "ComposeTab")
        }
    }
}