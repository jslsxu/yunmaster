package com.yun.yunmaster.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.yunmaster.R;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.network.base.presenter.BaseRecyclerAdapter;
import com.yun.yunmaster.utils.ResourceUtil;

/**
 * Created by jslsxu on 2018/3/30.
 */

public class OrderToPickAdapter extends BaseRecyclerAdapter<OrderItem> {
    private Context mContext;

    public OrderToPickAdapter(Context context) {
        super(R.layout.order_pick_cell);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final OrderItem item) {
        helper.setText(R.id.timeTextView, item.time);
        helper.setText(R.id.dateTextView, item.date);
        helper.setText(R.id.addressTextView, item.detail_address.address);
        helper.setText(R.id.priceTextView, "￥" + item.total_price);
        helper.setText(R.id.vehicleNumTextView, "" + item.transport_times);

        TextView timeSlotTextView = helper.getView(R.id.timeSlotTextView);
        boolean isAm = item.isAm();
        if(isAm){
            timeSlotTextView.setText("上午");
            timeSlotTextView.setCompoundDrawables(ResourceUtil.getDrawable(mContext, R.drawable.am), null, null, null);
        }
        else {
            timeSlotTextView.setText("下午");
            timeSlotTextView.setCompoundDrawables(ResourceUtil.getDrawable(mContext, R.drawable.pm), null, null, null);
        }

        TextView acceptButton = helper.getView(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.takeOrder(mContext, null);
            }
        });

        TextView newTextView = helper.getView(R.id.newOrderTextView);
        if(item.isNew){
            newTextView.setVisibility(View.VISIBLE);
        }
        else {
            newTextView.setVisibility(View.GONE);
        }
    }
}
