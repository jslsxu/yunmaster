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
import com.yun.yunmaster.model.OrderPickInfo;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.OrderApis;
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
    @BindView(R.id.acceptButton)
    RoundTextView acceptButton;
    private Context mContext;
    private OrderPickInfo mOrderInfo;

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
                takeOrder(mOrderInfo.oid);
            }
        });
    }

    public void setOrder(OrderPickInfo order) {
        this.mOrderInfo = order;

    }

    private void takeOrder(final String oid) {
        final KProgressHUD mProgress = KProgressHUD.create(mContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        mProgress.setCancellable(false);
        mProgress.show();
        OrderApis.takeOrder(oid, new ResponseCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseData) {
                mProgress.dismiss();
                EventBus.getDefault().post(new EventBusEvent.OrderStatusChangedEvent());
                OrderDetailActivity.intentTo(mContext, oid);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                mProgress.dismiss();
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });
    }
}
