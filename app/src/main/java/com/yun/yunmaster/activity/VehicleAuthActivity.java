package com.yun.yunmaster.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.ImageUploadItem;
import com.yun.yunmaster.model.VehicleItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.UploadApis;
import com.yun.yunmaster.response.UploadCarLicenseResponse;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.CameraUtils;
import com.yun.yunmaster.utils.Constants;
import com.yun.yunmaster.utils.ImageUtil;
import com.yun.yunmaster.utils.PhotoManager;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class VehicleAuthActivity extends BaseActivity {

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

    private ImageUploadItem fronPhotoItem = new ImageUploadItem(null);
    private ImageUploadItem backPhotoItem = new ImageUploadItem(null);
    private ImageUploadItem licensePhotoItem = new ImageUploadItem(null);
    private ImageUploadItem currentPhotoItem;
    private ImageView currentImageView;
    private UploadCarLicenseResponse.CarLicenseInfo carLicenseInfo;

    public static void intentTo(Context context, VehicleItem vehicleItem) {
        Intent intent = new Intent(context, VehicleAuthActivity.class);
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


    }

    @OnClick({R.id.tv_submit, R.id.vehicleFrontImageView, R.id.vehicleBackImageView, R.id.licenseImageView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.vehicleFrontImageView:
            {
                currentPhotoItem = fronPhotoItem;
                currentImageView = vehicleFrontImageView;
                PhotoManager.requestPhoto(this,1);
            }
                break;
            case R.id.vehicleBackImageView:
            {
                currentPhotoItem = backPhotoItem;
                currentImageView = vehicleBackImageView;
                PhotoManager.requestPhoto(this,1);
            }
                break;
            case R.id.licenseImageView:
            {
                currentPhotoItem = licensePhotoItem;
                currentImageView = licenseImageView;
                PhotoManager.requestPhoto(this,1);
            }
                break;
            case R.id.tv_submit:
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

    private void setCarLicenseInfo(UploadCarLicenseResponse.CarLicenseInfo info){
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

    private void uploadImage(String imagePath, Bitmap bitmap){
        currentPhotoItem.bitmap = bitmap;
        currentImageView.setImageBitmap(bitmap);
        startLoading();
        UploadApis.uploadCarLicense(imagePath, new ResponseCallback<UploadCarLicenseResponse>() {
            @Override
            public void onSuccess(UploadCarLicenseResponse baseData) {
                endLoading();
                currentPhotoItem.full_path = baseData.data.img_url;
                if(currentPhotoItem == licensePhotoItem){
                    setCarLicenseInfo(baseData.data.car_license_info);
                }
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
            }
        });
    }
}
