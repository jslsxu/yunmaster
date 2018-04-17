package com.yun.yunmaster.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.yunmaster.R;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.network.base.presenter.BaseRecyclerAdapter;

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
        helper.setText(R.id.addressTextView, item.address);
        helper.setText(R.id.priceTextView, "￥" + item.total_price);
        helper.setText(R.id.vehicleNumTextView, "" + item.transport_times);
        TextView acceptButton = helper.getView(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.takeOrder(mContext, null);
            }
        });
    }
}
