package com.bluetriangle.android.demo.java;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;

import com.bluetriangle.analytics.Timer;
import com.bluetriangle.android.demo.R;
import com.bluetriangle.android.demo.databinding.ActivityAnrTestJavaBinding;
import com.bluetriangle.android.demo.tests.ANRTest;
import com.bluetriangle.android.demo.tests.ANRTestFactory;
import com.bluetriangle.android.demo.tests.ANRTestScenario;

public class ANRTestActivity extends AppCompatActivity {
    public static String TestScenario = "TestScenario";
    public static String Test = "Test";
    public static String BroadCastName = "com.example.Broadcast";
    private ANRTest anrTest = ANRTest.All;
    private ANRTestScenario anrTestScenario = ANRTestScenario.Unknown;
    private MyReceiver receiver = null;

    private Timer timer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAnrTestJavaBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_anr_test_java);
        binding.setLifecycleOwner(this);

        anrTest = (ANRTest) getIntent().getExtras().getSerializable(Test);
        anrTestScenario = (ANRTestScenario) getIntent().getExtras().getSerializable(TestScenario);

        if (anrTest == ANRTest.All || anrTestScenario == ANRTestScenario.Unknown || anrTestScenario == ANRTestScenario.OnApplicationCreate) {
            binding.setAdapter(new ANRTestAdapter(ANRTestFactory.INSTANCE.getANRTests()));
        } else if (anrTestScenario == ANRTestScenario.OnActivityCreate) {
            ANRTestFactory.INSTANCE.getANRTest(anrTest).run();
        } else if (anrTestScenario == ANRTestScenario.OnBroadCastReceived) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BroadCastName);
            receiver = new MyReceiver();
            registerReceiver(receiver, filter);
        }

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

    @Override
    protected void onResume() {
        super.onResume();

        if (anrTestScenario != ANRTestScenario.Unknown && anrTestScenario != ANRTestScenario.OnApplicationCreate && anrTest != ANRTest.All) {
            if (anrTestScenario == ANRTestScenario.OnActivityResume)
                ANRTestFactory.INSTANCE.getANRTest(anrTest).run();

            if (anrTestScenario == ANRTestScenario.OnBroadCastReceived) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> sendBroadcast(new Intent(BroadCastName)), 2000);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) unregisterReceiver(receiver);
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ANRTestFactory.INSTANCE.getANRTest(anrTest).run();
        }
    }
}