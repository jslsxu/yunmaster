package com.yun.yunmaster.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.flyco.roundview.RoundLinearLayout;
import com.flyco.roundview.RoundTextView;
import com.flyco.roundview.RoundViewDelegate;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.Location;
import com.yun.yunmaster.model.OrderDetail;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.OrderApis;
import com.yun.yunmaster.network.httpapis.UploadApis;
import com.yun.yunmaster.response.OrderCompletePhotoResponse;
import com.yun.yunmaster.response.OrderDetailResponse;
import com.yun.yunmaster.utils.CommonCallback;
import com.yun.yunmaster.utils.Constants;
import com.yun.yunmaster.utils.LocationManager;
import com.yun.yunmaster.utils.MyDistanceUtil;
import com.yun.yunmaster.utils.PhotoManager;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.CommonDialog;
import com.yun.yunmaster.view.CommonFeeView;
import com.yun.yunmaster.view.CustomScrollView;
import com.yun.yunmaster.view.OrderDetail.PhotoGridView;
import com.yun.yunmaster.view.OrderDetailInfoView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class OrderDetailActivity extends BaseActivity {
    public static final String OID_KEY = "OID_KEY";
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.photoLayout)
    RoundLinearLayout photoLayout;
    @BindView(R.id.completePhotoLayout)
    RoundLinearLayout completePhotoLayout;
    @BindView(R.id.feeView)
    CommonFeeView feeView;
    @BindView(R.id.scrollView)
    CustomScrollView scrollView;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.actionButton)
    RoundTextView actionButton;
    @BindView(R.id.actionView)
    RelativeLayout actionView;
    @BindView(R.id.photoGridView)
    PhotoGridView photoGridView;
    @BindView(R.id.completePhotoGridView)
    PhotoGridView completePhotoGridView;
    @BindView(R.id.hintTextView)
    TextView hintTextView;
    @BindView(R.id.mapView)
    MapView mapView;
    @BindView(R.id.orderInfoView)
    OrderDetailInfoView orderInfoView;

    private boolean mapSet = false;
    private String oid;
    private OrderDetail orderDetail;
    private Timer timer;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            requestOrderDetail(false);
        }
    };
    private Timer updateLocationTimer;
    private TimerTask updateLocationTask = new TimerTask() {
        @Override
        public void run() {
            LocationManager.getLocation(new LocationManager.LocationListener() {
                @Override
                public void onLocationSuccess(BDLocation location) {
                    if (location != null) {
                        MyLocationData.Builder builder = new MyLocationData.Builder();
                        builder.latitude(location.getLatitude());
                        builder.longitude(location.getLongitude());
                        MyLocationData data = builder.build();
                        mapView.getMap().setMyLocationData(data);
                        Location myLocation = new Location(location.getLatitude(), location.getLongitude());
                        Location orderLocation = new Location(orderDetail.detail_address.lat, orderDetail.detail_address.lng);
                        LatLng center = new LatLng((myLocation.getLat() + orderLocation.getLat()) / 2, (myLocation.getLng() + orderLocation.getLng()) / 2);
                        int zoomLevel = MyDistanceUtil.getZoomForMap(myLocation, orderLocation);
                        mapView.getMap().setMapStatus(MapStatusUpdateFactory.newLatLng(center));
                        mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(zoomLevel).build()));
                        OrderApis.updateLocation(oid, location.getLatitude(), location.getLongitude(), null);
                    }
                }

                @Override
                public void onLocationFail() {

                }
            });
        }
    };

    public static void intentTo(Context context, String oid) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(OID_KEY, oid);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }

        if (updateLocationTimer != null) {
            updateLocationTimer.cancel();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        oid = intent.getStringExtra(OID_KEY);
        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestOrderDetail(true);
            }
        });
        requestOrderDetail(true);
    }

    private void requestOrderDetail(final boolean onEnter) {
        if (onEnter) {
            startLoading();
        }
        OrderApis.orderDetail(oid, new ResponseCallback<OrderDetailResponse>() {
            @Override
            public void onSuccess(OrderDetailResponse baseData) {
                endLoading();
                refreshLayout.setRefreshing(false);
                if (baseData != null && baseData.data != null && baseData.data.order != null) {
                    setOrderDetail(baseData.data.order);
                }
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                refreshLayout.setRefreshing(false);
                if (onEnter) {
                    ToastUtil.showToast(failDate.getErrmsg());
                }
            }
        });
    }

    private void checkNeedUpdateLocation() {
        if (orderDetail.needUpdateLocation()) {
            if (updateLocationTimer == null) {
                mapView.getMap().setMyLocationEnabled(true);
                updateLocationTimer = new Timer();
                updateLocationTimer.schedule(updateLocationTask, 3000, 3000);
            }
        } else {
            mapView.getMap().setMyLocationEnabled(false);
            if (updateLocationTimer != null) {
                updateLocationTimer.cancel();
                updateLocationTimer = null;
            }
        }
    }

    private void checkTimer() {
        if (orderDetail.needTimer()) {
            if (timer == null) {
                timer = new Timer();
                timer.schedule(timerTask, 3000, 3000);
            }
        } else {
            if (timer != null) {
                timer.cancel();
            }
        }
    }

    public void setOrderDetail(OrderDetail orderDetailInfo) {
        if (this.orderDetail != null && this.orderDetail.step != orderDetailInfo.step) {
            orderStatusChanged();
        }
        this.orderDetail = orderDetailInfo;
        scrollView.setVisibility(View.VISIBLE);
        actionView.setVisibility(View.VISIBLE);
        orderInfoView.setOrderDetail(this.orderDetail);
        setupMapView();

        hintTextView.setText("费用为预估费用，司机到达现场后可根据具体情况调整价格");

        feeView.setFeeList(this.orderDetail.fee_items);
        final List<String> photoList = this.orderDetail.photo;
        if (photoList == null || photoList.size() == 0) {
            photoLayout.setVisibility(View.GONE);
        } else {
            photoLayout.setVisibility(View.VISIBLE);
            photoGridView.setPhotoList(photoList);
        }

        final List<String> completePhotoList = this.orderDetail.complete_photo;
        if (completePhotoList == null || completePhotoList.size() == 0) {
            completePhotoLayout.setVisibility(View.GONE);
        } else {
            completePhotoLayout.setVisibility(View.VISIBLE);
            completePhotoGridView.setPhotoList(completePhotoList);
        }

        actionButton.setEnabled(orderDetail.canOperation());

        RoundViewDelegate delegate = actionButton.getDelegate();
        Resources resources = getResources();
        if (this.orderDetail.canOperation()) {
            delegate.setStrokeColor(resources.getColor(R.color.color_blue));
            delegate.setBackgroundColor(resources.getColor(R.color.color_blue));
            delegate.setBackgroundPressColor(resources.getColor(R.color.color_blue_pressed));
            actionButton.setTextColor(resources.getColor(R.color.white));
        } else {
            delegate.setStrokeColor(resources.getColor(R.color.color9));
            delegate.setBackgroundColor(resources.getColor(R.color.white));
            delegate.setBackgroundPressColor(resources.getColor(R.color.color_d));
            actionButton.setTextColor(resources.getColor(R.color.color9));
        }
        actionButton.setText(this.orderDetail.actionTitle());
//        checkTimer();
        checkNeedUpdateLocation();
    }

    private void setupMapView() {
        if (orderDetail.needMap()) {
            mapView.setVisibility(View.VISIBLE);
            if (!mapSet) {
                mapSet = true;
                LatLng point = new LatLng(this.orderDetail.detail_address.lat, this.orderDetail.detail_address.lng);
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.order_pin);
                OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);

                mapView.getMap().addOverlay(option);
                mapView.getMap().setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(16).build()));
                mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
            }
        } else {
            mapView.setVisibility(View.GONE);
        }
    }


    private void orderAction() {
        switch (this.orderDetail.step) {
            case OrderItem.ORDER_STATUS_WAITING_ACCEPT:
                takeOrder();
                break;
            case OrderItem.ORDER_STATUS_ACCEPTED:
            case OrderItem.ORDER_STATUS_SET_OUT:
                updateOrderStatus();
                break;
            case OrderItem.ORDER_STATUS_ARRIVED://已到达，上传完成照片和调价
            {
                CompleteOrderActivity.intentTo(this, orderDetail);
            }
            break;
            default:
                break;
        }
    }

    private void updateOrderStatus() {
        startLoading();
        final int step = orderDetail.nextStep();
        OrderApis.updateOrderStatus(oid, step, new ResponseCallback<OrderDetailResponse>() {
            @Override
            public void onSuccess(OrderDetailResponse baseData) {
                endLoading();
                orderDetail.step = step;
                setOrderDetail(orderDetail);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });
    }

    /**
     * 网络请求
     */
    private void takeOrder() {
        orderDetail.takeOrder(this, new CommonCallback() {
            @Override
            public void onFinish(boolean success) {
                requestOrderDetail(false);
            }

            @Override
            public void onCallback() {

            }
        });
    }


    private void orderStatusChanged() {
        EventBus.getDefault().post(new EventBusEvent.OrderStatusChangedEvent());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.COMPLETE_ORDER_REQUEST_CODE) {
                OrderDetail completeOrderDetail = (OrderDetail) data.getSerializableExtra(CompleteOrderActivity.ORDER_KEY);
                setOrderDetail(completeOrderDetail);
            }
        }
    }

    @OnClick({R.id.actionButton})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.actionButton:
                orderAction();
                break;
        }
    }
}

