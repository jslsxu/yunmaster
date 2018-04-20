package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.flyco.roundview.RoundTextView;
import com.umeng.analytics.MobclickAgent;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.model.UserData;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.utils.AppInfoUtil;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.LoginManager;
import com.yun.yunmaster.view.HomeOrderListView;
import com.yun.yunmaster.view.MapOrderListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.yun.yunmaster.model.UserData.ACCEPT_ORDER;
import static com.yun.yunmaster.model.UserData.NOT_ACCEPT_ORDER;

public class MainActivity extends BaseActivity {

    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.acceptButton)
    RoundTextView acceptButton;
    @BindView(R.id.homeOrderListView)
    HomeOrderListView homeOrderListView;
    @BindView(R.id.mapOrderListView)
    MapOrderListView mapOrderListView;
    @BindView(R.id.tab_control)
    RadioGroup tabControl;
    private long exitTime = 0;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
        AppSettingManager.requestUserData();
        AppInfoUtil.checkVersion(this);
    }

    private void init() {
        navigationBar.setLeftItem(R.drawable.personal, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MineActivity.intentTo(MainActivity.this);
            }
        });
        navigationBar.setRightItem(R.drawable.order_list, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OrderListActivity.intentTo(MainActivity.this);
            }
        });
        homeOrderListView.refresh();
        updateAcceptButton();
        final int tabCount = tabControl.getChildCount();
        tabControl.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                for (int i = 0; i < tabCount; i++) {
                    if (checkedId == group.getChildAt(i).getId()) {
                        selectIndex(i);
                    }
                }
            }
        });
        selectIndex(0);
    }

    private void updateAcceptButton() {
        UserData userData = AppSettingManager.getUserData();
        if (userData.order_push == ACCEPT_ORDER) {
            acceptButton.setText("停止\n接单");
        } else {
            acceptButton.setText("开始\n接单");
        }
    }

    private void selectIndex(int index) {
        if (index == 0) {
            homeOrderListView.setVisibility(View.VISIBLE);
            mapOrderListView.setVisibility(View.GONE);
        } else {
            homeOrderListView.setVisibility(View.GONE);
            mapOrderListView.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.acceptButton})
    public void onClick(View view) {
        if (view.getId() == R.id.acceptButton) {
            final UserData userData = AppSettingManager.getUserData();
            final boolean isOpen = userData.order_push == ACCEPT_ORDER;
            final int changedOpen = isOpen ? NOT_ACCEPT_ORDER : ACCEPT_ORDER;
            startLoading();
            CommonApis.setOrderPush(changedOpen, new ResponseCallback<BaseResponse>() {
                @Override
                public void onSuccess(BaseResponse baseData) {
                    endLoading();
                    userData.setOrderPush(changedOpen);
                    AppSettingManager.setUserData(userData);
                    updateAcceptButton();
                }

                @Override
                public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                    endLoading();
                }
            });
        }
    }

    public void receiveNewOrder(OrderItem orderItem){
        homeOrderListView.receiveNewOrder(orderItem);
        mapOrderListView.receiveNewOrder(orderItem);
    }

    // 监听点击回退按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MobclickAgent.onKillProcess(MainActivity.this);
                finish();

                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        LoginManager.setUserRegid();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onOrderPickListChanged(EventBusEvent.OrderPickDataChangedEvent event) {
        this.mapOrderListView.setOrderList(homeOrderListView.orderPickInfoList());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onOrderStatusChanged(EventBusEvent.OrderStatusChangedEvent event) {
        this.homeOrderListView.refresh();
    }
}

