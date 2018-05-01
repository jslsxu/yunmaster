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
import com.robin.lazy.util.extend.draw.ImageUtils;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.DriverInfo;
import com.yun.yunmaster.model.DriverLicenseInfo;
import com.yun.yunmaster.model.EventBusEvent;
import com.yun.yunmaster.model.IDCardInfo;
import com.yun.yunmaster.model.UserData;
import com.yun.yunmaster.network.base.apis.CommonInterface;
import com.yun.yunmaster.network.base.apis.HttpApiBase;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.network.httpapis.UploadApis;
import com.yun.yunmaster.response.DriverInfoResponse;
import com.yun.yunmaster.response.UploadDriverLicenseResponse;
import com.yun.yunmaster.response.UploadIDCardBackResponse;
import com.yun.yunmaster.response.UploadIDCardFrontResponse;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.CameraUtils;
import com.yun.yunmaster.utils.Constants;
import com.yun.yunmaster.utils.ImageUtil;
import com.yun.yunmaster.utils.LoginManager;
import com.yun.yunmaster.utils.PhotoManager;
import com.yun.yunmaster.utils.ResourceUtil;
import com.yun.yunmaster.utils.ToastUtil;

import org.apache.log4j.lf5.util.Resource;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class DriverAuthActivity extends BaseActivity {

    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_submit)
    RoundTextView tvSubmit;
    @BindView(R.id.idCardFrontImageView)
    ImageView idCardFrontImageView;
    @BindView(R.id.idCardBackImageView)
    ImageView idCardBackImageView;
    @BindView(R.id.nameTextView)
    TextView nameTextView;
    @BindView(R.id.idNoTextView)
    TextView idNoTextView;
    @BindView(R.id.birthdayTextView)
    TextView birthdayTextView;
    @BindView(R.id.addressTextView)
    TextView addressTextView;
    @BindView(R.id.validateDateTextView)
    TextView validateDateTextView;
    @BindView(R.id.licenseImageView)
    ImageView licenseImageView;
    @BindView(R.id.licenseNoTextView)
    TextView licenseNoTextView;
    @BindView(R.id.licenseNameTextView)
    TextView licenseNameTextView;
    @BindView(R.id.licenseSexTextView)
    TextView licenseSexTextView;
    @BindView(R.id.licenseAddressTextView)
    TextView licenseAddressTextView;
    @BindView(R.id.licenseBirthdayTextView)
    TextView licenseBirthdayTextView;
    @BindView(R.id.firstGetDateTextView)
    TextView firstGetDateTextView;
    @BindView(R.id.driveVehicleTextView)
    TextView driveVehicleTextView;
    @BindView(R.id.startDateTextView)
    TextView startDateTextView;
    @BindView(R.id.endDateTextView)
    TextView endDateTextView;

    private ImageView currentImageView;
    private DriverInfo mDriverInfo = new DriverInfo();
    private boolean canEdit = false;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, DriverAuthActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_auth);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        navigationBar.setTitle("车主认证");
        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        UserData userData = AppSettingManager.getUserData();
        if (userData.auth_type == 0) {
            setCanEdit(true);
        } else if (userData.auth_type == 1 || userData.auth_type == 2 || userData.auth_type == 4) {
            setCanEdit(false);
            getDriverInfo();
        } else if (userData.auth_type == 3) {
            getDriverInfo();
            setCanEdit(true);
        }

    }

    private void setCanEdit(boolean edit) {
        canEdit = edit;
        if (canEdit) {
            tvSubmit.setVisibility(View.VISIBLE);
        } else {
            tvSubmit.setVisibility(View.GONE);
            idCardFrontImageView.setImageDrawable(ResourceUtil.getDrawable(this, R.drawable.icon_list_tianjia_nor));
            idCardBackImageView.setImageDrawable(ResourceUtil.getDrawable(this, R.drawable.icon_list_tianjia_nor));
            licenseImageView.setImageDrawable(ResourceUtil.getDrawable(this, R.drawable.icon_list_tianjia_nor));
        }
    }

    private void setDriverInfo(DriverInfo driverInfo) {
        mDriverInfo = driverInfo;
        if (null != driverInfo.id_card_front_info) {
            IDCardInfo.IDCardFrontInfo frontInfo = driverInfo.id_card_front_info;
            nameTextView.setText(frontInfo.id_card_name);
            idNoTextView.setText(frontInfo.id_card_no);
            birthdayTextView.setText(frontInfo.id_card_birthday);
            addressTextView.setText(frontInfo.id_card_address);
            if (!canEdit) {
                String url = HttpApiBase.getSecureBaseUrl() + CommonInterface.SHOW_IMG + "?" + "pic=" + driverInfo.id_card_front_info.img_url
                        + "&token=" + LoginManager.getToken();
                Glide.with(this).load(url).into(idCardFrontImageView);
            }
        }
        if (null != driverInfo.id_card_back_info) {
            validateDateTextView.setText(driverInfo.id_card_back_info.id_card_sign_start + "-" + driverInfo.id_card_back_info.id_card_sign_end);
            if (!canEdit) {
                String url = HttpApiBase.getSecureBaseUrl() + CommonInterface.SHOW_IMG + "?" + "pic=" + driverInfo.id_card_back_info.img_url
                        + "&token=" + LoginManager.getToken();
                Glide.with(this).load(url).into(idCardBackImageView);
            }
        }
        if (null != driverInfo.driving_license) {
            DriverLicenseInfo licenseInfo = driverInfo.driving_license;
            licenseNoTextView.setText(licenseInfo.license_number);
            licenseNameTextView.setText(licenseInfo.real_name);
            licenseSexTextView.setText(licenseInfo.sex);
            licenseAddressTextView.setText(licenseInfo.address);
            licenseBirthdayTextView.setText(licenseInfo.birth);
            firstGetDateTextView.setText(licenseInfo.initial_date);
            driveVehicleTextView.setText(licenseInfo.type);
            startDateTextView.setText(licenseInfo.start_date);
            endDateTextView.setText(licenseInfo.end_date);
            if (!canEdit) {
                String url = HttpApiBase.getSecureBaseUrl() + CommonInterface.SHOW_IMG + "?" + "pic=" + driverInfo.driving_license.img_url
                        + "&token=" + LoginManager.getToken();
                Glide.with(this).load(url).into(licenseImageView);
            }
        }
    }

    private void getDriverInfo() {
        startLoading();
        CommonApis.getDriverInfo(new ResponseCallback<DriverInfoResponse>() {
            @Override
            public void onSuccess(DriverInfoResponse baseData) {
                endLoading();
                setDriverInfo(baseData.data);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
            }
        });
    }

    @OnClick({R.id.tv_submit, R.id.idCardFrontImageView, R.id.idCardBackImageView, R.id.licenseImageView})
    public void onViewClicked(View view) {
        if (!canEdit) {
            return;
        }
        switch (view.getId()) {
            case R.id.idCardFrontImageView: {
                currentImageView = idCardFrontImageView;
                PhotoManager.requestPhoto(this, 1);
            }
            break;
            case R.id.idCardBackImageView: {
                currentImageView = idCardBackImageView;
                PhotoManager.requestPhoto(this, 1);
            }
            break;
            case R.id.licenseImageView: {
                currentImageView = licenseImageView;
                PhotoManager.requestPhoto(this, 1);
            }
            break;
            case R.id.tv_submit:
                commitDriverAuth();
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
                if (bitmap != null) {
                    uploadImage(imagePath, bitmap);
                }
            } else if (requestCode == Constants.REQUEST_CODE_IMAGE) {
                List<String> pathList = data.getStringArrayListExtra("result");
                Bitmap bitmap = ImageUtil.compressImage(pathList.get(0), 4);/**/
                Timber.e("image size is %d", bitmap.getByteCount());
                if (bitmap != null) {
                    uploadImage(pathList.get(0), bitmap);
                }
            }
        }

    }

    private void uploadImage(String imagePath, final Bitmap bitmap) {
        startLoading();
        String desPath = imagePath;
        if(bitmap.getByteCount() > 100 * 1000){
            String tmpImagePath = PhotoManager.commonTmpImagePath();
            if(!TextUtils.isEmpty(tmpImagePath)){
                ImageUtil.saveBitmapToSDCard(bitmap, tmpImagePath);
                desPath = tmpImagePath;
            }
        }
        if (currentImageView == idCardFrontImageView) {
            UploadApis.uploadIDCardFront(desPath, new ResponseCallback<UploadIDCardFrontResponse>() {
                @Override
                public void onSuccess(UploadIDCardFrontResponse baseData) {
                    endLoading();
                    currentImageView.setImageBitmap(bitmap);
                    IDCardInfo.IDCardFrontInfo frontInfo = baseData.data.id_card_front_info;
                    frontInfo.img_url = baseData.data.img_url;
                    mDriverInfo.id_card_front_info = frontInfo;
                    setDriverInfo(mDriverInfo);
                }

                @Override
                public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                    endLoading();
                    ToastUtil.showToast(failDate.getErrmsg());
                }
            });
        } else if (currentImageView == idCardBackImageView) {
            UploadApis.uploadIDCardBack(desPath, new ResponseCallback<UploadIDCardBackResponse>() {
                @Override
                public void onSuccess(UploadIDCardBackResponse baseData) {
                    endLoading();
                    currentImageView.setImageBitmap(bitmap);
                    IDCardInfo.IDCardBackInfo backInfo = baseData.data.id_card_back_info;
                    backInfo.img_url = baseData.data.img_url;
                    mDriverInfo.id_card_back_info = backInfo;
                    setDriverInfo(mDriverInfo);
                }

                @Override
                public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                    endLoading();
                    ToastUtil.showToast(failDate.getErrmsg());
                }
            });
        } else if (currentImageView == licenseImageView) {
            UploadApis.uploadDriverLicense(desPath, new ResponseCallback<UploadDriverLicenseResponse>() {
                @Override
                public void onSuccess(UploadDriverLicenseResponse baseData) {
                    endLoading();
                    currentImageView.setImageBitmap(bitmap);
                    DriverLicenseInfo licenseInfo = baseData.data.driving_license_info;
                    licenseInfo.img_url = baseData.data.img_url;
                    mDriverInfo.driving_license = licenseInfo;
                    setDriverInfo(mDriverInfo);
                }

                @Override
                public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                    endLoading();
                    ToastUtil.showToast(failDate.getErrmsg());
                }
            });
        }
    }

    private void commitDriverAuth() {
        String id_front_image = mDriverInfo.id_card_front_info.img_url;
        String id_back_image = mDriverInfo.id_card_back_info.img_url;
        String license_photo = mDriverInfo.driving_license.img_url;
        String errMsg = "";
        if (TextUtils.isEmpty(id_front_image)) {
            errMsg = "请上传身份证正面照片";
        } else if (TextUtils.isEmpty(id_back_image)) {
            errMsg = "请上传身份证反面照片";
        } else if (TextUtils.isEmpty(license_photo)) {
            errMsg = "请上传驾驶证照片";
        }
        if (!TextUtils.isEmpty(errMsg)) {
            ToastUtil.showToast(errMsg);
            return;
        }
        startLoading();
        CommonApis.driverAuth(id_front_image, id_back_image, license_photo, new ResponseCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseData) {
                endLoading();
                ToastUtil.showToast("车主认证提交成功");
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

