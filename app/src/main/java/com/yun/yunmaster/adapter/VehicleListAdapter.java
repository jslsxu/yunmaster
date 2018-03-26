package com.yun.yunmaster.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.flyco.roundview.RoundTextView;
import com.flyco.roundview.RoundViewDelegate;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.yun.yunmaster.R;
import com.yun.yunmaster.activity.VehicleAuthActivity;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.VehicleItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.presenter.BaseRecyclerAdapter;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.utils.ResourceUtil;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.CommonDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by jslsxu on 2017/9/19.
 */

public class VehicleListAdapter extends BaseRecyclerAdapter<VehicleItem> {
    private Context mContext;

    public VehicleListAdapter(Context context) {
        super(R.layout.vehicle_list_item);
        mContext = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, final VehicleItem item) {
        helper.setText(R.id.tv_vehicle_type, item.vehicle_type.name);
        helper.setText(R.id.tv_vehicle_brand_no, TextUtils.isEmpty(item.vehicle_brand) ? item.vehicle_no : item.vehicle_brand + "  " + item.vehicle_no);
        helper.setText(R.id.tv_auth_type, item.getAuthType());

        RoundTextView authStatusView = helper.getView(R.id.tv_auth_type);
        int colorId = R.color.auth_yellow;
        if(item.auth_type == VehicleItem.AUTH_TYPE_SUCCESS){
            colorId = R.color.togglebutton_green;
        }
        else if(item.auth_type == VehicleItem.AUTH_TYPE_FAILED){
            colorId = R.color.color_blue;
        }
        int color = ResourceUtil.getColor(mContext, colorId);
        authStatusView.setTextColor(color);
        RoundViewDelegate delegate = authStatusView.getDelegate();
        delegate.setStrokeColor(color);

        RelativeLayout rl_bottom = helper.getView(R.id.rl_bottom);
        rl_bottom.setVisibility(item.auth_type == VehicleItem.AUTH_TYPE_IN_AUTH ? View.GONE : View.VISIBLE);

        TextView tv_delete = helper.getView(R.id.tv_delete);
        TextView tv_edit = helper.getView(R.id.tv_edit);
        tv_edit.setVisibility(item.auth_type == VehicleItem.AUTH_TYPE_FAILED ? View.VISIBLE : View.GONE);

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_CANCEL, "取消", new CommonDialog.ActionCallback() {
                    @Override
                    public void onAction() {

                    }
                });
                CommonDialog.ActionItem settingItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                    @Override
                    public void onAction() {
                        requestVehicleDelete(item.vehicle_id);
                    }
                });
                ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                actionList.add(cancelItem);
                actionList.add(settingItem);
                CommonDialog.showDialog(mContext, "提示", "是否删除该车辆", actionList);


            }
        });
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                VehicleAuthActivity.intentTo(mContext, item);
            }
        });
    }


    private void requestVehicleDelete(final String vehicle_id) {
        final KProgressHUD mProgress = KProgressHUD.create(mContext)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        mProgress.setCancellable(false);
        mProgress.show();
        CommonApis.vehicleDelete(vehicle_id, new ResponseCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseData) {
                mProgress.dismiss();
                EventBus.getDefault().post(new EventBusEvent.VehicleListChangedEvent());
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                mProgress.dismiss();
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });
    }

}