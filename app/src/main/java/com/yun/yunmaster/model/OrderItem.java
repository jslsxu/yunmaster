package com.yun.yunmaster.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.yun.yunmaster.activity.OrderDetailActivity;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseObject;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.OrderApis;
import com.yun.yunmaster.response.PickOrderResponse;
import com.yun.yunmaster.utils.CommonCallback;
import com.yun.yunmaster.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jslsxu on 2017/9/19.
 */

public class OrderItem extends BaseObject {

    public static final int ORDER_STATUS_WAITING_ACCEPT = 2;        //已确认，待接单
    public static final int ORDER_STATUS_ACCEPTED = 3;              //已接单,待出发
    public static final int ORDER_STATUS_SET_OUT = 7;               //已发车,待到达
    public static final int ORDER_STATUS_ARRIVED = 4;               //已到达
    public static final int ORDER_STATUS_FEE_CONFIRMED = 5;         //费用已确认，待支付
    public static final int ORDER_STATUS_PAYED = 6;   //已支付，待完成

    public static final int GET_ORDER_NORMAL = 0;       //可抢单
    public static final int GET_ORDER_GET = 1;          //已抢到，未完成
    public static final int GET_ORDER_FAILED = 2;       //未抢到
    public int is_get_order;            //订单是否接单
    public boolean isNew;
    public String oid;
    public String time;
    public String date;
    public String vehicle;
    public String address;
    public String total_price;
    public String comment;
    //    public boolean is_cancel;
//    public boolean can_cancel;
//    public String cancel_msg;
    public boolean can_change_price;
    public int step;
    public int transport_times;
    public String other_fee;

    public OrderItem.CustomerInfo customer;
    public AddressInfo detail_address;
    public List<InfoItem> fee_items;
    public List<String> photo;
    public List<String> complete_photo;

    public String statusTitle() {
        String title = "缺省";
        switch (step) {
            case ORDER_STATUS_WAITING_ACCEPT:
                title = "抢单";
                break;
            case ORDER_STATUS_ACCEPTED:
                title = "待出发";
                break;
            case ORDER_STATUS_SET_OUT:
                title = "已发车";
                break;
            case ORDER_STATUS_ARRIVED:
                title = "已到达";
                break;
            case ORDER_STATUS_FEE_CONFIRMED:
                title = "待支付";
                break;
            case ORDER_STATUS_PAYED:
                title = "已支付";
                break;
        }
        return title;
    }

    public String actionTitle() {
        String title = "缺省";
        switch (step) {
            case ORDER_STATUS_WAITING_ACCEPT:
                title = "抢单";
                break;
            case ORDER_STATUS_ACCEPTED:
                title = "发车";
                break;
            case ORDER_STATUS_SET_OUT:
                title = "确定到达";
                break;
            case ORDER_STATUS_ARRIVED:
                title = arrivedAction();
                break;
            case ORDER_STATUS_FEE_CONFIRMED:
                title = "待支付";
                break;
            case ORDER_STATUS_PAYED:
                title = "已支付";
                break;
        }
        return title;
    }

    public boolean canOperation() {
        if (step == ORDER_STATUS_FEE_CONFIRMED
                || step == ORDER_STATUS_PAYED
                ) {
            return false;
        }
        return true;
    }

    public static class WasteYard extends BaseObject {
        public String yardId;
        public String address;
        public String name;
        public double lat;
        public double lng;
    }

    public static class CustomerInfo extends BaseObject {
        public String uid;
        public String name;
        public String avatar;
        public String mobile;
    }

    public int nextStep() {
        if (step == ORDER_STATUS_WAITING_ACCEPT) {
            return ORDER_STATUS_ACCEPTED;
        } else if (step == ORDER_STATUS_ACCEPTED) {
            return ORDER_STATUS_SET_OUT;
        } else if (step == ORDER_STATUS_SET_OUT) {
            return ORDER_STATUS_ARRIVED;
        }
        return 0;
    }

    public boolean isAm() {
        if (time.compareTo("12:00") < 0) {
            return true;
        }
        return false;
    }


    public void takeOrder(final Context context, final CommonCallback callback) {
        final KProgressHUD mProgress = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        mProgress.setCancellable(false);
        mProgress.setLabel("      PK中      ");
        mProgress.show();
        OrderApis.takeOrder(oid, new ResponseCallback<PickOrderResponse>() {
            @Override
            public void onSuccess(PickOrderResponse baseData) {
                mProgress.dismiss();
                is_get_order = baseData.data.is_get_order;
                if(is_get_order == GET_ORDER_GET){
                    EventBus.getDefault().post(new EventBusEvent.OrderStatusChangedEvent());
                }
                if (callback != null) {
                    callback.onFinish(true);
                }
                ToastUtil.showToast(baseData.getErrmsg());

            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                mProgress.dismiss();
                ToastUtil.showToast(failDate.getErrmsg());
                if (callback != null) {
                    callback.onFinish(false);
                }
            }
        });
    }

    public List<InfoItem> orderInfoList() {
        ArrayList<InfoItem> infoList = new ArrayList<>();
        InfoItem oidItem = new InfoItem();
        oidItem.key = "订单号";
        oidItem.value = oid;
        infoList.add(oidItem);

        InfoItem item = new InfoItem();
        item.key = "车型";
        item.value = vehicle;
        infoList.add(item);

        InfoItem item2 = new InfoItem();
        item2.key = "车次";
        item2.value = transport_times + "";
        infoList.add(item2);

        InfoItem item3 = new InfoItem();
        item3.key = "用车时间";
        item3.value = time;
        infoList.add(item3);

        if (detail_address != null) {
            if (!TextUtils.isEmpty(detail_address.address)) {
                InfoItem item4 = new InfoItem();
                item4.key = "地址";
                item4.value = detail_address.address;
                item4.isAddress = true;
                infoList.add(item4);
            }

            if (!TextUtils.isEmpty(detail_address.name)) {
                InfoItem nameItem = new InfoItem();
                nameItem.key = "联系人";
                nameItem.value = detail_address.name;
                infoList.add(nameItem);
            }

            if (!TextUtils.isEmpty(detail_address.mobile)) {
                InfoItem mobileItem = new InfoItem();
                mobileItem.key = "联系电话";
                mobileItem.value = detail_address.mobile;
                mobileItem.isMobile = true;
                infoList.add(mobileItem);
            }
        }

        if (!TextUtils.isEmpty(comment)) {
            InfoItem item6 = new InfoItem();
            item6.key = "订单备注";
            item6.value = comment;
            infoList.add(item6);
        }

        return infoList;
    }

    public String arrivedAction() {
        if (step == ORDER_STATUS_ARRIVED) {
            if (needUploadFinishPhoto()) {
                return "清运完成";
            } else if (can_change_price) {
                return "调整费用";
            }
        }
        return null;
    }

    public boolean needUploadFinishPhoto() {
        if (this.complete_photo == null || this.complete_photo.size() == 0) {
            return true;
        }
        return false;
    }

    public boolean needTimer() {
        return this.step == ORDER_STATUS_FEE_CONFIRMED;
    }

    public boolean needUpdateLocation() {
        return step == ORDER_STATUS_SET_OUT;
    }

    public boolean needMap() {
        if (step == ORDER_STATUS_WAITING_ACCEPT || step == ORDER_STATUS_ACCEPTED ||
                step == ORDER_STATUS_ARRIVED || step == ORDER_STATUS_SET_OUT) {
            return true;
        }
        return false;
    }
}
