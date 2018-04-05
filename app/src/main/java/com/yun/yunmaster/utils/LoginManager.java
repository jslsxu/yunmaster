package com.yun.yunmaster.utils;

import android.content.Context;
import android.text.TextUtils;

import com.robin.lazy.cache.CacheLoaderManager;
import com.yun.yunmaster.activity.LoginActivity;
import com.yun.yunmaster.application.YunApplication;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.network.base.apis.GsonManager;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.LoginResponse;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class LoginManager {
    public static final String LOGIN_INFO_KEY = "LOGIN_INFO";

    public static boolean isLogin() {
        LoginResponse.LoginData loginData = getLoginData();
        if (loginData != null && !TextUtils.isEmpty(loginData.token)) {
            return true;
        }
        return false;
    }

    public static String getToken() {
        LoginResponse.LoginData loginData = getLoginData();
        if (loginData != null) {
            return loginData.token;
        }
        return null;
    }

    public static String getUid() {
        LoginResponse.LoginData loginData = getLoginData();
        if (loginData != null) {
            return loginData.user_info.uid;
        }
        return null;
    }

    public static LoginResponse.LoginData getLoginData() {
        LoginResponse.LoginData mLoginData = null;
        String loginInfo = CacheLoaderManager.getInstance().loadString(LOGIN_INFO_KEY);
        if (!TextUtils.isEmpty(loginInfo)) {
            try {
                mLoginData = GsonManager.getGson().fromJson(loginInfo, LoginResponse.LoginData.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mLoginData;
    }

    public static void setLoginData(LoginResponse.LoginData userInfo) {
        String jsonString = GsonManager.getGson().toJson(userInfo);
        CacheLoaderManager.getInstance().saveString(LOGIN_INFO_KEY, jsonString, Constants.CACHE_TIME);
        CommonApis.selectCity();
        AppSettingManager.setUserData(userInfo.user_info);
    }

    public static void logout(boolean tokenInvalidate) {//是否因为token失效
        CacheLoaderManager.getInstance().delete(LOGIN_INFO_KEY);
        AppSettingManager.setUserData(null);
        EventBus.getDefault().post(new EventBusEvent.LogoutEvent());
        Context context = ActivityManager.getInstance().currentActivity();
        LoginActivity.intentTo(context);
        ActivityManager.getInstance().popAllActivityExceptOne(LoginActivity.class);
        if (tokenInvalidate) {
            ToastUtil.showToast("您的登录时效已过期，请重新登录!");
        }
    }

    public static void setUserRegid() {
        if (TextUtils.isEmpty(YunApplication.mipushregid)) {
            return;
        }
        CommonApis.saveRegid(YunApplication.mipushregid);
    }
}
