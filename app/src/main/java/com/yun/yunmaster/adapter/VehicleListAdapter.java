package com.yun.yunmaster.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
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

import org.greenrobot.eventbus.EventBus;

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
        TextView authTypeTextView = helper.getView(R.id.tv_auth_type);
        authTypeTextView.setText(item.getAuthType());
        int color;
        if(item.auth_type == VehicleItem.AUTH_TYPE_IN_AUTH){
            color = ResourceUtil.getColor(mContext, R.color.color_blue);
        }
        else if(item.auth_type == VehicleItem.AUTH_TYPE_SUCCESS){
            color = ResourceUtil.getColor(mContext, R.color.togglebutton_green);
        }
        else {
            color = ResourceUtil.getColor(mContext, R.color.color6);
        }
        authTypeTextView.setTextColor(color);
        helper.setText(R.id.tv_auth_type, item.getAuthType());
        helper.setText(R.id.tv_vehicle_type, item.vehicle_type.name);
        helper.setText(R.id.vehicleNOTextView, item.vehicle_no);
        helper.setText(R.id.vehicleBrandTextView, item.vehicle_brand);

        LinearLayout editView = helper.getView(R.id.editView);
        editView.setVisibility(item.canEdit() ? View.VISIBLE : View.GONE);

        ImageView editButton = helper.getView(R.id.editButton);
        ImageView deleteButton = helper.getView(R.id.deleteButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VehicleAuthActivity.intentTo(mContext, item);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestVehicleDelete(item.vehicle_id);
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