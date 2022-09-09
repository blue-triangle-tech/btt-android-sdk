package com.bluetriangle.android.demo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.bluetriangle.analytics.Timer;
import com.bluetriangle.analytics.Tracker;
import com.bluetriangle.analytics.okhttp.BlueTriangleOkHttpInterceptor;

import java.io.IOException;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BlueTriangle";
    private Timer timer;

    @BindView(R.id.button_start)
    protected Button startButton;
    @BindView(R.id.button_interactive)
    protected Button interactiveButton;
    @BindView(R.id.button_stop)
    protected Button stopButton;
    @BindView(R.id.button_crash)
    protected Button crashButton;

    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        updateButtonState();
        okHttpClient = new OkHttpClient.Builder().addInterceptor(new BlueTriangleOkHttpInterceptor(Tracker.getInstance().getConfiguration())).build();
    }

    private void updateButtonState() {
        startButton.setEnabled(timer == null || !timer.isRunning() || timer.hasEnded());
        interactiveButton.setEnabled(timer != null && timer.isRunning() && !timer.isInteractive() && !timer.hasEnded());
        stopButton.setEnabled(timer != null && timer.isRunning() && !timer.hasEnded());
    }

    @OnClick(R.id.button_start)
    public void startButtonClicked() {
        timer = new Timer(MainActivity.class.getSimpleName(), "Ä Traffic Šegment").start();
        updateButtonState();
    }

    @OnClick(R.id.button_interactive)
    public void interactiveButtonClicked() {
        if (timer.isRunning()) {
            timer.interactive();
        }
        updateButtonState();
    }

    @OnClick(R.id.button_stop)
    public void stopButtonClicked() {
        if (timer.isRunning()) {
            timer.end().submit();
        }
        updateButtonState();
    }

    @OnClick(R.id.button_next)
    public void nextButtonClicked() {
        final Timer timer = new Timer("Next Page", "Android Traffic").start();
        final Intent intent = new Intent(this, NextActivity.class);
        intent.putExtra(Timer.EXTRA_TIMER, timer);
        startActivity(intent);
    }

    @OnClick(R.id.button_background)
    public void backgroundButtonClicked() {
        final Timer backgroundTimer = new Timer("Background Timer", "background traffic").start();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... voids) {
                try {
                    Thread.sleep(500);
                    backgroundTimer.interactive();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                backgroundTimer.end().submit();
                return null;
            }
        }.execute();
    }

    @OnClick(R.id.button_track_catch_exception)
    public void trackCatchExceptionButtonClicked() {
        try {
            Tracker.getInstance().raiseTestException();
        } catch (Throwable e) {
            Tracker.getInstance().trackException("A test exception caught!", e);
        }
    }

    @OnClick(R.id.button_crash)
    public void crashButtonClicked() {
        Tracker.getInstance().raiseTestException();
    }

    @OnClick(R.id.button_network)
    public void captureNetworkRequests() {
        final Timer timer = new Timer("Test Network Capture", "Android Traffic").start();

        final Request imageRequest = new Request.Builder().url("https://www.httpbin.org/image/jpeg").build();
        okHttpClient.newCall(imageRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: " + call.request());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "onResponse: " + call.request());
            }
        });

        final Request jsonRequest = new Request.Builder().url("https://www.httpbin.org/json").build();
        okHttpClient.newCall(jsonRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "onFailure: " + call.request());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "onResponse: " + call.request());
                timer.end().submit();

                RequestBody body = new FormBody(Arrays.asList("test"), Arrays.asList("value"));
                final Request postRequest = new Request.Builder().url("https://httpbin.org/post").method("POST", body).build();
                okHttpClient.newCall(postRequest).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d(TAG, "onFailure: " + call.request());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Log.d(TAG, "onResponse: " + call.request());
                        new Timer("Test Network Capture 2", "Android Traffic").start().end().submit();
                    }
                });
            }
        });
    }
}
