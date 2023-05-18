package com.bluetriangle.android.demo.java.screenTracking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.bluetriangle.android.demo.R;
import com.bluetriangle.android.demo.databinding.DialogAlertBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogAlertBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_alert, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.btnOk.setOnClickListener(v -> dismiss());
        return binding.getRoot();
    }

    public void show(FragmentManager manager) {
        show(manager, BottomSheetDialog.class.getSimpleName());
    }
}
