package com.yun.yunmaster.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yun.yunmaster.R;
import com.yun.yunmaster.application.YunApplication;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.view.ActionSheetDialog;
import com.yuyh.library.imgsel.ISNav;
import com.yuyh.library.imgsel.common.ImageLoader;
import com.yuyh.library.imgsel.config.ISListConfig;

import java.io.File;
import java.util.Arrays;

import timber.log.Timber;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class PhotoManager {
    private static String imagePath = null;

    public static void requestPhoto(final BaseActivity context, final int num) {
        String[] photoSource = {"拍照", "从相册选择"};
        new ActionSheetDialog(context)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .setSheetItems(Arrays.asList(photoSource), ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                if (which == 1) {
                                    takePhoto(context);
                                } else if (which == 2) {
                                    chooseImage(context, num);
                                }
                            }
                        })
                .show();
    }

    private static void chooseImage(BaseActivity context, int num) {
        ISNav.getInstance().init(new ImageLoader() {
            @Override
            public void displayImage(Context context, String path, ImageView imageView) {
                Glide.with(YunApplication.getApp()).load(path).into(imageView);
            }
        });

        int color = ResourceUtil.getColor(context, R.color.color_blue);

        ISListConfig config = new ISListConfig.Builder()
                // 是否多选
                .multiSelect(true)
                .btnText("确认")
                // 确定按钮背景色
                //.btnBgColor(Color.parseColor(""))
                // 确定按钮文字颜色
                .btnTextColor(Color.WHITE)
                // 使用沉浸式状态栏
//                .statusBarColor(color)
                // 返回图标ResId
                .backResId(R.drawable.ic_back)
                .title("选择照片")
                .titleColor(Color.WHITE)
                .titleBgColor(color)
                .rememberSelected(false)
                .allImagesText("图片来源")
                .needCrop(false)
                .needCamera(false)
                // 最大选择图片数量
                .maxNum(num)
                .build();

        ISNav.getInstance().toListActivity(context, config, Constants.REQUEST_CODE_IMAGE);
    }

    private static void takePhoto(BaseActivity context) {
        //该照片的绝对路径
        imagePath = tmpImagePath();
        if(TextUtils.isEmpty(imagePath)){
            ToastUtil.showToast("请检查SD卡");
            return;
        }
        File out = new File(imagePath);
        Uri uri = Uri.fromFile(out);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        context.startActivityForResult(intent, Constants.REQUEST_CODE_CAPTURE);
    }

    public static String getImagePath() {
        return imagePath;
    }

    public static String commonTmpImagePath(){
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QingYunImage/";
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (savePath == null || "".equals(savePath)) {
            Timber.e("无法保存照片，请检查SD卡是否挂载");
            return null;
        }
        ;
        //照片命名
        String fileName = "UploadTmp.jpg";
        //该照片的绝对路径
        String imageTmpPath = savePath + fileName;
        return imageTmpPath;
    }

    public static String tmpImagePath(){
        String savePath = "";
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QingYunImage/";
            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (savePath == null || "".equals(savePath)) {
            Timber.e("无法保存照片，请检查SD卡是否挂载");
            return null;
        }

        String timeStamp = System.currentTimeMillis() + "";
        //照片命名
        String fileName = timeStamp + ".jpg";
        //该照片的绝对路径
        String imageTmpPath = savePath + fileName;
        return imageTmpPath;
    }
}

