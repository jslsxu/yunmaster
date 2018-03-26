package com.yun.yunmaster.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yun.yunmaster.R;
import com.yun.yunmaster.application.YunApplication;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.DriverInfo;
import com.yun.yunmaster.model.ImageUploadItem;
import com.yun.yunmaster.model.UserData;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.DriverInfoResponse;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.CameraUtils;
import com.yun.yunmaster.utils.Constants;
import com.yun.yunmaster.utils.ImageUtil;
import com.yun.yunmaster.utils.PhotoManager;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.ClearEditText;
import com.yun.yunmaster.view.CommonDialog;
import com.yun.yunmaster.view.GridViewForScrollView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class DriverAuthActivity extends BaseActivity {
    public static final int MAX_IMAGE_COUNT = 2;
    protected ArrayList<ImageUploadItem> photoList = new ArrayList<>();
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.photoGridView)
    GridViewForScrollView photoGridView;
    @BindView(R.id.et_truename)
    ClearEditText etTruename;
    @BindView(R.id.et_id_no)
    ClearEditText etIdNo;
    @BindView(R.id.iv_driving_license)
    ImageView ivDrivingLicense;
    private BaseAdapter photoAdapter;
    private int imageUploadType; //当1的时候 上传身份证 当2的时候 上传驾驶证
    private String drivingLicenseUploadPath = null;

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

    public void init() {
        navigationBar.setTitle("车主认证");
        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        UserData userData = AppSettingManager.getUserData();
        if (userData.auth_type == -1) {
            requestDriverInfo();
        }
//        //TODO
//        requestDriverInfo();
        photoAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return Math.min(photoList.size() + 1, 2);
            }

            @Override
            public Object getItem(int position) {
                if (position < photoList.size()) {
                    return photoList.get(position);
                } else {
                    return R.drawable.icon_list_tianjia_nor;
                }
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View cell = convertView;
                Context context = DriverAuthActivity.this;
                if (cell == null) {
                    cell = LayoutInflater.from(context).inflate(R.layout.driver_cert_image_cell, parent, false);
                }
                ImageButton deleteButton = (ImageButton) cell.findViewById(R.id.removeButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteImageAtIndex(position);
                    }
                });
                ImageView imageView = (ImageView) cell.findViewById(R.id.imageView);
                Drawable addDrawable = context.getResources().getDrawable(R.drawable.icon_list_tianjia_nor);
                if (position < photoList.size()) {
                    if (photoList.get(position).bitmap == null && photoList.get(position).full_path != null) {
                        Glide.with(YunApplication.getApp()).load(photoList.get(position).full_path).into(imageView);
                    } else {
                        imageView.setImageBitmap(photoList.get(position).bitmap);
                    }

                    deleteButton.setVisibility(View.VISIBLE);
                } else {
                    imageView.setImageDrawable(addDrawable);
                    deleteButton.setVisibility(View.GONE);
                }

                return cell;
            }
        };
        photoGridView.setAdapter(photoAdapter);
        photoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == photoList.size()) {
                    if (MAX_IMAGE_COUNT <= photoList.size()) {
                        ToastUtil.showToast("最多添加" + MAX_IMAGE_COUNT + "张图片");
                    } else {
                        imageUploadType = 1;
                        showSelectPhotoDialog();
                    }

                }

            }
        });

    }

    private void showSelectPhotoDialog() {
        PhotoManager.requestPhoto(this, imageUploadType == 1 ? (MAX_IMAGE_COUNT - photoList.size()) : 1);
    }


    private void deleteImageAtIndex(int position) {
        photoList.remove(position);
        photoAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_CAPTURE) {
                String imagePath = PhotoManager.getImagePath();
                Bitmap bitmap = CameraUtils.getBitmapByPath(imagePath, 640, 960);
                ImageUtil.savePhotoLibrary(this, new File(imagePath));
                if (bitmap != null) {
                    ArrayList<Bitmap> imageList = new ArrayList<>();
                    imageList.add(bitmap);
                    addImage(imageList);
                }
            } else if (requestCode == Constants.REQUEST_CODE_IMAGE) {
                List<String> pathList = data.getStringArrayListExtra("result");
                ArrayList<Bitmap> imageList = new ArrayList<>();
                for (int i = 0; i < pathList.size(); i++) {
                    Bitmap bitmap = ImageUtil.compressImage(pathList.get(i), 2);
                    imageList.add(bitmap);
                }
                addImage(imageList);
            }

        }
    }

    private void addImage(final ArrayList<Bitmap> imageList) {
        for (int i = 0; i < imageList.size(); i++) {
//            Bitmap imageBitmap = imageList.get(i);
//            ImageUploadItem item = new ImageUploadItem(imageBitmap);
//            startLoading();
//            String uploadString = AliUploadManager.getInstance().upload(imageBitmap, new AliUploadManager.UploadImageCallback() {
//                @Override
//                public void onSuccess(String imagePath) {
//                    endLoading();
////                    if (imageUploadType == 2) {
////                        drivingLicenseUploadPath = imagePath;
////                        ivDrivingLicense.setImageBitmap(imageList.get(0));
////                    }
//                }
//
//                @Override
//                public void onFail() {
//                    endLoading();
//                }
//            });
//            if (imageUploadType == 2) {
//                drivingLicenseUploadPath = uploadString;
//                ivDrivingLicense.setImageBitmap(imageList.get(0));
//                return;
//            }
//            item.short_path = uploadString;
//            photoList.add(item);
        }
        photoAdapter.notifyDataSetChanged();
    }


    @OnClick({R.id.tv_upload_driving_license, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_upload_driving_license:
                imageUploadType = 2;
                showSelectPhotoDialog();
                break;
            case R.id.tv_submit:

                if (getErrorMsg() == null) requestSubmitInfo();
                break;
        }
    }

    private String getErrorMsg() {
        Timber.e("imagePath99991" + drivingLicenseUploadPath);
        String errorMsg = null;
        if (TextUtils.isEmpty(etTruename.getText().toString().trim())) {
            errorMsg = "请输入真实姓名";
        } else if (TextUtils.isEmpty(etIdNo.getText().toString().trim())) {
            errorMsg = "请输入身份证号码";
        } else if (photoList.size() < 2) {
            errorMsg = "请上传身份证正反两张照片";
        } else if (drivingLicenseUploadPath == null) {
            errorMsg = "请上传驾驶证照片";
        }
        if (errorMsg != null) ToastUtil.showToast(errorMsg);
        return errorMsg;
    }


    private void requestSubmitInfo() {
        String card_photo = photoList.get(0).short_path + "," + photoList.get(1).short_path;
        startLoading();
        CommonApis.driverAuth(etTruename.getText().toString().trim(), etIdNo.getText().toString().trim(), card_photo, drivingLicenseUploadPath, new ResponseCallback<BaseResponse>() {

            @Override
            public void onSuccess(BaseResponse baseData) {
                endLoading();
                CommonDialog.ActionItem doneItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                    @Override
                    public void onAction() {
                        finish();
                    }
                });
                ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                actionList.add(doneItem);
                CommonDialog.showDialog(DriverAuthActivity.this, "提交成功", "我们将尽快审核您的信息，反馈结果会以短信形式通知您！", actionList);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(failDate.getErrmsg());
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
//        refreshLayout.setEnabled(false);
//        refreshLayout.setRefreshing(false);
//        setRefreshLayoutEnabled = false;


        etTruename.setText(user.real_name);
        etIdNo.setText(user.card_no);
        for (int i = 0; user.card_photo != null && i < user.card_photo.size(); i++) {
            ImageUploadItem imageUploadItem = new ImageUploadItem(null);
            imageUploadItem.short_path = user.card_photo.get(i).short_path;
            imageUploadItem.full_path = user.card_photo.get(i).full_path;
            photoList.add(imageUploadItem);
        }
        photoAdapter.notifyDataSetChanged();
        if (user.driver_license_photo != null && !TextUtils.isEmpty(user.driver_license_photo.full_path)) {
            Glide.with(YunApplication.getApp()).load(user.driver_license_photo.full_path).into(ivDrivingLicense);
            drivingLicenseUploadPath = user.driver_license_photo.short_path;
        }

    }
}

