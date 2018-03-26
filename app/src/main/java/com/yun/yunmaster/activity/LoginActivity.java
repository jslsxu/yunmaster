package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.LoginResponse;
import com.yun.yunmaster.response.PublicParamsResponse;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.LoginManager;
import com.yun.yunmaster.utils.SendSmsTimeCount;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.ClearEditText;
import com.yun.yunmaster.view.TabEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    public static final int TYPE_LOGIN_TYPE_VERIFY = 1;
    public static final int TYPE_LOGIN_TYPE_PWD = 2;
    @BindView(R.id.ctl_tab)
    CommonTabLayout ctl_tab;
    @BindView(R.id.tv_send_sms)
    TextView tv_send_sms;
    @BindView(R.id.et_phone_num)
    ClearEditText et_phone_num;
    @BindView(R.id.et_code)
    ClearEditText et_code;
    @BindView(R.id.rl_verification_login)
    RelativeLayout rl_verification_login;
    @BindView(R.id.et_pw_phone_num)
    ClearEditText et_pw_phone_num;
    @BindView(R.id.et_pwd)
    ClearEditText et_pwd;
    @BindView(R.id.rl_pw_login)
    RelativeLayout rl_pw_login;
    @BindView(R.id.tv_login)
    TextView tv_login;
    @BindView(R.id.cb_terms)
    CheckBox cb_terms;
    @BindView(R.id.rl_terms)
    RelativeLayout rl_terms;
    @BindView(R.id.back_button)
    ImageButton back_button;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.tv_right)
    TextView tv_right;
    @BindView(R.id.tv_forget_pwd)
    TextView tv_forget_pwd;
    private String[] logintype = {"手机号快捷登录", "账号密码登录"};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private boolean termsChecked = true;
    private int loginType = TYPE_LOGIN_TYPE_VERIFY;
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
        initData();
    }


    private void initView() {
        tv_title.setText("好运欢迎您");
        back_button.setVisibility(View.GONE);
        rl_verification_login.setVisibility(View.VISIBLE);
        rl_pw_login.setVisibility(View.GONE);
        rl_terms.setVisibility(View.VISIBLE);
        tv_forget_pwd.setVisibility(View.GONE);
        tv_right.setVisibility(View.VISIBLE);
        tv_login.setEnabled(false);
        tv_send_sms.setEnabled(false);
        et_phone_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_send_sms.setEnabled(s.toString().length() == 11);
                tv_login.setEnabled(isLoginEnabled());
            }
        });

        time = new SendSmsTimeCount(60000, 1000);// 构造CountDownTimer对象
        time.setOnTimeCountListener(new SendSmsTimeCount.OnTimeCountListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_send_sms.setEnabled(false);
                tv_send_sms.setText(millisUntilFinished / 1000 + "s后重试");
            }

            @Override
            public void onFinish() {
                tv_send_sms.setEnabled(true);
                tv_send_sms.setText("获取验证码");
            }
        });


        cb_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                termsChecked = isChecked;
                tv_login.setEnabled(isLoginEnabled());
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_login.setEnabled(isLoginEnabled());
            }
        };
        et_pw_phone_num.addTextChangedListener(textWatcher);
        et_pwd.addTextChangedListener(textWatcher);
        et_code.addTextChangedListener(textWatcher);

    }

    private void initData() {
        AppSettingManager.requestPublicParams();
        //后面两个参数用不到
        for (String aLogintype : logintype) {
            mTabEntities.add(new TabEntity(aLogintype, R.drawable.ic_launcher, R.drawable.ic_launcher));
        }
        ctl_tab.setTabData(mTabEntities);
        ctl_tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        rl_verification_login.setVisibility(View.VISIBLE);
                        rl_pw_login.setVisibility(View.GONE);
                        rl_terms.setVisibility(View.VISIBLE);
                        tv_forget_pwd.setVisibility(View.GONE);
                        loginType = TYPE_LOGIN_TYPE_VERIFY;
                        break;
                    case 1:
                        rl_verification_login.setVisibility(View.GONE);
                        rl_pw_login.setVisibility(View.VISIBLE);
                        rl_terms.setVisibility(View.GONE);
                        tv_forget_pwd.setVisibility(View.VISIBLE);
                        loginType = TYPE_LOGIN_TYPE_PWD;
                        break;
                }
                tv_login.setEnabled(isLoginEnabled());
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private boolean isLoginEnabled() {
        if (loginType == TYPE_LOGIN_TYPE_VERIFY) {
            return et_phone_num.getText().length() == 11 && !TextUtils.isEmpty(et_code.getText()) && termsChecked;
        } else {
            return et_pw_phone_num.getText().length() == 11 && et_pwd.length() > 5;
        }
    }

    private void requestSendSms() {
        AppSettingManager.sendSms(et_phone_num.getText().toString().trim());
    }


    private void requestLogin() {
        startLoading();
        CommonApis.userLogin(loginType == TYPE_LOGIN_TYPE_VERIFY ? et_phone_num.getText().toString().trim():et_pw_phone_num.getText().toString()
                , et_code.getText().toString().trim(), et_pwd.getText().toString().trim(),loginType, new ResponseCallback<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse response) {
                        endLoading();
                        LoginManager.setLoginData(response.data);
                        LoginManager.setUserRegid();
                        MainActivity.intentTo(LoginActivity.this);
                        LoginActivity.this.finish();

                    }

                    @Override
                    public void onFail(int statusCode, @Nullable BaseResponse httpResponse, @Nullable Throwable error) {
                        endLoading();
                        ToastUtil.showToast(httpResponse.getErrmsg());
                    }
                });


    }


    @OnClick({ R.id.tv_send_sms, R.id.tv_login, R.id.tv_terms, R.id.tv_right, R.id.tv_forget_pwd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_send_sms:
                requestSendSms();
                time.start();
                break;
            case R.id.tv_login:
                requestLogin();
                break;
            case R.id.tv_terms:
                PublicParamsResponse.DataBean publicParams = AppSettingManager.getPublicParams();
                if (publicParams != null && publicParams.getPolicy() != null) {
                    String policyUrl = publicParams.getPolicy().register_policy;
                    CommonWebActivity.intentTo(LoginActivity.this, policyUrl, "好运服务协议");
                } else {
                    AppSettingManager.requestPublicParams();
                }
                break;

            case R.id.tv_right:
                RegisterActivity.intentTo(this);
                break;

            case R.id.tv_forget_pwd:
                ResetPasswordActivity.intentTo(LoginActivity.this, "重置密码");
                break;
        }
    }
}

