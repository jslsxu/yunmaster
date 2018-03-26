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
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.LoginResponse;
import com.yun.yunmaster.response.PublicParamsResponse;
import com.yun.yunmaster.utils.ActivityManager;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.LoginManager;
import com.yun.yunmaster.utils.SendSmsTimeCount;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.ClearEditText;
import com.yun.yunmaster.view.CommonDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.tv_send_sms)
    TextView tv_send_sms;
    @BindView(R.id.et_phone_num)
    ClearEditText et_phone_num;
    @BindView(R.id.et_code)
    ClearEditText et_code;
    @BindView(R.id.et_pwd)
    ClearEditText et_pwd;
    @BindView(R.id.et_pwd_confirm)
    ClearEditText et_pwd_confirm;
    @BindView(R.id.et_invite_code)
    ClearEditText et_invite_code;
    @BindView(R.id.cb_terms)
    CheckBox cb_terms;
    @BindView(R.id.tv_submit)
    TextView tv_submit;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    private boolean termsChecked = true;
    private SendSmsTimeCount time;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        navigationBar.setTitle("注册");
        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_submit.setEnabled(false);
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
                tv_submit.setEnabled(isLoginEnabled());
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
                tv_submit.setEnabled(isLoginEnabled());

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
                tv_submit.setEnabled(isLoginEnabled());

            }
        };

        et_pwd.addTextChangedListener(textWatcher);
        et_pwd_confirm.addTextChangedListener(textWatcher);
        et_code.addTextChangedListener(textWatcher);
    }


    private boolean isLoginEnabled() {
        return et_phone_num.getText().length() == 11 && !TextUtils.isEmpty(et_code.getText()) && termsChecked && et_pwd.getText().length() > 5 && et_pwd_confirm.getText().length() > 5;
    }

    private void initData() {
        AppSettingManager.requestPublicParams();
    }


    private void requestSendSms() {
        AppSettingManager.sendSms(et_phone_num.getText().toString().trim());
    }


    private void requestRegister() {
        startLoading();
        CommonApis.userRegister(et_phone_num.getText().toString().trim(), et_pwd.getText().toString().trim(),
                et_code.getText().toString().trim(), et_invite_code.getText().toString().trim(), new ResponseCallback<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse response) {
                        endLoading();
                        LoginManager.setLoginData(response.data);
                        LoginManager.setUserRegid();
                        ActivityManager.getInstance().popActivity(LoginActivity.class);
                        MainActivity.intentTo(RegisterActivity.this);
                        RegisterActivity.this.finish();

                    }

                    @Override
                    public void onFail(int statusCode, @Nullable BaseResponse httpResponse, @Nullable Throwable error) {
                        endLoading();
                        ToastUtil.showToast(httpResponse.getErrmsg());
                    }
                });
    }


    @OnClick({R.id.tv_send_sms, R.id.tv_submit, R.id.tv_terms})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.tv_send_sms:
                requestSendSms();
                time.start();
                break;
            case R.id.tv_submit:
                String pwdstr = et_pwd.getText().toString();
                if (!pwdstr.equals(et_pwd_confirm.getText().toString())) {
                    CommonDialog.ActionItem doneItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                        @Override
                        public void onAction() {
                        }
                    });
                    ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                    actionList.add(doneItem);
                    CommonDialog.showDialog(RegisterActivity.this, "提示", "密码输入不一致", actionList);

                } else {
                    requestRegister();
                }
                break;
            case R.id.tv_terms:
                PublicParamsResponse.DataBean publicParams = AppSettingManager.getPublicParams();
                if (publicParams != null && publicParams.getPolicy() != null) {
                    String policyUrl = publicParams.getPolicy().register_policy;
                    CommonWebActivity.intentTo(RegisterActivity.this, policyUrl, "好运服务协议");
                } else {
                    AppSettingManager.requestPublicParams();
                }

                break;
        }
    }


}

