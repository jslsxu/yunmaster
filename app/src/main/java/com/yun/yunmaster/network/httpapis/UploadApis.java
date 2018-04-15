package com.yun.yunmaster.network.httpapis;

import com.yun.yunmaster.network.base.apis.HttpApiBase;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.response.UploadCarLicenseResponse;
import com.yun.yunmaster.response.UploadDriverLicenseResponse;
import com.yun.yunmaster.response.UploadIDCardBackResponse;
import com.yun.yunmaster.response.UploadIDCardFrontResponse;
import com.yun.yunmaster.response.UploadImageResponse;

import java.util.HashMap;

/**
 * Created by jslsxu on 2018/4/10.
 */

public class UploadApis extends HttpApiBase {
    public static final int NORMAL_IMAGE = 0;
    public static final int ID_CARD_FRONT = 1;
    public static final int ID_CARD_BACK = 2;
    public static final int DRIVER_LICENSE = 3;
    public static final int CAR_LICENSE = 4;
    public static void uploadCarLicense(String imagePath, ResponseCallback<UploadCarLicenseResponse> callback){
        HashMap<String, String> params = new HashMap<>();
        params.put("file_type", Integer.toString(CAR_LICENSE));
        upload(UPLOAD_IMAGE,imagePath, params, callback);
    }

    public static void uploadNormalImage(String imagePath, ResponseCallback<UploadImageResponse> callback){
        upload(UPLOAD_IMAGE, imagePath, null, callback);
    }

    public static void uploadDriverLicense(String imagePath, ResponseCallback<UploadDriverLicenseResponse> callback){
        HashMap<String, String> params = new HashMap<>();
        params.put("file_type", Integer.toString(DRIVER_LICENSE));
        upload(UPLOAD_IMAGE,imagePath, params, callback);
    }

    public static void uploadIDCardFront(String imagePath, ResponseCallback<UploadIDCardFrontResponse> callback){
        HashMap<String, String> params = new HashMap<>();
        params.put("file_type", Integer.toString(ID_CARD_FRONT));
        upload(UPLOAD_IMAGE,imagePath, params, callback);
    }

    public static void uploadIDCardBack(String imagePath, ResponseCallback<UploadIDCardBackResponse> callback){
        HashMap<String, String> params = new HashMap<>();
        params.put("file_type", Integer.toString(ID_CARD_BACK));
        upload(UPLOAD_IMAGE,imagePath, params, callback);
    }
}
