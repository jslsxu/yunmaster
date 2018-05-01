package com.yun.yunmaster.network.base.apis;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.yun.yunmaster.BuildConfig;
import com.yun.yunmaster.network.base.callback.DownloadCallback;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.utils.LoginManager;
import com.yun.yunmaster.utils.ParametersSorting;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import timber.log.Timber;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class HttpApiBase implements CommonInterface {
    public static final int GET_METHOD = 1;
    public static final int POST_METHOD = 2;
    protected static final int LIMIT = 20;
    private final static String[] IPS = {BuildConfig.API_HOST, BuildConfig.DEBUG_API_HOST};
//    private final static String[] H5IPS = {BuildConfig.H5_HOST, BuildConfig.DEBUG_H5_HOST};
    /**
     * 0为正式线上环境，1为开发环境
     */
    static int HOST = BuildConfig.API_ENV;

    public static String getSecureBaseUrl() {
        int urlIndex = HOST;
        if (BuildConfig.DEBUG) {
            urlIndex = AppUrlUtil.appUtlTypeRelease() ? 0 : 1;
        }
        return IPS[urlIndex];
    }

//    public static String getH5SecureBaseUrl() {
//        int urlIndex = HOST;
//        if(BuildConfig.DEBUG){
//            urlIndex = AppUrlUtil.appUtlTypeRelease() ? 0 : 1;
//        }
//        return H5IPS[urlIndex];
//    }

    public static void get(String url, Map<String, String> params, final ResponseCallback responseCallback) {
        execute(GET_METHOD, url, params, responseCallback);
    }

    public static void post(String url, Map<String, String> params, final ResponseCallback responseCallback) {
        execute(POST_METHOD, url, params, responseCallback);
    }

    public static void execute(int method, String url, Map<String, String> params, final ResponseCallback responseCallback) {
        String requestUrl = getSecureBaseUrl() + url;
        HashMap validateMap = addCommonParams(params);
        RequestCall requestCall;
        if (method == GET_METHOD) {
            requestCall = OkHttpUtils.get().url(requestUrl).params(validateMap).build();
        } else {
            requestCall = OkHttpUtils.post().url(requestUrl).params(validateMap).build();
        }
        requestCall.execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (responseCallback != null) {
                    responseCallback.onFail(BaseResponse.NETWORK_ERROR, new BaseResponse(), null);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                BaseResponse httpResponse = new BaseResponse();
                if (!TextUtils.isEmpty(response) && responseCallback != null) {
                    try {
                        httpResponse = GsonManager.getGson().fromJson(response, responseCallback.getClazz());
                    } catch (JsonIOException | JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
                if (httpResponse.success()) {
                    if (responseCallback != null) {
                        responseCallback.onSuccess(httpResponse);
                    }
                } else {
                    if (httpResponse.getErrno() == 7) {
                        //登陆过期
//                        LoginManager.logout(true);
                    }
                    if (responseCallback != null) {
                        responseCallback.onFail(httpResponse.getErrno(), httpResponse, null);
                    }
                }
            }
        });
    }

    public static HashMap addCommonParams(Map<String, String> params) {
        HashMap<String, String> paramsMap = getStringStringHashMap();
        if (params != null) {
            paramsMap.putAll(params);
        }

        return paramsMap;
    }

    public static String getCommonParamsUrl() {

        HashMap<String, String> paramsMap = getStringStringHashMap();

        return ParametersSorting.createLinkString(paramsMap);

    }

    @NonNull
    public static HashMap<String, String> getStringStringHashMap() {
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("platform", "android");

        String token = LoginManager.getToken();
        if (!TextUtils.isEmpty(token)) {
            paramsMap.put("token", token);
        }

        String uid = LoginManager.getUid();
        if(!TextUtils.isEmpty(uid)){
            paramsMap.put("uid", uid);
        }

//        CityInfo cityInfo = AppSettingManager.getCurrentCity();
//        if(cityInfo != null){
//            paramsMap.put("city", cityInfo.city_id);
//        }

        paramsMap.put("version", BuildConfig.VERSION_NAME);
        paramsMap.put("channel", BuildConfig.FLAVOR);
        return paramsMap;
    }

    public static void download(String url, String destinationDir, String fileName, final DownloadCallback callback) {
        OkHttpUtils.get().url(url).build().execute(new FileCallBack(destinationDir, fileName) {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (callback != null) {
                    callback.onFail(e);
                }
            }

            @Override
            public void onResponse(File response, int id) {
                if (callback != null) {
                    callback.onSuccess(response);
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                if (callback != null) {
                    callback.onProgress(progress, total);
                }
            }
        });
    }

    public static void upload(String url, String filePath, Map<String, String> params, final ResponseCallback responseCallback){
        String requestUrl = getSecureBaseUrl() + url;
        HashMap validateMap = addCommonParams(params);
        File file = new File(filePath);
        Timber.e(filePath);
        if(file == null){
            Timber.e("文件为空");
            return;
        }
        Timber.e("file length is " + file.length());
        RequestCall requestCall = OkHttpUtils.post().url(requestUrl).params(validateMap).addFile("image", "image.jpg", file).build();
        requestCall.writeTimeOut(60 * 1000);
        requestCall.readTimeOut(60 * 1000);
        requestCall.connTimeOut(60 * 1000);
        requestCall.execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (responseCallback != null) {
                    responseCallback.onFail(BaseResponse.NETWORK_ERROR, new BaseResponse(), null);
                }
            }

            @Override
            public void onResponse(String response, int id) {
                Timber.e(response);
                BaseResponse httpResponse = new BaseResponse();
                if (!TextUtils.isEmpty(response) && responseCallback != null) {
                    try {
                        httpResponse = GsonManager.getGson().fromJson(response, responseCallback.getClazz());
                    } catch (JsonIOException | JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }
                if (httpResponse.success()) {
                    if (responseCallback != null) {
                        responseCallback.onSuccess(httpResponse);
                    }
                } else {
                    if (responseCallback != null) {
                        responseCallback.onFail(httpResponse.getErrno(), httpResponse, null);
                    }
                }
            }
        });


    }
}


