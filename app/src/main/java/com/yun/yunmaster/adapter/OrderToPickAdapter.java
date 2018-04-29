package com.yun.yunmaster.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.roundview.RoundTextView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.network.base.presenter.BaseRecyclerAdapter;
import com.yun.yunmaster.utils.CommonCallback;
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
    protected void convert(final BaseViewHolder helper, final OrderItem item) {
        helper.setText(R.id.timeTextView, item.time);
        helper.setText(R.id.dateTextView, item.date);
        helper.setText(R.id.addressTextView, item.detail_address.address);
        helper.setText(R.id.priceTextView, "￥" + item.total_price);
        helper.setText(R.id.vehicleNumTextView, "" + item.transport_times);
        helper.setText(R.id.timeSlotTextView, item.isAm() ? "上午" : "下午");
        ImageView timeSlotImageView = helper.getView(R.id.timeSlotImageView);
        timeSlotImageView.setImageDrawable(ResourceUtil.getDrawable(mContext, item.isAm() ? R.drawable.am : R.drawable.pm));

        int color = R.color.accept_color;
        String takeTitle = null;
        if(item.is_get_order == OrderItem.GET_ORDER_NORMAL){
            takeTitle = "抢单";
        }
        else if(item.is_get_order == OrderItem.GET_ORDER_GET){
            takeTitle = "抢单\n成功";
            color = R.color.color_blue;
        }
        else if(item.is_get_order == OrderItem.GET_ORDER_FAILED){
            takeTitle = "未抢到";
            color = R.color.color_blue;
        }
        RoundTextView acceptButton = helper.getView(R.id.acceptButton);
        acceptButton.setText(takeTitle);
        acceptButton.setTextColor(ResourceUtil.getColor(mContext, color));
        acceptButton.getDelegate().setStrokeColor(ResourceUtil.getColor(mContext, color));
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.is_get_order == OrderItem.GET_ORDER_NORMAL){
                    item.takeOrder(mContext, new CommonCallback() {
                        @Override
                        public void onFinish(boolean success) {
                            convert(helper, item);
                        }

                        @Override
                        public void onCallback() {

                        }
                    });
                }
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
