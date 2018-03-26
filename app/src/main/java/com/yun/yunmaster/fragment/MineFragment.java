package com.yun.yunmaster.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.activity.CommonWebActivity;
import com.yun.yunmaster.activity.DriverAuthActivity;
import com.yun.yunmaster.activity.DriverInfoActivity;
import com.yun.yunmaster.activity.IncomeExpensesDetailActivity;
import com.yun.yunmaster.activity.VehicleListActivity;
import com.yun.yunmaster.base.BaseFragment;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.UserData;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.PublicParamsResponse;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.LoginManager;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.CommonDialog;
import com.yun.yunmaster.view.Utils;
import com.zcw.togglebutton.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by jslsxu on 2017/9/19.
 */

public class MineFragment extends BaseFragment {
    @BindView(R.id.avatarImageView)
    ImageView avatarImageView;
    @BindView(R.id.userNameTextView)
    TextView userNameTextView;
    @BindView(R.id.mobileTextView)
    TextView mobileTextView;
    @BindView(R.id.balanceTextView)
    TextView balanceTextView;
    @BindView(R.id.cashTextView)
    TextView cashTextView;
    @BindView(R.id.logoutButton)
    TextView logoutButton;
    @BindView(R.id.togglebutton)
    ToggleButton togglebutton;
    @BindView(R.id.tv_car_info)
    TextView tvCarInfo;
    @BindView(R.id.rl_is_order_push)
    RelativeLayout rlIsOrderPush;
    @BindView(R.id.rl_upload_muck_address)
    RelativeLayout rlUploadMuckAddress;
    @BindView(R.id.rl_charge_standard)
    RelativeLayout rlChargeStandard;
    @BindView(R.id.rl_contact_us)
    RelativeLayout rlContactUs;
    @BindView(R.id.ll_balance)
    LinearLayout llBalance;
    @BindView(R.id.inviteView)
    RelativeLayout inviteView;


    private void init() {
        setUserInfo();
        togglebutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                UserData userData = AppSettingManager.getUserData();
                requestSetOrderPush(userData.order_push == 2);

                return true;
            }
        });

    }

    @OnClick({R.id.logoutButton, R.id.rl_driver_cert, R.id.rl_my_trucks, R.id.rl_upload_muck_address, R.id.rl_charge_standard, R.id.rl_contact_us, R.id.ll_balance, R.id.inviteView, R.id.feeView})
    public void onClicked(View view) {
        Context context = getContext();
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
                CommonDialog.showDialog(context, "提示", "确定要退出登录吗?", actionList);
                break;
            case R.id.rl_driver_cert:
                UserData userData = AppSettingManager.getUserData();
                if (userData.isNeedAuth()) {
                    DriverAuthActivity.intentTo(getActivity());
                    return;
                }
                if (userData.auth_type == 2 || userData.auth_type == 4) {
                    DriverInfoActivity.intentTo(getActivity());
                }

                break;
            case R.id.rl_my_trucks:
                VehicleListActivity.intentTo(getActivity());
                break;

            case R.id.ll_balance:
                IncomeExpensesDetailActivity.intentTo(getActivity());
                break;
            case R.id.rl_upload_muck_address:
//                AddYardActivity.intentTo(getActivity());
                break;
            case R.id.rl_charge_standard:
                if (publicParams != null && publicParams.getPolicy() != null && publicParams.getPolicy().register_policy != null) {
                    String policyUrl = publicParams.getPolicy().register_policy;
                    CommonWebActivity.intentTo(getActivity(), policyUrl, "好运服务协议");
                } else {
                    AppSettingManager.requestPublicParams();
                }
                break;
            case R.id.inviteView:
            {
                if (publicParams != null && publicParams.getPolicy() != null && !TextUtils.isEmpty(publicParams.getPolicy().invite_url)){
                    CommonWebActivity.intentTo(getActivity(), publicParams.getPolicy().invite_url, null);
                }
                else {
                    AppSettingManager.requestPublicParams();
                }
            }
                break;
            case R.id.feeView:
            {
                if (publicParams != null && publicParams.getPolicy() != null && !TextUtils.isEmpty(publicParams.getPolicy().fee_policy)){
                    CommonWebActivity.intentTo(getActivity(), publicParams.getPolicy().fee_policy, "收费标准");
                }
                else {
                    AppSettingManager.requestPublicParams();
                }
            }
            break;
            case R.id.rl_contact_us:
                if (publicParams != null && publicParams.getPolicy() != null && publicParams.getContact_phone() != null) {
                    Utils.call(getContext(), publicParams.getContact_phone());
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
            mobileTextView.setText(userData.mobile);

            balanceTextView.setText(userData.balance == null ? "0元" : userData.balance + "元");
            cashTextView.setText(userData.encashment == null ? "0元" : userData.encashment + "元");

            Timber.d("999998" + userData.order_push);
            if (userData.order_push == 2) {
                togglebutton.setToggleOff();
            } else {
                togglebutton.setToggleOn();
            }
            tvCarInfo.setText(userData.getAuthType());


        }
    }


    public void requestSetOrderPush(final boolean isOpen) {
        startLoading();
        CommonApis.setOrderPush(isOpen, new ResponseCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseData) {
                UserData userData = AppSettingManager.getUserData();

                if (isOpen) {
                    togglebutton.setToggleOn();
                    userData.setOrderPush(1);
                } else {
                    togglebutton.setToggleOff();
                    userData.setOrderPush(2);
                }
                AppSettingManager.setUserData(userData);
                endLoading();
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                if (failDate.getErrno() == BaseResponse.NETWORK_ERROR) {
                    ToastUtil.showToast("网络请求失败，请稍后再试");
                } else {
                    ToastUtil.showToast(failDate.getErrmsg());
                }
                endLoading();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            AppSettingManager.requestUserData();
        }
    }

}
