package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yun.yunmaster.R;
import com.yun.yunmaster.application.YunApplication;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.DriverInfo;
import com.yun.yunmaster.model.ImageUploadItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.DriverInfoResponse;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.GridViewForScrollView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class DriverInfoActivity extends BaseActivity {

    protected ArrayList<ImageUploadItem> photoList = new ArrayList<>();
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.photoGridView)
    GridViewForScrollView photoGridView;
    @BindView(R.id.iv_driving_license)
    ImageView ivDrivingLicense;
    @BindView(R.id.tv_truename)
    TextView tvTruename;
    @BindView(R.id.tv_id_no)
    TextView tvIdNo;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.scrollview)
    ScrollView scrollview;
    @BindView(R.id.cardPhotoView)
    LinearLayout cardPhotoView;
    @BindView(R.id.licensePhotoView)
    LinearLayout licensePhotoView;
    private BaseAdapter photoAdapter;
    private boolean setRefreshLayoutEnabled = true;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, DriverInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_info);
        ButterKnife.bind(this);

        init();
    }

    public void init() {
        scrollview.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                refreshLayout.setEnabled(scrollview.getScrollY() == 0 && setRefreshLayoutEnabled);
            }
        });

        navigationBar.setTitle("车主认证信息");
        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        requestDriverInfo();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestDriverInfo();
            }
        });

    }


    private void requestDriverInfo() {

        startLoading();
        CommonApis.getDriverInfo(new ResponseCallback<DriverInfoResponse>() {

            @Override
            public void onSuccess(DriverInfoResponse baseData) {
                endLoading();
                if (baseData.data.user == null) return;
                scrollview.setVisibility(View.VISIBLE);
                setUserData(baseData.data.user);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });

    }

    private void setUserData(final DriverInfo user) {
        if (user == null) return;
        refreshLayout.setEnabled(false);
        refreshLayout.setRefreshing(false);
        setRefreshLayoutEnabled = false;
        tvTruename.setText(user.real_name);
        tvIdNo.setText(user.card_no);

        if (user.card_photo == null || user.card_photo.size() == 0) {
            cardPhotoView.setVisibility(View.GONE);
        }
        else {
            cardPhotoView.setVisibility(View.VISIBLE);
            photoAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    if (user.card_photo != null) {
                        return user.card_photo.size();
                    }
                    return 0;
                }

                @Override
                public Object getItem(int position) {
                    return user.card_photo.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    View cell = convertView;
                    Context context = DriverInfoActivity.this;
                    if (cell == null) {
                        cell = LayoutInflater.from(context).inflate(R.layout.driver_cert_image_cell, parent, false);
                    }
                    ImageButton deleteButton = (ImageButton) cell.findViewById(R.id.removeButton);
                    deleteButton.setVisibility(View.GONE);
                    ImageView imageView = (ImageView) cell.findViewById(R.id.imageView);
                    Glide.with(YunApplication.getApp()).load(user.card_photo.get(position).full_path).into(imageView);
                    return cell;
                }
            };
            photoGridView.setAdapter(photoAdapter);
        }


        if (user.driver_license_photo != null && !TextUtils.isEmpty(user.driver_license_photo.full_path)) {
            licensePhotoView.setVisibility(View.VISIBLE);
            Glide.with(YunApplication.getApp()).load(user.driver_license_photo.full_path).into(ivDrivingLicense);
        }
        else {
            licensePhotoView.setVisibility(View.GONE);
        }

    }

}

