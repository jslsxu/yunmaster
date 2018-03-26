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

import com.bumptech.glide.Glide;
import com.flyco.roundview.RoundTextView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.application.YunApplication;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.VehicleItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.PublicParamsResponse;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.CameraUtils;
import com.yun.yunmaster.utils.Constants;
import com.yun.yunmaster.utils.ImageUtil;
import com.yun.yunmaster.utils.PhotoManager;
import com.yun.yunmaster.utils.ResourceUtil;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.CarTypeView;
import com.yun.yunmaster.view.ClearEditText;
import com.yun.yunmaster.view.CommonDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class VehicleAuthActivity extends BaseActivity {

    public static final String IS_EDIT_VEHICLE_KEY = "IS_EDIT_VEHICLE_KEY";
    public static final String VEHICLEITEM_KEY = "VEHICLEITEM_KEY";

    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.et_vehicle_no)
    ClearEditText etVehicleNo;
    @BindView(R.id.iv_vehicle_license)
    ImageView ivVehicleLicense;
    @BindView(R.id.iv_vehicle_photo)
    ImageView ivVehiclePhoto;
    @BindView(R.id.tv_vehicle_type)
    RoundTextView tvVehicleType;
    @BindView(R.id.et_vehicle_brand)
    ClearEditText etVehicleBrand;

    private int carTypePos = -1;
    private String vehicle_type;
    private String vehicleLicenseUploadPath = null;
    private String vehiclePhotoUploadpath = null;
    private String vehicle_id = null;
    private int photoType; //1.代表行驶证;2.代表车辆正面照

    public static void intentTo(Context context, VehicleItem vehicleItem) {
        Intent intent = new Intent(context, VehicleAuthActivity.class);
        if (vehicleItem == null) {
            intent.putExtra(IS_EDIT_VEHICLE_KEY, false);
        } else {
            intent.putExtra(IS_EDIT_VEHICLE_KEY, true);
            intent.putExtra(VEHICLEITEM_KEY, vehicleItem);
        }
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
        boolean isEditVehicle = intent.getBooleanExtra(IS_EDIT_VEHICLE_KEY, false);
        if (isEditVehicle) {
            VehicleItem vehicleItem = (VehicleItem) intent.getSerializableExtra(VEHICLEITEM_KEY);
            vehicle_id = vehicleItem.vehicle_id;
            etVehicleNo.setText(vehicleItem.vehicle_no);
            etVehicleBrand.setText(vehicleItem.vehicle_brand);
            if (vehicleItem.vehicle_type != null) {
                vehicle_type = vehicleItem.vehicle_type.car_type;
                tvVehicleType.setText(vehicleItem.vehicle_type.name);
                tvVehicleType.setTextColor(ResourceUtil.getColor(VehicleAuthActivity.this, R.color.color2));
            }
            if (vehicleItem.license_photo != null && !TextUtils.isEmpty(vehicleItem.license_photo.short_path) && !TextUtils.isEmpty(vehicleItem.license_photo.full_path)) {
                Glide.with(YunApplication.getApp()).load(vehicleItem.license_photo.full_path).into(ivVehicleLicense);
                vehicleLicenseUploadPath = vehicleItem.license_photo.short_path;
            }
            if (vehicleItem.vehicle_photo != null && !TextUtils.isEmpty(vehicleItem.vehicle_photo.short_path) && !TextUtils.isEmpty(vehicleItem.vehicle_photo.full_path)) {
                Glide.with(YunApplication.getApp()).load(vehicleItem.vehicle_photo.full_path).into(ivVehiclePhoto);
                vehiclePhotoUploadpath = vehicleItem.vehicle_photo.short_path;
            }
            PublicParamsResponse.DataBean publicParams = AppSettingManager.getPublicParams();
            if (publicParams != null && publicParams.getCar_type_list() != null && publicParams.getCar_type_list().size() > 0) {
                for (int i = 0; i < publicParams.getCar_type_list().size(); i++) {
                    if (publicParams.getCar_type_list().get(i).getCar_type().equals(vehicle_type)) {
                        carTypePos = i;
                        break;
                    }
                }
            } else {
                AppSettingManager.requestPublicParams();
            }
        }
    }

    @OnClick({R.id.tv_submit, R.id.tv_vehicle_type, R.id.tv_upload_vehicle_license, R.id.tv_upload_vehicle_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_vehicle_type:
                PublicParamsResponse.DataBean publicParams = AppSettingManager.getPublicParams();
                if (publicParams != null && publicParams.getCar_type_list() != null && publicParams.getCar_type_list().size() > 0) {
                    showCarTypeDialog(publicParams.getCar_type_list());
                } else {
                    AppSettingManager.requestPublicParams();
                }
                break;
            case R.id.tv_upload_vehicle_license: //车辆行驶证
                photoType = 1;
                showSelectPhotoDialog();
                break;
            case R.id.tv_upload_vehicle_photo: //车辆正面照
                photoType = 2;
                showSelectPhotoDialog();
                break;
            case R.id.tv_submit:
                if (getErrorMsg() == null) requestSubmitInfo();
                break;

        }
    }

    private String getErrorMsg() {
        String errorMsg = null;
        if (TextUtils.isEmpty(vehicle_type)) {
            errorMsg = "请选择车型";
        } else if (TextUtils.isEmpty(etVehicleNo.getText().toString().trim())) {
            errorMsg = "请输入车辆号码";
        } else if (TextUtils.isEmpty(etVehicleBrand.getText().toString().trim())) {
            errorMsg = "请输入车辆品牌型号";
        } else if (TextUtils.isEmpty(vehicleLicenseUploadPath)) {
            errorMsg = "请上传行驶证照片";
        } else if (TextUtils.isEmpty(vehiclePhotoUploadpath)) {
            errorMsg = "请上传车辆正面照片";
        }
        if (errorMsg != null) ToastUtil.showToast(errorMsg);
        return errorMsg;
    }


    private void requestSubmitInfo() {
        startLoading();
        CommonApis.vehicleAuth(vehicle_id, vehicle_type, etVehicleNo.getText().toString().trim(), etVehicleBrand.getText().toString().trim(), vehicleLicenseUploadPath, vehiclePhotoUploadpath, new ResponseCallback<BaseResponse>() {

            @Override
            public void onSuccess(BaseResponse baseData) {
                endLoading();
                EventBus.getDefault().post(new EventBusEvent.VehicleListChangedEvent());
                CommonDialog.ActionItem doneItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                    @Override
                    public void onAction() {
                        finish();
                    }
                });
                ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                actionList.add(doneItem);
                CommonDialog.showDialog(VehicleAuthActivity.this, "提交成功", "我们将尽快审核您的信息，反馈结果会以短信形式通知您！", actionList);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });

    }


    private void showSelectPhotoDialog() {
        PhotoManager.requestPhoto(this, 1);
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
                    uploadImage(bitmap);
                }
            } else if (requestCode == Constants.REQUEST_CODE_IMAGE) {
                List<String> pathList = data.getStringArrayListExtra("result");
                Bitmap bitmap = ImageUtil.compressImage(pathList.get(0), 2);/**/
                if (bitmap != null) {
                    uploadImage(bitmap);
                }
            }
        }

    }

    private void uploadImage(final Bitmap bitmap) {
//        startLoading();
//        String uploadImagePath = AliUploadManager.getInstance().upload(bitmap, new AliUploadManager.UploadImageCallback() {
//            @Override
//            public void onSuccess(String imagePath) {
//                endLoading();
//            }
//
//            @Override
//            public void onFail() {
//                endLoading();
//            }
//        });
//        setUploadImage(bitmap, uploadImagePath);
    }


    private void setUploadImage(Bitmap bitmap, String imagePath) {
        if (imagePath == null) return;
        if (photoType == 1) {
            vehicleLicenseUploadPath = imagePath;
            ivVehicleLicense.setImageBitmap(bitmap);
        } else if (photoType == 2) {
            vehiclePhotoUploadpath = imagePath;
            ivVehiclePhoto.setImageBitmap(bitmap);
        }
    }

    private void showCarTypeDialog(final List<PublicParamsResponse.DataBean.CarTypeListBean> carTypeList) {

        new CarTypeView() {
            @Override
            public void onItemSelect(int pos) {
                carTypePos = pos;
                vehicle_type = carTypeList.get(pos).getCar_type();
                tvVehicleType.setText(carTypeList.get(pos).getName());
                tvVehicleType.setTextColor(ResourceUtil.getColor(VehicleAuthActivity.this, R.color.color2));
            }
        }.show(VehicleAuthActivity.this, carTypeList, carTypePos);
    }
}
