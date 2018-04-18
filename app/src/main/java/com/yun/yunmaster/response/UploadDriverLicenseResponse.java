package com.yun.yunmaster.response;

import com.yun.yunmaster.model.DriverLicenseInfo;
import com.yun.yunmaster.model.UploadImageData;
import com.yun.yunmaster.network.base.response.BaseResponse;

public class UploadDriverLicenseResponse extends BaseResponse {
    public UploadDriverLicenseData data;
    public static class UploadDriverLicenseData extends UploadImageData{
        public DriverLicenseInfo driving_license_info;
    }
}
