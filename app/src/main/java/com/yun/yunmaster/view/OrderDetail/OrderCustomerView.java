package com.yun.yunmaster.view.OrderDetail;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yun.yunmaster.R;
import com.yun.yunmaster.application.YunApplication;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.view.CommonDialog;
import com.yun.yunmaster.view.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2017/10/24.
 */

public class OrderCustomerView extends RelativeLayout {
    @BindView(R.id.avatarView)
    ImageView avatarView;
    @BindView(R.id.nameTextView)
    TextView nameTextView;
    @BindView(R.id.mobileTextView)
    TextView mobileTextView;
    private Context mContext;
    private OrderItem.CustomerInfo mCustomerInfo;

    public OrderCustomerView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public OrderCustomerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.order_customer_view, this);
        ButterKnife.bind(this);
    }

    public void setCustomer(OrderItem.CustomerInfo customer) {
        this.mCustomerInfo = customer;
        if (this.mCustomerInfo != null) {
            avatarView.setVisibility(TextUtils.isEmpty(this.mCustomerInfo.avatar) ? View.GONE : View.VISIBLE);
            nameTextView.setVisibility(View.VISIBLE);
            mobileTextView.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(this.mCustomerInfo.avatar)) {
                Glide.with(YunApplication.getApp()).load(this.mCustomerInfo.avatar).into(avatarView);
            }
            nameTextView.setText(this.mCustomerInfo.name);
            mobileTextView.setText(this.mCustomerInfo.mobile);
        } else {
            nameTextView.setVisibility(View.GONE);
            mobileTextView.setVisibility(View.GONE);
            avatarView.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.mobileTextView})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mobileTextView:
                CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_CANCEL, "取消", null);
                CommonDialog.ActionItem confirmItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                    @Override
                    public void onAction() {
                        Utils.call(mContext, mCustomerInfo.mobile);
                    }
                });
                ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                actionList.add(cancelItem);
                actionList.add(confirmItem);
                CommonDialog.showDialog(mContext, "提示", "确定拨打电话" + this.mCustomerInfo.mobile + "吗?", actionList);

                break;
        }
    }
}
