package com.yun.yunmaster.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.yun.yunmaster.R;
import com.yun.yunmaster.view.CommonDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by xionghu on 2017/10/11.
 */
public class LocationUtils {

    static public final int REQUEST_CODE_ASK_PERMISSIONS = 101;
    public static int DIALOG_GTYPE_SETTING = 1;//去设置权限
    public static int DIALOG_GTYPE_NOMAL = 2;//只是告诉用户要设置
    private static WeakReference<LocationUtils> WeakReferenceInstance;
    private LocationListener locationListener;
    private Activity activity;
    private Object objLock = new Object();
    private int showDialogType = DIALOG_GTYPE_SETTING;
    private OnPermissionCallBackListener permissionCallBackListener;
    private LocationClientOption mOption, DIYoption;
    private LocationClient mLocationClient;
    private boolean toastLoad = false;
    private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>();
    private Handler locHander = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                BDLocation location = msg.getData().getParcelable("loc");
                if (location!=null&&(location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeNetWorkLocation || location.getLocType() == BDLocation.TypeOffLineLocation)) {
                    locationListener.success(location);
                    stopLocation();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };
    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(final BDLocation location) {


            if (null != location && (location.getLocType() == BDLocation.TypeOffLineLocation || location.getLocType() == BDLocation.TypeNetWorkLocation || location.getLocType() == BDLocation.TypeGpsLocation)) {
                Bundle locData = algorithm(location);
                Message locMsg = locHander.obtainMessage();
                if (locData != null) {
                    locData.putParcelable("loc", location);
                    locMsg.setData(locData);
                    locHander.sendMessage(locMsg);

                    if (!toastLoad) {
                        Toast.makeText(activity, "正在加载地图...", Toast.LENGTH_SHORT).show();
                    }
                    toastLoad = true;
                }
            } else {
                locationListener.error();
                Toast.makeText(activity, "定位失败，请检查手机网络或设置！", Toast.LENGTH_LONG).show();
            }

        }

    };


    public LocationUtils(Activity activity, LocationUtils.LocationListener locationListener) {
        this.activity = activity;
        mOption = new LocationClientOption();
        setPermition();
        startLocation();
        this.locationListener = locationListener;
    }

    //防止单例持有Activity 造成内存泄漏
    public static LocationUtils getLocation(Activity activity, LocationUtils.LocationListener locationListener) {
        if (WeakReferenceInstance == null || WeakReferenceInstance.get() == null) {
            WeakReferenceInstance = new WeakReference<LocationUtils>(new LocationUtils(activity, locationListener));
        }
        return WeakReferenceInstance.get();
    }



    /***
     * 平滑策略代码实现方法，主要通过对新定位和历史定位结果进行速度评分，
     * 来判断新定位结果的抖动幅度，如果超过经验值，则判定为过大抖动，进行平滑处理,若速度过快，
     * 则推测有可能是由于运动速度本身造成的，则不进行低速平滑处理
     *
     * @param location
     * @return Bundle
     */
    private Bundle algorithm(BDLocation location) {
        float[] EARTH_WEIGHT = {0.1f, 0.2f, 0.4f, 0.6f, 0.8f}; // 推算计算权重_地球
        Bundle locData = new Bundle();
        double curSpeed = 0;
        if (locationList.isEmpty() || locationList.size() < 2) {
            LocationEntity temp = new LocationEntity();
            temp.location = location;
            temp.time = System.currentTimeMillis();
            locData.putInt("iscalculate", 0);
            locationList.add(temp);
        } else {
            if (locationList.size() > 5)
                locationList.removeFirst();
            double score = 0;
            for (int i = 0; i < locationList.size(); ++i) {
                LatLng lastPoint = new LatLng(locationList.get(i).location.getLatitude(),
                        locationList.get(i).location.getLongitude());
                LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());
                double distance = DistanceUtil.getDistance(lastPoint, curPoint);
                curSpeed = distance / (System.currentTimeMillis() - locationList.get(i).time) / 1000;
                score += curSpeed * EARTH_WEIGHT[i];
            }
            if (score > 0.00000999 && score < 0.00005) {
                location.setLongitude(
                        (locationList.get(locationList.size() - 1).location.getLongitude() + location.getLongitude())
                                / 2);
                location.setLatitude(
                        (locationList.get(locationList.size() - 1).location.getLatitude() + location.getLatitude())
                                / 2);
                locData.putInt("iscalculate", 1);
            } else {
                locData.putInt("iscalculate", 0);
            }
            LocationEntity newLocation = new LocationEntity();
            newLocation.location = location;
            newLocation.time = System.currentTimeMillis();
            locationList.add(newLocation);

        }
        return locData;
    }

    public LocationUtils setShowDialogType(int type) {
        if (WeakReferenceInstance == null || WeakReferenceInstance.get() == null) {
            WeakReferenceInstance.get().showDialogType = type;
        }

        return WeakReferenceInstance.get();
    }

    public LocationUtils onLocationPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (WeakReferenceInstance == null || WeakReferenceInstance.get() == null) {
            onLocationRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        return WeakReferenceInstance.get();
    }

    /**
     * 外部获取这个LocationClientOption 根据自己需求更改需要的参数
     *
     * @return
     */
    public LocationClientOption getLocationClientOption() {
        return mOption;
    }

    /**
     * 初始化定位
     */
    public void initeLocation() {

        if (mLocationClient == null) {
            mLocationClient = new LocationClient(activity);
        }
        if (mLocationClient.isStarted()) {
            return;
        }
        registerListener(mListener);
        mLocationClient.setLocOption(getDefaultLocationClientOption());
        startLocation();
    }

    public void registerListener(BDAbstractLocationListener listener) {
        if (listener != null) {
            mLocationClient.registerLocationListener(listener);
        }
    }

    public void unRegisterListener(BDAbstractLocationListener listener) {
        if (listener != null) {
            mLocationClient.unRegisterLocationListener(listener);
        }
    }

    /***
     *
     * @return DefaultLocationClientOption
     */
    public LocationClientOption getDefaultLocationClientOption() {

        mOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        mOption.setScanSpan(5000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        mOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集

        mOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用

        return mOption;
    }

    /**
     * 开始定位
     */

    public void startLocation() {
        synchronized (objLock) {
            if (mLocationClient != null && !mLocationClient.isStarted()) {
                mLocationClient.start();
            }
        }
    }

    public void stopLocation() {
        synchronized (objLock) {
            if (mLocationClient != null && mLocationClient.isStarted()) {
                mLocationClient.stop();
            }
        }
    }

    public void onDestroy() {
        stopLocation();
        unRegisterListener(mListener);
        WeakReferenceInstance =null;
    }

    /**
     * 设置权限
     */
    public void setPermition() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                        || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    if (showDialogType == DIALOG_GTYPE_SETTING) {
                        if (permissionCallBackListener != null) {
                            permissionCallBackListener.OnPermissionCallBack(true);
                        }
                        settingDialog();
                    } else if (showDialogType == DIALOG_GTYPE_NOMAL) {
                        if (permissionCallBackListener != null) {
                            permissionCallBackListener.OnPermissionCallBack(false);
                        }
                        ToastUtil.showLongToast(activity.getResources().getString(R.string.permission_no_map));
                    }
                } else {
                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
                }
                return;
            } else {
                initeLocation();
            }
        } else {
            initeLocation();
        }
    }

    public void getLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    /**
     * 权限请求返回
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onLocationRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (permissionCallBackListener != null) {
                        permissionCallBackListener.OnPermissionCallBack(true);
                    }
                    initeLocation();
                } else {
                    if (showDialogType == DIALOG_GTYPE_SETTING) {
                        settingDialog();
                    } else if (showDialogType == DIALOG_GTYPE_NOMAL) {
                        if (permissionCallBackListener != null) {
                            permissionCallBackListener.OnPermissionCallBack(false);
                        }
                        ToastUtil.showLongToast(activity.getResources().getString(R.string.permission_no_map));
                    }
                }
                break;
        }
    }

    /**
     * 权限设置弹出框
     */
    public void settingDialog() {

        CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_CANCEL, "取消", new CommonDialog.ActionCallback() {
            @Override
            public void onAction() {

            }
        });
        CommonDialog.ActionItem settingItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "设置", new CommonDialog.ActionCallback() {
            @Override
            public void onAction() {
                getAppDetailSettingIntent(activity);
            }
        });
        ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
        actionList.add(cancelItem);
        actionList.add(settingItem);
        CommonDialog.showDialog(activity, "", "无法获取你的位置信息。请在手机系统设置--权限管理中打开位置权限。", actionList);

    }

    /**
     * 跳入设置权限的界面
     *
     * @param context
     */
    public void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(localIntent);
    }

    public void setOnPermissionCallBackListener(OnPermissionCallBackListener permissionCallBackListener) {
        this.permissionCallBackListener = permissionCallBackListener;
    }


    public interface LocationListener {
        void success(BDLocation location);

        void error();
    }

    public interface OnPermissionCallBackListener {
        void OnPermissionCallBack(boolean isPermissionOk);
    }

    class LocationEntity {
        BDLocation location;
        long time;
    }

}
