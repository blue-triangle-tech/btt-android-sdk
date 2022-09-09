package com.bluetriangle.android.demo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;

import com.bluetriangle.analytics.Timer;
import com.bluetriangle.analytics.Tracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private Timer timer;

    @BindView(R.id.button_start)
    protected Button startButton;
    @BindView(R.id.button_interactive)
    protected Button interactiveButton;
    @BindView(R.id.button_stop)
    protected Button stopButton;
    @BindView(R.id.button_crash)
    protected Button crashButton;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        updateButtonState();
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
}
