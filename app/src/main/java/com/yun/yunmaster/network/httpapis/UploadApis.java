package com.yun.yunmaster.network.httpapis;

import com.yun.yunmaster.network.base.apis.HttpApiBase;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.response.UploadCarLicenseResponse;
import com.yun.yunmaster.response.UploadImageResponse;

import java.util.HashMap;

/**
 * Created by jslsxu on 2018/4/10.
 */

public class UploadApis extends HttpApiBase {
    public static void uploadCarLicense(String imagePath, ResponseCallback<UploadCarLicenseResponse> callback){
        HashMap<String, String> params = new HashMap<>();
        params.put("file_type", "4");
        upload(UPLOAD_IMAGE,imagePath, params, callback);
    }

    public static void uploadNormalImage(String imagePath, ResponseCallback<UploadImageResponse> callback){
        upload(UPLOAD_IMAGE, imagePath, null, callback);
    }
}
