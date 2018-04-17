package com.yun.yunmaster.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.roundview.RoundTextView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.ImageUploadItem;
import com.yun.yunmaster.model.VehicleItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.network.httpapis.UploadApis;
import com.yun.yunmaster.response.UploadCarLicenseResponse;
import com.yun.yunmaster.response.UploadImageResponse;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.CameraUtils;
import com.yun.yunmaster.utils.Constants;
import com.yun.yunmaster.utils.ImageUtil;
import com.yun.yunmaster.utils.PhotoManager;
import com.yun.yunmaster.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class VehicleAuthActivity extends BaseActivity {

    public static final String VEHICLE_KEY = "vehicle_key";
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_submit)
    RoundTextView tvSubmit;
    @BindView(R.id.noTextView)
    TextView noTextView;
    @BindView(R.id.typeTextView)
    TextView typeTextView;
    @BindView(R.id.ownerTextView)
    TextView ownerTextView;
    @BindView(R.id.addressTextView)
    TextView addressTextView;
    @BindView(R.id.brandTextView)
    TextView brandTextView;
    @BindView(R.id.validateNoTextView)
    TextView validateNoTextView;
    @BindView(R.id.engineNoTextView)
    TextView engineNoTextView;
    @BindView(R.id.registerDateTextView)
    TextView registerDateTextView;
    @BindView(R.id.cerDateTextView)
    TextView cerDateTextView;
    @BindView(R.id.vehicleFrontImageView)
    ImageView vehicleFrontImageView;
    @BindView(R.id.vehicleBackImageView)
    ImageView vehicleBackImageView;
    @BindView(R.id.licenseImageView)
    ImageView licenseImageView;

    private VehicleItem vehicleItem;
    private ImageUploadItem frontPhotoItem = new ImageUploadItem(null);
    private ImageUploadItem sidePhotoItem = new ImageUploadItem(null);
    private ImageUploadItem licensePhotoItem = new ImageUploadItem(null);
    private ImageUploadItem currentPhotoItem;
    private ImageView currentImageView;
    private UploadCarLicenseResponse.CarLicenseInfo carLicenseInfo;

    public static void intentTo(Context context, VehicleItem vehicleItem) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(VEHICLE_KEY, vehicleItem);
        Intent intent = new Intent(context, VehicleAuthActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        AppSettingManager.requestPublicParams();
        navigationBar.setTitle("车辆认证");
        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        vehicleItem = (VehicleItem)intent.getSerializableExtra(VEHICLE_KEY);
        if(vehicleItem != null){
            try {
                frontPhotoItem.full_path = vehicleItem.vehicle_photo.front_photo;
                sidePhotoItem.full_path = vehicleItem.vehicle_photo.side_photo;
                licensePhotoItem.full_path = vehicleItem.license_photo.full_path;
                Glide.with(this).load(frontPhotoItem.full_path).into(vehicleFrontImageView);
                Glide.with(this).load(sidePhotoItem.full_path).into(vehicleBackImageView);
                Glide.with(this).load(licensePhotoItem.full_path).into(licenseImageView);
            }catch (Exception e){
                e.printStackTrace();
            }

            setCarLicenseInfo(vehicleItem.car_license_info);
        }
    }

    @OnClick({R.id.tv_submit, R.id.vehicleFrontImageView, R.id.vehicleBackImageView, R.id.licenseImageView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.vehicleFrontImageView: {
                currentPhotoItem = frontPhotoItem;
                currentImageView = vehicleFrontImageView;
                PhotoManager.requestPhoto(this, 1);
            }
            break;
            case R.id.vehicleBackImageView: {
                currentPhotoItem = sidePhotoItem;
                currentImageView = vehicleBackImageView;
                PhotoManager.requestPhoto(this, 1);
            }
            break;
            case R.id.licenseImageView: {
                currentPhotoItem = licensePhotoItem;
                currentImageView = licenseImageView;
                PhotoManager.requestPhoto(this, 1);
            }
            break;
            case R.id.tv_submit:
                commitVehicle();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_CAPTURE) {
                String imagePath = PhotoManager.getImagePath();
                Bitmap bitmap = CameraUtils.getBitmapByPath(imagePath, 800, 480);
                ImageUtil.savePhotoLibrary(this, new File(imagePath));
                if (bitmap != null) {
                    uploadImage(imagePath, bitmap);
                }
            } else if (requestCode == Constants.REQUEST_CODE_IMAGE) {
                List<String> pathList = data.getStringArrayListExtra("result");
                Bitmap bitmap = ImageUtil.compressImage(pathList.get(0), 2);/**/
                if (bitmap != null) {
                    uploadImage(pathList.get(0), bitmap);
                }
            }
        }

    }

    private void setCarLicenseInfo(UploadCarLicenseResponse.CarLicenseInfo info) {
        this.carLicenseInfo = info;
        noTextView.setText(carLicenseInfo.ls_num);
        typeTextView.setText(carLicenseInfo.ls_type);
        ownerTextView.setText(carLicenseInfo.real_name);
        addressTextView.setText(carLicenseInfo.address);
        brandTextView.setText(carLicenseInfo.car_type);
        validateNoTextView.setText(carLicenseInfo.frame_no);
        engineNoTextView.setText(carLicenseInfo.engine_no);
        registerDateTextView.setText(carLicenseInfo.reg_date);
        cerDateTextView.setText(carLicenseInfo.issue_date);
    }

    private void uploadImage(String imagePath, final Bitmap bitmap) {
        startLoading();
        if (currentPhotoItem == licensePhotoItem) {
            UploadApis.uploadCarLicense(imagePath, new ResponseCallback<UploadCarLicenseResponse>() {
                @Override
                public void onSuccess(UploadCarLicenseResponse baseData) {
                    endLoading();
                    currentPhotoItem.bitmap = bitmap;
                    currentImageView.setImageBitmap(bitmap);
                    currentPhotoItem.full_path = baseData.data.img_url;
                    if (currentPhotoItem == licensePhotoItem) {
                        setCarLicenseInfo(baseData.data.car_license_info);
                    }
                }

                @Override
                public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                    endLoading();
                    ToastUtil.showToast(failDate.getErrmsg());
                }
            });
        } else {
            UploadApis.uploadNormalImage(imagePath, new ResponseCallback<UploadImageResponse>() {
                @Override
                public void onSuccess(UploadImageResponse baseData) {
                    endLoading();
                    currentPhotoItem.bitmap = bitmap;
                    currentImageView.setImageBitmap(bitmap);
                    currentPhotoItem.full_path = baseData.data.img_url;
                }

                @Override
                public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                    endLoading();
                    ToastUtil.showToast(failDate.getErrmsg());
                }
            });
        }
    }

    private void commitVehicle() {
        String vehicle_id = null;
        if (vehicleItem != null) {
            vehicle_id = vehicleItem.vehicle_id;
        }
        String vehicle_front_image = frontPhotoItem.full_path;
        String vehicle_side_image = sidePhotoItem.full_path;
        String license_photo = licensePhotoItem.full_path;
        String errMsg = "";
        if (TextUtils.isEmpty(vehicle_front_image)) {
            errMsg = "请上传车辆正面照片";
        } else if (TextUtils.isEmpty(vehicle_side_image)) {
            errMsg = "请上传车辆侧面照片";
        } else if (TextUtils.isEmpty(license_photo)) {
            errMsg = "请上传行驶证照片";
        }
        if (!TextUtils.isEmpty(errMsg)) {
            ToastUtil.showToast(errMsg);
            return;
        }
        startLoading();
        CommonApis.vehicleAuth(vehicle_id, vehicle_front_image, vehicle_side_image, license_photo, new ResponseCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseData) {
                endLoading();
                ToastUtil.showToast("车辆提交成功");
                EventBus.getDefault().post(new EventBusEvent.VehicleListChangedEvent());
                finish();
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });
    }
}
