package com.yun.yunmaster.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.robin.lazy.cache.CacheLoaderManager;
import com.yun.yunmaster.application.YunApplication;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.UserData;
import com.yun.yunmaster.network.base.apis.GsonManager;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.CityListResponse;
import com.yun.yunmaster.response.PublicParamsResponse;
import com.yun.yunmaster.response.UserInfoResponse;
import com.yun.yunmaster.model.CityInfo;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class AppSettingManager {
    public static final String GUIDE_KEY = "NewGuide";
    public static final String POLICY_KEY = "PublicParams";
    public static final String USER_DATA_KEY = "UserInfo";
    public static final String CITY_LIST_KEY = "CityList";
    public static final String CURRENT_CITY_KEY = "CurrentCity";

    public static void requestUserData(){
        CommonApis.getUserInfo(new ResponseCallback<UserInfoResponse>() {
            @Override
            public void onSuccess(UserInfoResponse baseData) {
                UserData userData = baseData.data.user_info;
                if(userData != null){
                    setUserData(userData);
                    EventBus.getDefault().post(new EventBusEvent.UserDataUpdateEvent());
                    return;
                }
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
            }
        });
    }
    public static void requestPublicParams() {
        CommonApis.getPublicParams(new ResponseCallback<PublicParamsResponse>() {
            @Override
            public void onSuccess(PublicParamsResponse baseData) {
                if (baseData.getData() != null) {
                    setPublicParams(baseData.getData());
                }
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {

            }
        });
    }

    public static void setPublicParams(PublicParamsResponse.DataBean publicParamsData) {
        if (publicParamsData != null) {
            String jsonString = GsonManager.getGson().toJson(publicParamsData);
            CacheLoaderManager.getInstance().saveString(POLICY_KEY, jsonString, Constants.CACHE_TIME);
        } else {
            CacheLoaderManager.getInstance().delete(POLICY_KEY);
        }
    }
    public static PublicParamsResponse.DataBean getPublicParams() {
        String jsonString = CacheLoaderManager.getInstance().loadString(POLICY_KEY);
        PublicParamsResponse.DataBean publicParamsData = null;
        try {
            publicParamsData = GsonManager.getGson().fromJson(jsonString, PublicParamsResponse.DataBean.class);
        } catch (Exception e) {

        }
        return publicParamsData;
    }

    public static void setUserData(UserData userData){
        if(userData != null){
            String jsonString = GsonManager.getGson().toJson(userData);
            CacheLoaderManager.getInstance().saveString(USER_DATA_KEY, jsonString, Constants.CACHE_TIME);
        }
        else {
            CacheLoaderManager.getInstance().delete(USER_DATA_KEY);
        }
    }

    public static UserData getUserData(){
        String jsonString = CacheLoaderManager.getInstance().loadString(USER_DATA_KEY);
        UserData userData = null;
        try {
            userData = GsonManager.getGson().fromJson(jsonString, UserData.class);
        } catch (Exception e) {

        }
        if(userData == null){
            userData = LoginManager.getLoginData().user_info;
        }
        return userData;
    }

    public static boolean needGuide(){
        int versionCode = AppInfoUtil.getVerCode(YunApplication.getApp());
        String guideVersion = CacheLoaderManager.getInstance().loadString(GUIDE_KEY);
        int guideVersionCode = 0;
        if(!TextUtils.isEmpty(guideVersion)){
            guideVersionCode = Integer.parseInt(guideVersion);
        }
        if(versionCode > guideVersionCode){
            return true;
        }
        return false;
    }

    public static void updateGuide(){
        //保存当前的版本号
        int versionCode = AppInfoUtil.getVerCode(YunApplication.getApp());
        CacheLoaderManager.getInstance().saveString(GUIDE_KEY, Integer.toString(versionCode), Constants.CACHE_TIME);
    }

    public static void sendSms(String phone){


    }

    /**
     * 城市
     * @return
     */

    public static void setCityListData(CityListResponse.CityListData cityListData){
        if (cityListData != null) {
            String jsonString = GsonManager.getGson().toJson(cityListData);
            CacheLoaderManager.getInstance().saveString(CITY_LIST_KEY, jsonString, Constants.CACHE_TIME);
        } else {
            CacheLoaderManager.getInstance().delete(CITY_LIST_KEY);
        }
    }

    public static CityListResponse.CityListData getCityListData(){
        String jsonString = CacheLoaderManager.getInstance().loadString(CITY_LIST_KEY);
        CityListResponse.CityListData cityListData = null;
        try {
            cityListData = GsonManager.getGson().fromJson(jsonString, CityListResponse.CityListData.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityListData;
    }

    public static void requestCityList(){
        CommonApis.cityList("", new ResponseCallback<CityListResponse>() {
            @Override
            public void onSuccess(CityListResponse baseData) {
                if(baseData != null){
                    setCityListData(baseData.data);
                }
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {

            }
        });
    }

    public static CityInfo getCurrentCity() {
        String jsonString = CacheLoaderManager.getInstance().loadString(CURRENT_CITY_KEY);
        CityInfo cityInfo = null;
        try {
            cityInfo = GsonManager.getGson().fromJson(jsonString, CityInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cityInfo;
    }

    public static void setCurrentCity(CityInfo cityInfo) {
        if (cityInfo != null) {
            CityInfo currentCity = getCurrentCity();
            if(currentCity == null || !cityInfo.city_id.equals(currentCity.city_id)){
                String jsonString = GsonManager.getGson().toJson(cityInfo);
                CacheLoaderManager.getInstance().saveString(CURRENT_CITY_KEY, jsonString, Constants.CACHE_TIME);
                EventBus.getDefault().post(new EventBusEvent.CityChangedEvent());
                CommonApis.selectCity();
            }
        }
    }
}
