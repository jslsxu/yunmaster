package com.yun.yunmaster.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yun.yunmaster.R;
import com.yun.yunmaster.activity.OrderDetailActivity;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.model.OrderPickInfo;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.presenter.BaseRecyclerAdapter;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.OrderApis;
import com.yun.yunmaster.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jslsxu on 2018/3/30.
 */

public class OrderToPickAdapter extends BaseRecyclerAdapter<OrderPickInfo> {
    private Context mContext;
    private OrderListAdapter.OrderCellActionInterface mOrderActionListener;

    public OrderToPickAdapter(Context context) {
        super(R.layout.order_pick_cell);
        mContext = context;
    }

    public void setOrderActionLister(OrderListAdapter.OrderCellActionInterface listener) {
        this.mOrderActionListener = listener;
    }

    @Override
    protected void convert(BaseViewHolder helper, final OrderPickInfo item) {
//        helper.setText(R.id.timeTextView, item.time);
//        RelativeLayout addressView = helper.getView(R.id.addressView);
//        if(TextUtils.isEmpty(item.address)){
//            addressView.setVisibility(View.GONE);
//        }
//        else {
//            addressView.setVisibility(View.VISIBLE);
//            helper.setText(R.id.addressTextView, item.address);
//        }
//        helper.setText(R.id.vehicleTextView, "￥" + item.total_price);
        TextView acceptButton = helper.getView(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeOrder(item.oid);
            }
        });
    }

    private void takeOrder(final String oid){
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

    public interface OrderCellActionInterface {
        void onAcceptOrder(OrderItem orderItem);
    }
}
