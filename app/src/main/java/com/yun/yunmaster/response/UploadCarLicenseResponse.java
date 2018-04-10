package com.yun.yunmaster.response;

import com.yun.yunmaster.network.base.response.BaseResponse;

/**
 * Created by jslsxu on 2018/4/10.
 */

public class UploadCarLicenseResponse extends BaseResponse {

    public CarLicenseData data;
    public static class CarLicenseData{
        public String img_url;
        public CarLicenseInfo car_license_info;
    }

    public static class CarLicenseInfo{
        public String ls_prefix;
        public String ls_num;
        public String ls_type;
        public String ls_type_name;
        public String real_name;
        public String address;
        public String car_type;
        public String frame_no;
        public String engine_no;
        public String reg_date;
        public String issue_date;
        public String use_type;
    }
}
