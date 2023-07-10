package com.bluetriangle.android.demo.kotlin.screenTracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bluetriangle.android.demo.R

class FirstTabFragment : Fragment() {
    companion object {
        fun newInstance() = FirstTabFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_first_tab, container, false)
    }
}