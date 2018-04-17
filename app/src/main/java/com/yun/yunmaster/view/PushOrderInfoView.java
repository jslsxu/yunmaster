package com.yun.yunmaster.view;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.activity.OrderDetailActivity;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.utils.CommonCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by jslsxu on 2017/9/26.
 */

public class PushOrderInfoView extends RelativeLayout {

    public static Dialog currentDialog = null;
    @BindView(R.id.timeTextView)
    TextView timeTextView;
    @BindView(R.id.addressTextView)
    TextView addressTextView;
    @BindView(R.id.vehicleTextView)
    TextView vehicleTextView;
    @BindView(R.id.detailButton)
    RoundTextView detailButton;
    @BindView(R.id.cancelButton)
    RoundTextView cancelButton;
    @BindView(R.id.takeButton)
    RoundTextView takeButton;

    private Context mContext;
    private OrderItem mOrderItem;
    private CommonCallback callback;

    public PushOrderInfoView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PushOrderInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.push_order_info_view, this);
        ButterKnife.bind(this);
    }

    public void setOrderItem(OrderItem orderItem) {
        this.mOrderItem = orderItem;
        timeTextView.setText(this.mOrderItem.time);
        addressTextView.setText(this.mOrderItem.address);
        vehicleTextView.setText("￥" + this.mOrderItem.total_price);
    }

    public void setCallback(CommonCallback callback){
        this.callback = callback;
    }

    public OrderItem getOrderItem() {
        return this.mOrderItem;
    }

    @OnClick({R.id.detailButton, R.id.cancelButton, R.id.takeButton})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.detailButton: {
                OrderDetailActivity.intentTo(mContext, mOrderItem.oid);
                if(this.callback != null){
                    this.callback.onCallback();
                }
            }
                break;
            case R.id.cancelButton:{
                if(this.callback != null){
                    this.callback.onCallback();
                }
            }
                break;
            case R.id.takeButton:{
                mOrderItem.takeOrder(mContext, new CommonCallback() {
                    @Override
                    public void onFinish(boolean success) {
                        if(callback != null){
                            callback.onCallback();
                        }
                    }

                    @Override
                    public void onCallback() {

                    }
                });
            }
                break;
        }
    }

    public static void presentPushOrder(final Context context, final OrderItem orderInfo) {
        dismissCurrentDialog();
        final PushOrderInfoView orderInfoView = new PushOrderInfoView(context);
        orderInfoView.setCallback(new CommonCallback() {
            @Override
            public void onFinish(boolean success) {

            }

            @Override
            public void onCallback() {
                dismissCurrentDialog();
            }
        });
        orderInfoView.setOrderItem(orderInfo);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        orderInfoView.setLayoutParams(layoutParams);

        currentDialog = CommonDialog.showWithContentView(context, orderInfoView);
        currentDialog.show();
    }

    public static void dismissCurrentDialog() {
        if (currentDialog != null) {
            currentDialog.dismiss();
            currentDialog = null;
        }
    }
}
