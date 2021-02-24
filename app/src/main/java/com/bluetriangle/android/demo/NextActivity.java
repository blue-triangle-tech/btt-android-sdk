package com.bluetriangle.android.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bluetriangle.analytics.Timer;

public class NextActivity extends AppCompatActivity {

    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        ButterKnife.bind(this);
        timer = getIntent().getParcelableExtra(Timer.EXTRA_TIMER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.interactive();
    }

    @OnClick(R.id.button_stop)
    public void stopButtonClicked(final Button button) {
        timer.end().submit();
        button.setEnabled(false);
    }

}
