package com.bluetriangle.android.demo.kotlin.screenTracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.databinding.DialogSampleBinding

class SampleDialog : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding: DialogSampleBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_sample, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.btnOk.setOnClickListener { dismiss() }
        return binding.root
    }

    fun show(manager: FragmentManager) {
        show(manager, this.javaClass.simpleName)
    }
}