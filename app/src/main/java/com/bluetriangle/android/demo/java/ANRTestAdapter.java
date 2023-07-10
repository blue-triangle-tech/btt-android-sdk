package com.bluetriangle.android.demo.java;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bluetriangle.android.demo.R;
import com.bluetriangle.android.demo.databinding.AnrTestCaseItemLayoutBinding;
import com.bluetriangle.android.demo.tests.BTTTestCase;

import java.util.ArrayList;
import java.util.List;

public class ANRTestAdapter extends RecyclerView.Adapter<ANRTestAdapter.ANRTestCaseViewHolder> {
    private BTTTestCase currentTestCase = null;
    private List<BTTTestCase> testCases = new ArrayList<>();

    public ANRTestAdapter(List<BTTTestCase> testCases) {
        this.testCases = testCases;
    }

    @NonNull
    @Override
    public ANRTestCaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ANRTestCaseViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.anr_test_case_item_layout,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull ANRTestCaseViewHolder holder, int position) {
        holder.itemBinding.setTestCase(testCases.get(position));
        holder.itemBinding.runButton.setOnClickListener(v -> {
            currentTestCase = testCases.get(holder.getAdapterPosition());
            currentTestCase.run();
        });
    }

    @Override
    public int getItemCount() {
        return testCases.size();
    }

    static class ANRTestCaseViewHolder extends RecyclerView.ViewHolder {
        AnrTestCaseItemLayoutBinding itemBinding;

        public ANRTestCaseViewHolder(AnrTestCaseItemLayoutBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }
    }
}
