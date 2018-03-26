package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.adapter.VehicleListAdapter;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.VehicleItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.presenter.RecyclerViewPresenter;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.VehicleListResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class VehicleListActivity extends BaseActivity implements RecyclerViewPresenter.PresenterInterface {

    protected VehicleListAdapter mAdapter;
    protected RecyclerViewPresenter mPresenter;
    protected String start = "";
    protected Context mContext;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.emptyTextView)
    TextView emptyTextView;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.actionView)
    RelativeLayout actionView;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, VehicleListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
    }

    protected void init() {
        navigationBar.setTitle("我的车辆");
        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new VehicleListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter = new RecyclerViewPresenter();
        mPresenter.bind(mRecyclerView, mAdapter, mRefreshLayout, this, true);

        requestData(RecyclerViewPresenter.REQUEST_REFRESH);

    }

    public void refresh() {
        requestData(RecyclerViewPresenter.REQUEST_REFRESH);
    }

    @Override
    public void requestData(final int requestType) {
        startLoading();
        String from = requestType == RecyclerViewPresenter.REQUEST_REFRESH ? "" : start;
        CommonApis.getVehicleList(from, new ResponseCallback<VehicleListResponse>() {
            @Override
            public void onSuccess(VehicleListResponse baseData) {
                endLoading();
                start = baseData.data.next;
                List<VehicleItem> list = baseData.data.list;
                mPresenter.endRequest(requestType, list, baseData.data.hasMore());
                emptyTextView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                mPresenter.endRequest(requestType);
                emptyTextView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @OnClick(R.id.tv_submit)
    public void onViewClicked() {
        VehicleAuthActivity.intentTo(VehicleListActivity.this,null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onVehicleListChanged(EventBusEvent.VehicleListChangedEvent event) {
        refresh();
    }

}
