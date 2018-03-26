package com.yun.yunmaster.model;

import com.yun.yunmaster.network.base.response.BaseObject;

/**
 * Created by jslsxu on 2017/9/19.
 */

public class OrderItem extends BaseObject {

    public static final int ORDER_STATUS_WAIT_PAY_DEPOSIT = 1;      //待支付押金
    public static final int ORDER_STATUS_WAITING_ACCEPT = 2;        //待接单
    public static final int ORDER_STATUS_ACCEPTED = 3;              //已接单,待上门
    public static final int ORDER_STATUS_ARRIVED_WAIT_CONFIRM = 4;      //已到达，待确认费用
    public static final int ORDER_STATUS_FEE_CONFIRMED = 5;         //费用已确认，待支付
    public static final int ORDER_STATUS_PAYED_WAIT_COMPLETE = 6;   //已支付，待完成
    public static final int ORDER_STATUS_COMPLETED_WAIT_CONFIRM = 7;//完成待确认
    public static final int ORDER_STATUS_COMPLETED_CONFIRMED = 8;   //确认，已完成
    public static final int ORDER_STATUS_RATED = 9;                 //已评价

    public String oid;
    public String time;
    public String vehicle;
    public String address;
    public String total_price;
    public String comment;
    public boolean is_cancel;
    public boolean can_cancel;
    public String cancel_msg;
    public boolean can_change_price;
    public int step;
    public int transport_times;
    public String other_price;

    public String statusTitle() {
        if(is_cancel){
            return "已取消";
        }
        String title = "缺省";
        switch (step) {
            case ORDER_STATUS_WAITING_ACCEPT:
                title = "确定接单";
                break;
            case ORDER_STATUS_ACCEPTED:
                title = "待到达";
                break;
            case ORDER_STATUS_ARRIVED_WAIT_CONFIRM:
                title = "待确认费用";
                break;
            case ORDER_STATUS_FEE_CONFIRMED:
                title = "待支付";
                break;
            case ORDER_STATUS_PAYED_WAIT_COMPLETE:
                title = "已支付";
                break;
            case ORDER_STATUS_COMPLETED_WAIT_CONFIRM:
                title = "待确认";
                break;
            case ORDER_STATUS_COMPLETED_CONFIRMED:
                title = "已完成";
                break;
            case ORDER_STATUS_RATED:
                title = "已评价";
                break;
        }
        return title;
    }

    public String actionTitle() {
        if (is_cancel) {
            return "已取消";
        }
        String title = "缺省";
        switch (step) {
            case ORDER_STATUS_WAITING_ACCEPT:
                title = "确定接单";
                break;
            case ORDER_STATUS_ACCEPTED:
                title = "确认到达";
                break;
            case ORDER_STATUS_ARRIVED_WAIT_CONFIRM:
                title = "调整费用";
                break;
            case ORDER_STATUS_FEE_CONFIRMED:
                title = "待支付";
                break;
            case ORDER_STATUS_PAYED_WAIT_COMPLETE:
                title = "服务完成";
                break;
            case ORDER_STATUS_COMPLETED_WAIT_CONFIRM:
                title = "待确认";
                break;
            case ORDER_STATUS_COMPLETED_CONFIRMED:
                title = "已完成";
                break;
            case ORDER_STATUS_RATED:
                title = "已评价";
                break;
        }
        return title;
    }

    public boolean canOperation() {
        if (is_cancel) {
            return false;
        }
        if (step == ORDER_STATUS_WAITING_ACCEPT
                || step == ORDER_STATUS_ACCEPTED
                || step == ORDER_STATUS_ARRIVED_WAIT_CONFIRM
                || step == ORDER_STATUS_PAYED_WAIT_COMPLETE) {
            return true;
        }
        return false;
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
