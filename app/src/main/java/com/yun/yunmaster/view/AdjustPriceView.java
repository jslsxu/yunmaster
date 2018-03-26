package com.yun.yunmaster.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.model.OrderDetail;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.OrderApis;
import com.yun.yunmaster.response.CalFeeResponse;
import com.yun.yunmaster.utils.ToastUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2017/9/25.
 */

public class AdjustPriceView extends RelativeLayout {
    @BindView(R.id.numView)
    NumOperationView numView;
    @BindView(R.id.extraFeeTextView)
    EditText extraFeeTextView;
    @BindView(R.id.totalFeeTextView)
    TextView totalFeeTextView;
    private OrderDetail mOrderDetail;
    private Context mContext;

    public AdjustPriceView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public AdjustPriceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    public void updateWithOrder(OrderDetail orderDetail) {
        this.mOrderDetail = orderDetail;
        numView.setNum(this.mOrderDetail.transport_times);
        String extra = "0";
        if (!TextUtils.isEmpty(orderDetail.other_price)) {
            extra = orderDetail.other_price;
        }
        extraFeeTextView.setText(extra);
        totalFeeTextView.setText(orderDetail.total_price);
    }

    public static void showAdjustPrice(final Context context, OrderDetail orderDetail, final AdjustPriceCallback callback) {
        final AdjustPriceView adjustView = new AdjustPriceView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        adjustView.setLayoutParams(layoutParams);
        adjustView.updateWithOrder(orderDetail);
        final Dialog dialog = CommonDialog.showWithContentView(context, adjustView);
        RoundTextView cancelButton = (RoundTextView) adjustView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        RoundTextView confirmButton = (RoundTextView) adjustView.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String totalFee = adjustView.getTotalFee();
                CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_CANCEL, "取消", null);
                CommonDialog.ActionItem confirmItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                    @Override
                    public void onAction() {
                        int times = adjustView.getTimes();
                        String extra = adjustView.getExtra();
                        if (times <= 0) {
                            ToastUtil.showToast("次数应该大于0");
                        } else {
                            if (callback != null) {
                                callback.onAdjustPriceFinish(times, extra);
                            }
                            dialog.dismiss();
                        }
                    }
                });
                ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                actionList.add(cancelItem);
                actionList.add(confirmItem);
                CommonDialog.showDialog(context, "提示", "价格只能调整一次，确定调整为" + totalFee + "元吗?", actionList);
            }
        });
        dialog.show();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.adjust_price_view, this);
        ButterKnife.bind(this);

        numView.setNumChangeListener(new NumOperationView.NumChangeListener() {
            @Override
            public void numChanged(int num) {
                calFee();
            }
        });
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calFee();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        extraFeeTextView.addTextChangedListener(textWatcher);
    }

    private void calFee() {
        int num = numView.getNum();
        String extraStr = extraFeeTextView.getText().toString().trim();
        if (!TextUtils.isEmpty(extraStr)) {
            int extraFee = Integer.parseInt(extraStr);
            OrderApis.callFee(this.mOrderDetail.oid, num, Integer.toString(extraFee), new ResponseCallback<CalFeeResponse>() {
                @Override
                public void onSuccess(CalFeeResponse baseData) {
                    totalFeeTextView.setText(baseData.data.total);
                }

                @Override
                public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {

                }
            });
        }
    }

    private int getTimes() {
        return numView.getNum();
    }

    private String getExtra() {
        String extra = extraFeeTextView.getText().toString();
        return extra;
    }

    private String getTotalFee(){
        return totalFeeTextView.getText().toString();
    }

    public interface AdjustPriceCallback {
        void onAdjustPriceFinish(int times, String extra);
    }
}
