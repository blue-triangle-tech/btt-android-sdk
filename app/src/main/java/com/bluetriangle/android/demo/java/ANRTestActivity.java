package com.bluetriangle.android.demo.java;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;

import com.bluetriangle.analytics.Timer;
import com.bluetriangle.android.demo.R;
import com.bluetriangle.android.demo.databinding.ActivityAnrTestJavaBinding;
import com.bluetriangle.android.demo.tests.ANRTestFactory;

public class ANRTestActivity extends AppCompatActivity {
    private Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAnrTestJavaBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_anr_test_java);
        binding.setLifecycleOwner(this);
        binding.setAdapter(new ANRTestAdapter(ANRTestFactory.INSTANCE.getANRTests()));

        MutableLiveData<Boolean> timerStatus = new MutableLiveData<>(false);

        binding.setStatus(timerStatus);

        binding.startStopButton.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(timerStatus.getValue())) {
                timer.end().submit();
                timerStatus.setValue(false);
            } else {
                timer = new Timer();
                timer.start();
                timerStatus.setValue(true);
            }
        });
    }
}