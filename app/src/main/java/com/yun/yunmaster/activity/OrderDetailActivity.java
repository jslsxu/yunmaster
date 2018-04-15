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

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.flyco.roundview.RoundLinearLayout;
import com.flyco.roundview.RoundTextView;
import com.flyco.roundview.RoundViewDelegate;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.OrderDetail;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.OrderApis;
import com.yun.yunmaster.network.httpapis.UploadApis;
import com.yun.yunmaster.response.OrderCompletePhotoResponse;
import com.yun.yunmaster.response.OrderDetailResponse;
import com.yun.yunmaster.utils.Constants;
import com.yun.yunmaster.utils.PhotoManager;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.AdjustPriceView;
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

    private String oid;
    private OrderDetail orderDetail;
    private Timer timer;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            requestOrderDetail(false);
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
        checkTimer();
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
    }

    private void setupMapView() {
        LatLng point = new LatLng(this.orderDetail.detail_address.lat, this.orderDetail.detail_address.lng);
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.order_pin);
        OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);

        mapView.getMap().addOverlay(option);
        mapView.getMap().setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(16).build()));
        mapView.getMap().animateMapStatus(MapStatusUpdateFactory.newLatLng(point));
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
                if (orderDetail.needUploadFinishPhoto()) {
                    addPhoto();
                } else if (orderDetail.can_change_price) {
                    adjustPrice();
                }
            }
            break;
            default:
                break;
        }
    }

    private void updateOrderStatus() {
        startLoading();
        OrderApis.updateOrderStatus(oid, new ResponseCallback<OrderDetailResponse>() {
            @Override
            public void onSuccess(OrderDetailResponse baseData) {
                endLoading();
                OrderDetail order = baseData.getOrder();
                if (order != null) {
                    setOrderDetail(order);
                }
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });
    }

    private void addPhoto() {//完成，添加照片
        PhotoManager.requestPhoto(this, 1);
    }


    /**
     * 网络请求
     */
    private void takeOrder() {
        CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_CANCEL, "取消", null);
        CommonDialog.ActionItem confirmItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
            @Override
            public void onAction() {
                startLoading();
                OrderApis.takeOrder(oid, new ResponseCallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse baseData) {
                        endLoading();
                        requestOrderDetail(false);
                    }

                    @Override
                    public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                        endLoading();
                        ToastUtil.showToast(failDate.getErrmsg());
                    }
                });
            }
        });
        ArrayList<CommonDialog.ActionItem> list = new ArrayList<>();
        list.add(cancelItem);
        list.add(confirmItem);
        CommonDialog.showDialog(this, "提示", "确定接受此订单吗?", list);

    }

    private void orderComplete(String imagePath) {
        startLoading();
        UploadApis.orderComplete(oid, imagePath, new ResponseCallback<OrderCompletePhotoResponse>() {
            @Override
            public void onSuccess(OrderCompletePhotoResponse baseData) {
                endLoading();
                orderDetail.complete_photo = baseData.data.complete_photos;
                setOrderDetail(orderDetail);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });
    }

    private void adjustPrice() {
        AdjustPriceView.showAdjustPrice(this, this.orderDetail, new AdjustPriceView.AdjustPriceCallback() {
            @Override
            public void onAdjustPriceFinish(int times, String extra) {
                adjustPrice(times, extra);
            }
        });
    }

    private void adjustPrice(int times, String extra) {
        startLoading();
        OrderApis.modifyPrice(oid, times, extra, new ResponseCallback<OrderDetailResponse>() {
            @Override
            public void onSuccess(OrderDetailResponse baseData) {
                endLoading();
                ToastUtil.showToast("调整价格成功");
                OrderDetail order = baseData.getOrder();
                if (order != null) {
                    setOrderDetail(order);
                }
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(failDate.getErrmsg());
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
            if (requestCode == Constants.REQUEST_CODE_CAPTURE) {
                String imagePath = PhotoManager.getImagePath();
                orderComplete(imagePath);
            } else if (requestCode == Constants.REQUEST_CODE_IMAGE) {
                List<String> pathList = data.getStringArrayListExtra("result");
                if (pathList != null && pathList.size() > 0) {
                    orderComplete(pathList.get(0));
                }
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

