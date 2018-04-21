package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.LoginResponse;
import com.yun.yunmaster.utils.LoginManager;
import com.yun.yunmaster.utils.SendSmsTimeCount;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.ClearEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.mobileTextView)
    ClearEditText mobileTextView;
    @BindView(R.id.sendSmsButton)
    TextView sendSmsButton;
    @BindView(R.id.smsTextView)
    ClearEditText smsTextView;
    @BindView(R.id.loginButton)
    RoundTextView loginButton;
    private boolean termsChecked = true;
    private SendSmsTimeCount time;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {

    }


    private void requestSendSms() {
        String mobile = mobileTextView.getText().toString().trim();
        if (TextUtils.isEmpty(mobile)) {
            ToastUtil.showToast("请输入手机号");
            return;
        }
        CommonApis.sendSms(mobile, new ResponseCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response) {
                ToastUtil.showToast("验证码已发送");
                if (time != null) {
                    time.cancel();
                }
                time = new SendSmsTimeCount(60 * 1000, 1000);
                time.setOnTimeCountListener(new SendSmsTimeCount.OnTimeCountListener() {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int secondsRemained = (int) millisUntilFinished / 1000;
                        sendSmsButton.setText(secondsRemained + "秒后重试");
                        sendSmsButton.setEnabled(false);
                    }

                    @Override
                    public void onFinish() {
                        sendSmsButton.setText("发送验证码");
                        sendSmsButton.setEnabled(true);
                    }
                });
                time.start();
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse httpResponse, @Nullable Throwable error) {
                ToastUtil.showToast(httpResponse.getErrmsg());
            }
        });
    }


    private void requestLogin() {
        startLoading();
        String mobile = mobileTextView.getText().toString().trim();
        String verifyCode = smsTextView.getText().toString().trim();
        CommonApis.userLogin(mobile, verifyCode, new ResponseCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                endLoading();
                if(!TextUtils.isEmpty(response.data.token)){
                    LoginManager.setLoginData(response.data);
                    LoginManager.setUserRegid();
                    MainActivity.intentTo(LoginActivity.this);
                    LoginActivity.this.finish();
                }
                else {
                    ToastUtil.showToast("网络请求失败，请稍后再试");
                }

            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse httpResponse, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(httpResponse.getErrmsg());
            }
        });


    }


    @OnClick({R.id.loginButton, R.id.sendSmsButton})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendSmsButton:
                requestSendSms();
//                time.start();
                break;
            case R.id.loginButton:
                requestLogin();
                break;
        }
    }
}

