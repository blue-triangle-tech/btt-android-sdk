package com.bluetriangle.android.demo.java;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bluetriangle.analytics.Timer;
import com.bluetriangle.android.demo.R;
import com.bluetriangle.android.demo.databinding.ActivityNextBinding;

public class NextActivity extends AppCompatActivity {
    private ActivityNextBinding binding;
    private Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_next);

        timer = getIntent().getParcelableExtra(Timer.EXTRA_TIMER);

        binding.buttonStop.setOnClickListener(v -> {
            timer.end().submit();
            binding.buttonStop.setEnabled(false);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.interactive();
    }
}
