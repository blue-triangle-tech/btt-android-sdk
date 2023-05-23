package com.bluetriangle.android.demo.kotlin.screenTracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.bluetriangle.android.demo.R
import com.bluetriangle.android.demo.databinding.DialogBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialog : BottomSheetDialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding: DialogBottomSheetBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_bottom_sheet, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.btnOk.setOnClickListener { dismiss() }
        return binding.root
    }

    fun show(manager: FragmentManager) {
        show(manager, this.javaClass.simpleName)
    }
}