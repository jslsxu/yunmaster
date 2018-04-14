package com.yun.yunmaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.yun.yunmaster.R;
import com.yun.yunmaster.model.OrderDetail;

import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2018/4/14.
 */

public class OrderDetailInfoView extends RelativeLayout {
    Context mContext;
    OrderDetail mOrderDetail;
    public OrderDetailInfoView(Context context){
        super(context);
        mContext = context;
        init();
    }

    public OrderDetailInfoView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        mContext = context;
        init();
    }

    private void init(){
        LayoutInflater.from(mContext).inflate(R.layout.order_info_view, this);
        ButterKnife.bind(this);
    }

    public void setOrderDetail(OrderDetail orderDetail){

    }
}
