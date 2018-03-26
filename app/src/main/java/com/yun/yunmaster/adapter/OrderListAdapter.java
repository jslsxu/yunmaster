package com.yun.yunmaster.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.roundview.RoundTextView;
import com.flyco.roundview.RoundViewDelegate;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yun.yunmaster.R;
import com.yun.yunmaster.activity.OrderDetailActivity;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.presenter.BaseRecyclerAdapter;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.OrderApis;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.CommonDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;


/**
 * Created by jslsxu on 2017/9/19.
 */

public class OrderListAdapter extends BaseRecyclerAdapter<OrderItem> {
    private Context mContext;
    private OrderCellActionInterface mOrderActionListener;

    public OrderListAdapter(Context context) {
        super(R.layout.order_cell);
        mContext = context;
    }

    public void setOrderActionLister(OrderCellActionInterface listener) {
        this.mOrderActionListener = listener;
    }

    @Override
    protected void convert(BaseViewHolder helper, final OrderItem item) {
        helper.setText(R.id.timeTextView, item.time);
        RelativeLayout addressView = helper.getView(R.id.addressView);
        if(TextUtils.isEmpty(item.address)){
            addressView.setVisibility(View.GONE);
        }
        else {
            addressView.setVisibility(View.VISIBLE);
            helper.setText(R.id.addressTextView, item.address);
        }
        helper.setText(R.id.vehicleTextView, "￥" + item.total_price);
        RoundTextView acceptButton = (RoundTextView) helper.getView(R.id.actionButton);
        final int status = item.step;
        acceptButton.setEnabled(status == OrderItem.ORDER_STATUS_WAITING_ACCEPT);
        RoundViewDelegate delegate = acceptButton.getDelegate();
        Resources resources = mContext.getResources();
        if (status == OrderItem.ORDER_STATUS_WAITING_ACCEPT) {
            delegate.setBackgroundColor(resources.getColor(R.color.color_blue));
            delegate.setBackgroundPressColor(resources.getColor(R.color.color_blue_pressed));
            acceptButton.setTextColor(resources.getColor(R.color.white));
            delegate.setStrokeColor(resources.getColor(R.color.color_blue));
        } else {
            delegate.setBackgroundColor(resources.getColor(R.color.white));
            delegate.setBackgroundPressColor(resources.getColor(R.color.color_d));
            delegate.setStrokeColor(resources.getColor(R.color.color9));
            acceptButton.setTextColor(resources.getColor(R.color.color9));
        }
        acceptButton.setText(item.statusTitle());
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status == OrderItem.ORDER_STATUS_WAITING_ACCEPT){
                    CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_CANCEL, "取消", null);
                    CommonDialog.ActionItem confirmItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                        @Override
                        public void onAction() {
                            takeOrder(item.oid);
                        }
                    });
                    ArrayList<CommonDialog.ActionItem> list = new ArrayList<>();
                    list.add(cancelItem);
                    list.add(confirmItem);
                    CommonDialog.showDialog(mContext, "提示", "确定接受此订单吗?", list);

                }
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
