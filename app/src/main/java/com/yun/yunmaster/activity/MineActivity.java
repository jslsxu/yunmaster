package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.UserData;
import com.yun.yunmaster.network.base.apis.HttpApiBase;
import com.yun.yunmaster.response.PublicParamsResponse;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.LoginManager;
import com.yun.yunmaster.view.CommonDialog;
import com.yun.yunmaster.view.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by jslsxu on 2018/3/27.
 */

public class MineActivity extends BaseActivity {


    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.avatarImageView)
    ImageView avatarImageView;
    @BindView(R.id.verifyImageView)
    ImageView verifyImageView;
    @BindView(R.id.verifyTextView)
    TextView verifyTextView;
    @BindView(R.id.userNameTextView)
    TextView userNameTextView;
    @BindView(R.id.balanceTextView)
    TextView balanceTextView;
    @BindView(R.id.ll_balance)
    LinearLayout llBalance;
    @BindView(R.id.cashTextView)
    TextView cashTextView;
    @BindView(R.id.tv_car_info)
    TextView tvCarInfo;
    @BindView(R.id.rl_driver_cert)
    RelativeLayout rlDriverCert;
    @BindView(R.id.rl_my_trucks)
    RelativeLayout rlMyTrucks;
    @BindView(R.id.userProtocolView)
    RelativeLayout userProtocolView;
    @BindView(R.id.feedbackView)
    RelativeLayout feedbackView;
    @BindView(R.id.serverContactView)
    RelativeLayout serverContactView;
    @BindView(R.id.checkVersionView)
    RelativeLayout checkVersionView;
    @BindView(R.id.logoutButton)
    RoundTextView logoutButton;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, MineActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mine);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
    }

    private void init() {
        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setUserInfo();
    }

    @OnClick({R.id.logoutButton, R.id.rl_driver_cert, R.id.rl_my_trucks, R.id.ll_balance, R.id.userProtocolView, R.id.serverContactView, R.id.feedbackView, R.id.checkVersionView})
    public void onClicked(View view) {
        PublicParamsResponse.DataBean publicParams = AppSettingManager.getPublicParams();
        switch (view.getId()) {
            case R.id.logoutButton:
                ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_CANCEL, "取消", null);
                CommonDialog.ActionItem confirmItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                    @Override
                    public void onAction() {
                        LoginManager.logout(false);
                    }
                });
                actionList.add(cancelItem);
                actionList.add(confirmItem);
                CommonDialog.showDialog(this, "提示", "确定要退出登录吗?", actionList);
                break;
            case R.id.rl_driver_cert:
                DriverAuthActivity.intentTo(this);
                break;
            case R.id.rl_my_trucks:
                VehicleListActivity.intentTo(this);
                break;
            case R.id.ll_balance:
                IncomeExpensesDetailActivity.intentTo(this);
                break;
            case R.id.userProtocolView:
                String policyUrl = HttpApiBase.getSecureBaseUrl()+"protocol_list";
                CommonWebActivity.intentTo(this, policyUrl, null);
                break;
            case R.id.feedbackView: {
                FeedbackActivity.intentTo(this);
            }
            break;
            case R.id.serverContactView: {
                String serviceUrl = HttpApiBase.getSecureBaseUrl() + "service_center";
                CommonWebActivity.intentTo(this, serviceUrl, null);
            }
            break;
            case R.id.checkVersionView:
                if (publicParams != null && publicParams.getPolicy() != null && publicParams.getContact_phone() != null) {
                    Utils.call(this, publicParams.getContact_phone());
                } else {
                    AppSettingManager.requestPublicParams();
                }

                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUserDataChanged(EventBusEvent.UserDataUpdateEvent event) {
        setUserInfo();
    }

    private void setUserInfo() {
        UserData userData = AppSettingManager.getUserData();
        if (userData != null) {
            userNameTextView.setText(userData.name);

            balanceTextView.setText(userData.balance == null ? "0元" : userData.balance + "元");
            cashTextView.setText(userData.encashment == null ? "0元" : userData.encashment + "元");
            Timber.e("auth type is " + userData.auth_type);
            tvCarInfo.setText(userData.getAuthType());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppSettingManager.requestUserData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
