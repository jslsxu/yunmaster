package com.yun.yunmaster.model;

import com.yun.yunmaster.network.base.response.BaseObject;

/**
 * Created by jslsxu on 2017/9/27.
 */

public class UserData extends BaseObject {
    public static final int ACCEPT_ORDER = 0;
    public static final int NOT_ACCEPT_ORDER = 1;
    public String uid;
    public String name;
    public String mobile;
    public int rate;
    public String encashment;           //提现数
    public String balance;              //余额
    public int order_push;
    public CarInfo car_info;
    public int auth_type; //-1 认证未通过  1 未认证 2 已认证 3 已冻结 4认证中

    public void setOrderPush(int order_push) {
        this.order_push = order_push;
    }

    public boolean isNeedAuth() {
        if (-1==auth_type || 1==auth_type) {
            return true;
        }
        return false;
    }

    public String getAuthType() {
        if (-1 == auth_type) {
            return "认证未通过";
        } else if (1 == auth_type) {
            return "未认证";
        } else if (2 == auth_type) {
            return "已认证";
        } else if (3 == auth_type) {
            return "已冻结";
        } else if (4 == auth_type) {
            return "认证中";
        }
        return null;
    }
}
