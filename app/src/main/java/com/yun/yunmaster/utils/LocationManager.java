package com.yun.yunmaster.utils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yun.yunmaster.base.BaseActivity;

/**
 * Created by jslsxu on 2017/10/27.
 */

public class LocationManager {
    public static void getLocation(final LocationListener listener){
        BaseActivity topActivity = (BaseActivity) ActivityManager.getInstance().currentActivity();
        final LocationClient locationClient = new LocationClient(topActivity);
        BDLocationListener locationListener = new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                locationClient.stop();
                if(listener != null){
                    if(bdLocation == null){
                        listener.onLocationFail();
                    }
                    else {
                        listener.onLocationSuccess(bdLocation);
                    }
                }
            }
        };
        locationClient.registerLocationListener(locationListener);

        ///LocationClientOption类用来设置定位SDK的定位方式，
        LocationClientOption locationOption = new LocationClientOption(); //以下是给定位设置参数
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationOption.setOpenGps(true); // 打开gps
        locationOption.setIsNeedAddress(true);
        locationOption.setCoorType("bd09ll"); // 设置坐标类型
        locationOption.setScanSpan(1000);
        locationOption.setIsNeedLocationPoiList(true);
        locationClient.setLocOption(locationOption);

        locationClient.start();
//        int status = ContextCompat.checkSelfPermission(topActivity, Manifest.permission.ACCESS_COARSE_LOCATION);
//        if (status != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(topActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.LOCATION_PERMISSION_REQUEST_CODE);
//        } else {
//            locationClient.start();
//        }
    }

    public interface LocationListener{
        void onLocationSuccess(BDLocation location);
        void onLocationFail();
    }

}
