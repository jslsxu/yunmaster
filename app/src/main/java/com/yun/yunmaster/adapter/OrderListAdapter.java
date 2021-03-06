package com.yun.yunmaster.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.yunmaster.R;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.network.base.presenter.BaseRecyclerAdapter;
import com.yun.yunmaster.utils.ResourceUtil;


/**
 * Created by jslsxu on 2017/9/19.
 */

public class OrderListAdapter extends BaseRecyclerAdapter<OrderItem> {
    private Context mContext;

    public OrderListAdapter(Context context) {
        super(R.layout.order_cell);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final OrderItem item) {
        helper.setText(R.id.timeTextView, item.time);
        helper.setText(R.id.dateTextView, item.date);
        helper.setText(R.id.addressTextView, item.address);
        helper.setText(R.id.priceTextView, "￥" + item.total_price);
        helper.setText(R.id.vehicleNumTextView, "" + item.transport_times);
        helper.setText(R.id.stepTextView, item.statusTitle());
        helper.setText(R.id.timeSlotTextView, item.isAm() ? "上午" : "下午");
        ImageView timeSlotImageView = helper.getView(R.id.timeSlotImageView);
        timeSlotImageView.setImageDrawable(ResourceUtil.getDrawable(mContext, item.isAm() ? R.drawable.am : R.drawable.pm));
    }

}
