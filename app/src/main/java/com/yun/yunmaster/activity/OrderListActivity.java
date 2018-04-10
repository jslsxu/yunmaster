package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.view.OrderListView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2018/3/27.
 */

public class OrderListActivity extends BaseActivity {
    @BindView(R.id.navigationBar)
    NavigationBar mNavigationBar;
    @BindView(R.id.orderListView)
    OrderListView orderListView;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, OrderListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_list);
        ButterKnife.bind(this);
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        mNavigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        orderListView.setType(0);
        orderListView.refresh();
    }

}
