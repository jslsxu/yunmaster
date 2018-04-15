package com.yun.yunmaster.response;

import com.yun.yunmaster.model.DriverLicenseInfo;
import com.yun.yunmaster.model.UploadImageData;

public class UploadDriverLicenseResponse extends UploadImageResponse {
    public UploadDriverLicenseData data;
    public static class UploadDriverLicenseData extends UploadImageData{
        public DriverLicenseInfo driving_license_info;
    }
}
