package com.yun.yunmaster.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.yun.yunmaster.R;
import com.yun.yunmaster.adapter.ViewPagerAdapter;
import com.yun.yunmaster.base.BaseFragment;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.view.OrderListView;
import com.yun.yunmaster.view.TabEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2017/9/19.
 */

public class OrderListFragment extends BaseFragment {

    @BindView(R.id.navigationBar)
    NavigationBar mNavigationBar;
    @BindView(R.id.tabView)
    CommonTabLayout tabView;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refresh();
        }
    }

    private void init() {
        ArrayList<CustomTabEntity> tabList = new ArrayList<>();
        ArrayList<View> viewList = new ArrayList<>();
        String[] actionList = {"未完成订单", "已完成订单"};
        for (int i = 0; i < actionList.length; i++) {
            tabList.add(new TabEntity(actionList[i], 0, 0));
            OrderListView orderListView = new OrderListView(getContext());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            orderListView.setLayoutParams(layoutParams);
            orderListView.setType(i + 1);
            viewList.add(orderListView);
        }
        viewPager.setAdapter(new ViewPagerAdapter(viewList));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabView.setCurrentTab(position);
                pageRefresh(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabView.setTabData(tabList);
        tabView.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position, true);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void pageRefresh(int position){
        ViewPagerAdapter adapter = (ViewPagerAdapter)viewPager.getAdapter();
        OrderListView orderListView = (OrderListView)adapter.getItem(position);
        orderListView.refresh();
    }

    private void refresh(){
        ViewPagerAdapter adapter = (ViewPagerAdapter)viewPager.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++){
            OrderListView orderListView = (OrderListView)adapter.getItem(i);
            orderListView.refresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onOrderStatusChanged(EventBusEvent.OrderStatusChangedEvent event) {
        refresh();
    }
}
