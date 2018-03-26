package com.yun.yunmaster.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.utils.LoginManager;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class LaunchActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (LoginManager.isLogin()) {
                    MainActivity.intentTo(LaunchActivity.this);
                } else {
                    LoginActivity.intentTo(LaunchActivity.this);
                }
                finish();
            }
        }, 500);
    }
}

