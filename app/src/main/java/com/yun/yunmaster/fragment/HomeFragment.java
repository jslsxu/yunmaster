package com.yun.yunmaster.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yun.yunmaster.R;
import com.yun.yunmaster.activity.OrderDetailActivity;
import com.yun.yunmaster.adapter.OrderListAdapter;
import com.yun.yunmaster.base.BaseFragment;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.CityInfo;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.presenter.RecyclerViewPresenter;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.network.httpapis.OrderApis;
import com.yun.yunmaster.response.OrderListResponse;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.LocationManager;
import com.yun.yunmaster.view.HomeHeaderView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by jslsxu on 2017/9/19.
 */

public class HomeFragment extends BaseFragment implements RecyclerViewPresenter.PresenterInterface {
    @BindView(R.id.navigationBar)
    NavigationBar mNavigationBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.headerView)
    HomeHeaderView headerView;
    @BindView(R.id.cityTextView)
    TextView cityTextView;
    @BindView(R.id.emptyView)
    LinearLayout emptyView;
    @BindView(R.id.addressTextView)
    TextView addressTextView;
    @BindView(R.id.addressView)
    RelativeLayout addressView;

    private OrderListAdapter mAdapter;
    private RecyclerViewPresenter mPresenter;
    private String start = "";

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

    @Override
    public void onResume() {
        super.onResume();
        refreshLocation();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_home, container, false);
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
            refreshLocation();
        }
    }

    private void init() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new OrderListAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mPresenter = new RecyclerViewPresenter();
        mPresenter.bind(mRecyclerView, mAdapter, mRefreshLayout, this, true);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Timber.e(position + "");
                OrderItem orderItem = mAdapter.getItem(position);
                OrderDetailActivity.intentTo(getContext(), orderItem.oid);
            }
        });

        CityInfo cityInfo = AppSettingManager.getCurrentCity();
        if (cityInfo != null) {
            cityTextView.setText(cityInfo.name);
            refresh();
        }
    }

    //    private void takeOrder(final String oid) {
//        startLoading();
//        CommonApis.takeOrder(oid, new ResponseCallback<BaseResponse>() {
//            @Override
//            public void onSuccess(BaseResponse baseData) {
//                endLoading();
//                OrderDetailActivity.intentTo(getContext(), oid);
//            }
//
//            @Override
//            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
//                endLoading();
//                ToastUtil.showToast(failDate.getErrmsg());
//            }
//        });
//    }
    private void refresh() {
        requestData(RecyclerViewPresenter.REQUEST_REFRESH);
    }

    private void refreshLocation(){
        LocationManager.getLocation(new LocationManager.LocationListener() {
            @Override
            public void onLocationSuccess(BDLocation location) {
                String address = location.getCity() + location.getDistrict() + location.getStreet();
                Timber.e(address);
                addressTextView.setText(address);
                CommonApis.driverLocation(location.getLatitude(), location.getLongitude(), null);
            }

            @Override
            public void onLocationFail() {

            }
        });
    }
    @Override
    public void requestData(final int requestType) {
        CityInfo cityInfo = AppSettingManager.getCurrentCity();
        if (cityInfo == null) {
            return;
        }
        String from = requestType == RecyclerViewPresenter.REQUEST_REFRESH ? "" : start;
        OrderApis.getAllOrderList(from, new ResponseCallback<OrderListResponse>() {
            @Override
            public void onSuccess(OrderListResponse baseData) {
                start = baseData.data.next;
                mPresenter.endRequest(requestType, baseData.data.list, baseData.data.hasMore());
                emptyView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                mPresenter.endRequest(requestType);
                emptyView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onNewOrderCome(EventBusEvent.NewOrderEvent event) {
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUserDataChanged(EventBusEvent.UserDataUpdateEvent event) {
        headerView.updateWithUserData(AppSettingManager.getUserData());
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onOrderStatusChanged(EventBusEvent.OrderStatusChangedEvent event) {
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCityChanged(EventBusEvent.CityChangedEvent event) {
        CityInfo cityInfo = AppSettingManager.getCurrentCity();
        if (cityInfo != null) {
            cityTextView.setText(cityInfo.name);
            mAdapter.setData(null, true);
            refresh();
        }
    }


    @OnClick({R.id.cityTextView})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cityTextView:
//                CityListActivity.intentTo(getContext());
                break;
        }
    }
}
