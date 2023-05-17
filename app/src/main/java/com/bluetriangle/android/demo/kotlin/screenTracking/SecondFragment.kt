package com.bluetriangle.android.demo.kotlin.screenTracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bluetriangle.android.demo.R

class SecondFragment : Fragment() {
    companion object {
        fun newInstance() = SecondFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_screen_tracking_second, container, false)
    }
}