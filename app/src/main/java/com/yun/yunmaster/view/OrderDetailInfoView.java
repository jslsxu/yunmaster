package com.yun.yunmaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.model.OrderDetail;
import com.yun.yunmaster.model.OrderItem;

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
    TextView commentTextView;
    @BindView(R.id.customerView)
    LinearLayout customerView;

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
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        OrderItem.CustomerInfo customerInfo = orderDetail.customer;
        if(customerInfo != null){
            customerView.setVisibility(View.VISIBLE);
            nameTextView.setText(customerInfo.name);
            mobileTextView.setText(customerInfo.mobile);
        }
        else {
            customerView.setVisibility(View.GONE);
        }
    }
}
