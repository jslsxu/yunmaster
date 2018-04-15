package com.yun.yunmaster.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.network.httpapis.OrderApis;
import com.yun.yunmaster.response.ExpCodeResponse;
import com.yun.yunmaster.response.OrderDetailResponse;
import com.yun.yunmaster.utils.CameraUtils;
import com.yun.yunmaster.utils.Constants;
import com.yun.yunmaster.utils.ImageUtil;
import com.yun.yunmaster.utils.PhotoManager;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.utils.UrlParamsUtil;
import com.yun.yunmaster.view.AdjustPriceView;
import com.yun.yunmaster.view.CommonDialog;
import com.yun.yunmaster.view.CommonFeeView;
import com.yun.yunmaster.view.CustomScrollView;
import com.yun.yunmaster.view.OrderDetail.PhotoGridView;
import com.yun.yunmaster.view.OrderDetailInfoView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    @BindView(R.id.cancelButton)
    RoundTextView cancelButton;
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
//        checkTimer();
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

        if (this.orderDetail.is_cancel) {
            cancelButton.setVisibility(View.GONE);
            actionButton.setEnabled(false);
        } else {
            cancelButton.setVisibility(this.orderDetail.can_cancel ? View.VISIBLE : View.GONE);
            actionButton.setEnabled(this.orderDetail.canOperation());
        }
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

    private void cancelOrder() {
        startLoading();
        OrderApis.cancelOrder(this.orderDetail.oid, new ResponseCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseData) {
                endLoading();
                ToastUtil.showToast("订单取消成功");
                finish();
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });
    }

    private void onCancelClicked() {
        if (this.orderDetail.can_cancel) {
            CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_DESTRUCTIVE, "取消订单", new CommonDialog.ActionCallback() {
                @Override
                public void onAction() {
                    cancelOrder();
                }
            });
            CommonDialog.ActionItem quitItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "点错了", null);
            ArrayList<CommonDialog.ActionItem> list = new ArrayList<>();
            list.add(cancelItem);
            list.add(quitItem);
            CommonDialog.showDialog(this, "提示", this.orderDetail.cancel_msg, list);
        }

    }

    private void orderAction() {
        switch (this.orderDetail.step) {
            case OrderItem.ORDER_STATUS_WAITING_ACCEPT:
                takeOrder();
                break;
            case OrderItem.ORDER_STATUS_ACCEPTED:
                orderArrive();
                break;
            case OrderItem.ORDER_STATUS_ARRIVED_WAIT_CONFIRM:
                adjustPrice();
                break;
            case OrderItem.ORDER_STATUS_PAYED_WAIT_COMPLETE:
                addPhoto();
                break;
            default:
                break;
        }
    }

    private void addPhoto() {//完成，添加照片
        PhotoManager.requestPhoto(this, 1);
    }

    private void uploadImage(Bitmap bitmap) {
        startLoading();
//        AliUploadManager.getInstance().upload(bitmap, new AliUploadManager.UploadImageCallback() {
//            @Override
//            public void onSuccess(String imagePath) {
//                endLoading();
//                orderComplete(imagePath);
//            }
//
//            @Override
//            public void onFail() {
//                endLoading();
//            }
//        });
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

    private void orderComplete(String photo) {
        startLoading();
        OrderApis.orderComplete(oid, photo, new ResponseCallback<OrderDetailResponse>() {
            @Override
            public void onSuccess(OrderDetailResponse baseData) {
                endLoading();
                OrderDetail order = baseData.getOrder();
                if (order != null) {
                    setOrderDetail(order);
                }
                EventBus.getDefault().post(new EventBusEvent.OrderStatusChangedEvent());
                ToastUtil.showToast("照片上传成功");
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

    private void orderArrive() {
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


    private void orderStatusChanged() {
        EventBus.getDefault().post(new EventBusEvent.OrderStatusChangedEvent());
    }

    private void handleQrCode(String code) {
        if (TextUtils.isEmpty(code)) {
            ToastUtil.showToast("二维码为空");
            return;
        }
        startLoading();
        CommonApis.expCode(code, new ResponseCallback<ExpCodeResponse>() {
            @Override
            public void onSuccess(ExpCodeResponse baseData) {
                endLoading();
                String action = null;
                if (baseData != null && baseData.data != null) {
                    action = baseData.data.action;
                }
                if (!TextUtils.isEmpty(action)) {
                    Uri uri = Uri.parse(action);
                    String host = uri.getHost();
                    Map<String, String> params = UrlParamsUtil.URLRequest(action);
                    if (host.equals("pay")) {
                        CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_CANCEL, "取消", null);
                        CommonDialog.ActionItem confirmItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                            @Override
                            public void onAction() {
                                startLoading();
                                CommonApis.payWasteFee(oid, "", "", new ResponseCallback<BaseResponse>() {
                                    @Override
                                    public void onSuccess(BaseResponse baseData) {
                                        endLoading();
                                    }

                                    @Override
                                    public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                                        endLoading();
                                    }
                                });
                            }
                        });
                        ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                        actionList.add(cancelItem);
                        actionList.add(confirmItem);
                        CommonDialog.showDialog(OrderDetailActivity.this, "支付", action, actionList);
                    }
                }
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_CAPTURE) {
                String imagePath = PhotoManager.getImagePath();
                Bitmap bitmap = CameraUtils.getBitmapByPath(imagePath, 640, 960);
//                ImageUtil.savePhotoLibrary(this, new File(imagePath));
                if (bitmap != null) {
                    uploadImage(bitmap);
                }
            } else if (requestCode == Constants.REQUEST_CODE_IMAGE) {
                List<String> pathList = data.getStringArrayListExtra("result");
                if (pathList != null && pathList.size() > 0) {
                    Bitmap bitmap = ImageUtil.compressImage(pathList.get(0), 2);
                    uploadImage(bitmap);
                }
            }
        }
    }

    @OnClick({R.id.actionButton, R.id.cancelButton})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.actionButton:
                orderAction();
                break;
            case R.id.cancelButton:
                onCancelClicked();
                break;
        }
    }


}

