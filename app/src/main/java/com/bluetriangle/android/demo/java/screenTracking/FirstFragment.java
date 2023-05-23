package com.bluetriangle.android.demo.java.screenTracking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bluetriangle.android.demo.R;
import com.bluetriangle.android.demo.databinding.CustomAlertDialogBinding;

public class FirstFragment extends Fragment {
    public static FirstFragment newInstance() {
        return new FirstFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_screen_tracking_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.showAlert).setOnClickListener(v -> showAlertDialog());
        view.findViewById(R.id.showDialog).setOnClickListener(v -> new SampleDialog().show(getParentFragmentManager()));
        view.findViewById(R.id.showBottomSheet).setOnClickListener(v -> new BottomSheetDialog().show(getParentFragmentManager()));

        view.findViewById(R.id.nextScreen).setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, SecondFragment.newInstance())
                .addToBackStack(SecondFragment.class.getSimpleName())
                .commit());

        view.findViewById(R.id.tabViewScreen).setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, TabViewFragment.newInstance())
                .addToBackStack(TabViewFragment.class.getSimpleName())
                .commit());
    }

    private void showAlertDialog() {
        CustomAlertDialogBinding dialogBinding =
                DataBindingUtil.inflate(
                        LayoutInflater.from(getContext()),
                        R.layout.custom_alert_dialog,
                        null,
                        false
                );

        AlertDialog customDialog = new AlertDialog.Builder(requireContext(), 0).create();
        customDialog.setView(dialogBinding.getRoot());
        customDialog.setCancelable(false);
        dialogBinding.btnOk.setOnClickListener(v -> customDialog.dismiss());
        customDialog.show();
    }
}
