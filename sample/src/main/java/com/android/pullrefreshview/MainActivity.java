package com.android.pullrefreshview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.library.pullrefresh.PullRefreshView;

public class MainActivity extends AppCompatActivity implements PullRefreshView.PullRefreshListener {
    PullRefreshView mPullView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPullView = findViewById(R.id.pull_refresh_view);
        mPullView.setListener(this);
    }



    @Override
    public void onPullRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPullView.stopLoading();
            }
        },2000);
    }

}
