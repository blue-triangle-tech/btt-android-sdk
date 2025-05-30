package com.bluetriangle.android.demo.groupingpoc.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bluetriangle.android.demo.R

open class GroupTabBaseFragment(val color: Int, val text: String):Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frame = view.findViewById<FrameLayout>(R.id.container)
        val displayTextView = view.findViewById<TextView>(R.id.display_text)

        frame.setBackgroundColor(color)
        displayTextView.text = text
    }

}