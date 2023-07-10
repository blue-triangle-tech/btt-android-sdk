package com.bluetriangle.android.demo.java.screenTracking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bluetriangle.android.demo.R;
import com.bluetriangle.android.demo.databinding.DialogSampleBinding;

public class SampleDialog extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogSampleBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_sample, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.btnOk.setOnClickListener(v -> dismiss());
        return binding.getRoot();
    }

    public void show(FragmentManager manager) {
        show(manager, BottomSheetDialog.class.getSimpleName());
    }
}
