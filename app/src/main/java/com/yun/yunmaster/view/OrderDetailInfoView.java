package com.yun.yunmaster.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.flyco.roundview.RoundTextView;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.model.OrderDetail;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.utils.LocationManager;
import com.yun.yunmaster.utils.ResourceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2018/4/14.
 */

public class OrderDetailInfoView extends RelativeLayout {
    Context mContext;
    OrderDetail mOrderDetail;
    @BindView(R.id.avatarImageView)
    RoundedImageView avatarImageView;
    @BindView(R.id.nameTextView)
    TextView nameTextView;
    @BindView(R.id.mobileTextView)
    TextView mobileTextView;
    @BindView(R.id.timeTextView)
    TextView timeTextView;
    @BindView(R.id.addressTextView)
    TextView addressTextView;
    @BindView(R.id.numTextView)
    TextView numTextView;
    @BindView(R.id.priceTextView)
    TextView priceTextView;
    @BindView(R.id.commentTextView)
    RoundTextView commentTextView;
    @BindView(R.id.customerView)
    LinearLayout customerView;
    @BindView(R.id.locationImageView)
    ImageView locationImageView;

    public OrderDetailInfoView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public OrderDetailInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.order_info_view, this);
        ButterKnife.bind(this);

        mobileTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.call(mContext, mOrderDetail.customer.mobile);
            }
        });
        locationImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final KProgressHUD mProgress = KProgressHUD.create(mContext)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
                mProgress.setCancellable(false);
                mProgress.show();
                LocationManager.getLocation(new LocationManager.LocationListener() {
                    @Override
                    public void onLocationSuccess(BDLocation location) {
                        mProgress.dismiss();
                        LatLng start = new LatLng(location.getLatitude(), location.getLongitude());
                        LatLng end = new LatLng(mOrderDetail.detail_address.lat, mOrderDetail.detail_address.lng);
                        MapsDialog msgDialog = new MapsDialog(mContext, start, end);
                        msgDialog.show();
                    }

                    @Override
                    public void onLocationFail() {
                        mProgress.dismiss();
                    }
                });
            }
        });
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        mOrderDetail = orderDetail;
        OrderItem.CustomerInfo customerInfo = orderDetail.customer;
        if (customerInfo != null) {
            customerView.setVisibility(View.VISIBLE);
            nameTextView.setText(customerInfo.name);
            if(!TextUtils.isEmpty(customerInfo.mobile)) {
                mobileTextView.setVisibility(View.VISIBLE);
                mobileTextView.setText(customerInfo.mobile);
            }
            else {
                mobileTextView.setVisibility(View.GONE);
            }
        } else {
            customerView.setVisibility(View.GONE);
        }

        timeTextView.setText(orderDetail.date + " " + orderDetail.time);
        addressTextView.setText(orderDetail.detail_address.address);
        numTextView.setText(orderDetail.transport_times + "");
        priceTextView.setText("￥" + orderDetail.total_price);
        if (TextUtils.isEmpty(orderDetail.comment)) {
            commentTextView.setVisibility(View.GONE);
        } else {
            commentTextView.setVisibility(View.VISIBLE);
            commentTextView.setText(orderDetail.comment);
        }
    }
}
