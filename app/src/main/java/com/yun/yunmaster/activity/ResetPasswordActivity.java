package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.LoginResponse;
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

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private static final String TITLE_KEY = "title";
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
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
    @BindView(R.id.tv_submit)
    TextView tv_submit;
    private SendSmsTimeCount time;
    private String user_phonenum = null;


    public static void intentTo(Context context, String title) {
        Intent intent = new Intent(context, ResetPasswordActivity.class);
        intent.putExtra(TITLE_KEY, title);
        context.startActivity(intent);
    }

    private void initView() {
        navigationBar.setTitle("重置密码");
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
                tv_submit.setEnabled(isSubmitEnabled());
            }
        });
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_submit.setEnabled(isSubmitEnabled());
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

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tv_submit.setEnabled(isSubmitEnabled());


            }
        };

        et_pwd.addTextChangedListener(textWatcher);
        et_pwd_confirm.addTextChangedListener(textWatcher);
    }

    private void initData() {
        if (LoginManager.isLogin()) {
            LoginResponse.LoginData loginData = LoginManager.getLoginData();
            if (loginData != null && loginData.user_info != null) {
                user_phonenum = loginData.user_info.mobile;
            }
        }
    }


    private void requestSendSms() {
        AppSettingManager.sendSms(et_phone_num.getText().toString().trim());
    }


    private void requestResetPassword() {
        startLoading();
        CommonApis.userChangePwd(et_phone_num.getText().toString().trim(), et_pwd.getText().toString().trim(),
                et_code.getText().toString().trim(), new ResponseCallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        endLoading();
                        CommonDialog.ActionItem doneItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                            @Override
                            public void onAction() {
                                if (LoginManager.isLogin()) {
                                    ActivityManager.getInstance().popAllActivity();
                                    LoginManager.logout(false);
                                    LoginActivity.intentTo(ResetPasswordActivity.this);
                                    ResetPasswordActivity.this.finish();
                                } else {
                                    ResetPasswordActivity.this.finish();
                                }
                            }
                        });
                        ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                        actionList.add(doneItem);
                        CommonDialog.showDialog(ResetPasswordActivity.this, "提示", "密码修改成功！", actionList);

                    }

                    @Override
                    public void onFail(int statusCode, @Nullable BaseResponse httpResponse, @Nullable Throwable error) {
                        endLoading();
                        if (statusCode != BaseResponse.NETWORK_ERROR) {
                            ToastUtil.showToast(httpResponse.getErrmsg());
                        } else {
                            ToastUtil.showToast("网络连接异常");
                        }
                    }
                });
    }


    private boolean isSubmitEnabled() {
        return et_phone_num.getText().length() == 11 && !TextUtils.isEmpty(et_code.getText()) && et_pwd.getText().length() > 5 && et_pwd_confirm.getText().length() > 5;
    }

    @OnClick({R.id.tv_send_sms, R.id.tv_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_send_sms:
                if (!TextUtils.isEmpty(user_phonenum) && !user_phonenum.equals(et_phone_num.getText().toString())) {
                    CommonDialog.ActionItem doneItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                        @Override
                        public void onAction() {
                        }
                    });
                    ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                    actionList.add(doneItem);
                    CommonDialog.showDialog(ResetPasswordActivity.this, "提示", "请输入正确的手机号码", actionList);
                } else {
                    requestSendSms();
                    time.start();
                }
                break;
            case R.id.tv_submit:
                String pwdstr = et_pwd.getText().toString();
                CommonDialog.ActionItem doneItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                    @Override
                    public void onAction() {
                    }
                });
                ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                actionList.add(doneItem);
                if (!TextUtils.isEmpty(user_phonenum) && !user_phonenum.equals(et_phone_num.getText().toString())) {
                    CommonDialog.showDialog(ResetPasswordActivity.this, "提示", "请输入正确的手机号码", actionList);
                } else if (!pwdstr.equals(et_pwd_confirm.getText().toString())) {
                    CommonDialog.showDialog(ResetPasswordActivity.this, "提示", "密码输入不一致", actionList);

                } else {
                    requestResetPassword();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);
        initView();
        initData();
    }
}

