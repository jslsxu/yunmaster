package com.yun.yunmaster.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yun.yunmaster.R;
import com.yun.yunmaster.activity.OrderDetailActivity;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.OrderApis;
import com.yun.yunmaster.utils.ResourceUtil;
import com.yun.yunmaster.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2018/4/5.
 */

public class MarkerFireView extends RelativeLayout {
    @BindView(R.id.timeTextView)
    TextView timeTextView;
    @BindView(R.id.timeSlotTextView)
    TextView timeSlotTextView;
    @BindView(R.id.dateTextView)
    TextView dateTextView;
    @BindView(R.id.timeLayout)
    LinearLayout timeLayout;
    @BindView(R.id.addressTextView)
    TextView addressTextView;
    @BindView(R.id.vehicleNumTextView)
    TextView vehicleNumTextView;
    @BindView(R.id.priceTextView)
    TextView priceTextView;
    @BindView(R.id.accessImageView)
    ImageView accessImageView;
    @BindView(R.id.newOrderTextView)
    TextView newOrderTextView;
    @BindView(R.id.acceptButton)
    RoundTextView acceptButton;
    private Context mContext;
    private OrderItem mOrderInfo;

    public MarkerFireView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public MarkerFireView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.marker_fire_view, this);
        ButterKnife.bind(this);
        acceptButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOrderInfo.takeOrder(mContext, null);
            }
        });
    }

    public void setOrder(OrderItem order) {
        this.mOrderInfo = order;
        timeTextView.setText(order.time);
        dateTextView.setText(order.date);
        addressTextView.setText(order.detail_address.address);
        vehicleNumTextView.setText(order.transport_times + "");
        priceTextView.setText("￥" + order.total_price);

        boolean isAm = order.isAm();
        if(isAm){
            timeSlotTextView.setText("上午");
            timeSlotTextView.setCompoundDrawables(ResourceUtil.getDrawable(mContext, R.drawable.am), null, null, null);
        }
        else {
            timeSlotTextView.setText("下午");
            timeSlotTextView.setCompoundDrawables(ResourceUtil.getDrawable(mContext, R.drawable.pm), null, null, null);
        }
        newOrderTextView.setVisibility(order.isNew ? View.VISIBLE : View.GONE);

    }
}
