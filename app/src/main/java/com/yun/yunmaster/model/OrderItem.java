package com.yun.yunmaster.model;

import com.yun.yunmaster.network.base.response.BaseObject;

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
    public String other_price;

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
                title = "待完成";
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
                title = "完成订单";
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
}
