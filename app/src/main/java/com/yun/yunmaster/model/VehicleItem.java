package com.yun.yunmaster.model;

import com.yun.yunmaster.network.base.response.BaseObject;
import com.yun.yunmaster.response.UploadCarLicenseResponse;

/**
 * Created by Xionghu on 2017/10/24.
 */

public class VehicleItem extends BaseObject {

    public static final int AUTH_TYPE_IN_AUTH = 0;
    public static final int AUTH_TYPE_SUCCESS = 1;
    public static final int AUTH_TYPE_FAILED = 2;

    public String vehicle_id;
    public int auth_type;
    public VehicleTypeInfo vehicle_type;
    public String vehicle_brand;
    public String vehicle_no;
    public PhotoItem license_photo;
    public VehiclePhoto vehicle_photo;
    public UploadCarLicenseResponse.CarLicenseInfo car_license_info;

    public String getAuthType() {
        if (auth_type == AUTH_TYPE_IN_AUTH) {
            return "认证中";
        } else if (auth_type == AUTH_TYPE_SUCCESS) {
            return "已认证";
        } else if (auth_type == AUTH_TYPE_FAILED) {
            return "认证失败";
        }
        return "认证中";
    }

    public boolean canEdit(){
        return auth_type != AUTH_TYPE_IN_AUTH;
    }

    public static class VehiclePhoto extends BaseObject{
        public String front_photo;
        public String side_photo;
    }
}
