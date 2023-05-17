package com.bluetriangle.android.demo.java.screenTracking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bluetriangle.android.demo.R;

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
        view.findViewById(R.id.nextScreen).setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, SecondFragment.newInstance())
                .addToBackStack(SecondFragment.class.getSimpleName())
                .commit());
    }
}
