package com.yun.yunmaster.model;

import com.yun.yunmaster.network.base.response.BaseObject;

/**
 * Created by Xionghu on 2017/10/24.
 */

public class VehicleItem extends BaseObject {

    public static final int AUTH_TYPE_IN_AUTH = 1;
    public static final int AUTH_TYPE_SUCCESS = 2;
    public static final int AUTH_TYPE_FAILED = 3;

    public String vehicle_id;
    public int auth_type;
    public VehicleTypeInfo vehicle_type;
    public String vehicle_brand;
    public String vehicle_no;
    public PhotoItem license_photo;
    public PhotoItem vehicle_photo;

    public String getAuthType() {
        if (auth_type == AUTH_TYPE_IN_AUTH) {
            return "认证中";
        } else if (auth_type == AUTH_TYPE_SUCCESS) {
            return "已认证";
        } else if (auth_type == AUTH_TYPE_FAILED) {
            return "认证失败";
        }
        return null;
    }

}
