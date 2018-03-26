package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.umeng.analytics.MobclickAgent;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.BaseFragment;
import com.yun.yunmaster.model.CityInfo;
import com.yun.yunmaster.response.CityListResponse;
import com.yun.yunmaster.utils.AppInfoUtil;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.LocationManager;
import com.yun.yunmaster.utils.LoginManager;
import com.yun.yunmaster.view.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private static String[] tags = {"HomeFragment", "OrderListFragment", "MineFragment"};
    @BindView(R.id.tab_control)
    RadioGroup mTab_control;
    private BaseFragment mCurrentFragment;
    private BaseFragment toFragment;
    private long exitTime = 0;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        final int tabCount = mTab_control.getChildCount();
        mTab_control.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                for (int i = 0; i < tabCount; i++) {
                    if (checkedId == group.getChildAt(i).getId()) {
                        selectIndex(i);
                    }
                }
            }
        });
        showFragmentAtIndex(0);
        AppSettingManager.requestUserData();
        AppInfoUtil.checkVersion(this);
        checkCity();
    }

    public void showFragmentAtIndex(int index) {
        int tabCount = mTab_control.getChildCount();
        if (index >= 0 && index < tabCount) {
            int resId = mTab_control.getChildAt(index).getId();
            mTab_control.check(resId);
            selectIndex(index);
        }
    }

    private void selectIndex(int index) {
        String tag = tags[index];
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        toFragment = (BaseFragment) getSupportFragmentManager()
                .findFragmentByTag(tag);

        if (toFragment == null) {
            try {
                toFragment = (BaseFragment) Class.forName("com.yun.yunmaster.fragment." + tag).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (toFragment != null) {
            if (mCurrentFragment == toFragment) {
                return;
            }
            if (!toFragment.isAdded()) {
                fragmentTransaction.add(R.id.fragment_container, toFragment, tag);
            } else {
                fragmentTransaction.show(toFragment);
            }
            if (mCurrentFragment != null) {
                fragmentTransaction.hide(mCurrentFragment);
            }
            fragmentTransaction.commitAllowingStateLoss();
            mCurrentFragment = toFragment;
        }
    }

    private void checkCity() {
        CityInfo currentCity = AppSettingManager.getCurrentCity();
        if (currentCity == null) {
            startLoading();
        }
        LocationManager.getLocation(new LocationManager.LocationListener() {
            @Override
            public void onLocationSuccess(BDLocation location) {
                endLoading();
                CityInfo currentCityInfo = AppSettingManager.getCurrentCity();
                final CityInfo cityInfo = new CityInfo();
                cityInfo.name = location.getCity();
                cityInfo.city_id = location.getCityCode();
                if(!TextUtils.isEmpty(cityInfo.name) && !TextUtils.isEmpty(cityInfo.city_id)){
                    CityListResponse.CityListData cityListData = AppSettingManager.getCityListData();
                    List<CityListResponse.CityGroup> cityList = cityListData.list;
                    boolean cityValidate = false;
                    for (CityListResponse.CityGroup cityGroup : cityList){
                        for (CityInfo city : cityGroup.cities){
                            if(cityInfo.city_id.equals(city.city_id)){
                                cityValidate = true;
                                cityInfo.name = city.name;
                            }
                        }
                    }
                    if(cityValidate){
                        if (currentCityInfo == null) {
                            AppSettingManager.setCurrentCity(cityInfo);
                        } else if (!cityInfo.city_id.equals(currentCityInfo.city_id)) {
                            //切换城市
                            CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_CANCEL, "取消", null);
                            CommonDialog.ActionItem confirmItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "切换", new CommonDialog.ActionCallback() {
                                @Override
                                public void onAction() {
                                    AppSettingManager.setCurrentCity(cityInfo);
                                }
                            });
                            ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                            actionList.add(cancelItem);
                            actionList.add(confirmItem);
                            CommonDialog.showDialog(MainActivity.this, "提示", "当前定位城市是" + cityInfo.name + ",是否切换?", actionList);
                        }
                    }
                    else {
                        locationFail();
                    }
                }
                else {
                    locationFail();
                }
            }

            @Override
            public void onLocationFail() {
                endLoading();
                locationFail();
            }
        });
    }

    private void locationFail(){
        CityInfo currentCityInfo = AppSettingManager.getCurrentCity();
        if (currentCityInfo == null) {
            CityInfo cityInfo = new CityInfo();
            cityInfo.name = "北京";
            cityInfo.city_id = "131";
            AppSettingManager.setCurrentCity(cityInfo);
        }
    }

    // 监听点击回退按钮
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                MobclickAgent.onKillProcess(MainActivity.this);
                finish();

                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LoginManager.setUserRegid();
    }

}

