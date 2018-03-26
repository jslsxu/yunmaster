package com.yun.yunmaster.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.yun.yunmaster.network.base.callback.DownloadCallback;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.VersionResponse;
import com.yun.yunmaster.view.CommonDialog;

import java.io.File;
import java.util.ArrayList;

import timber.log.Timber;

import static com.yun.yunmaster.application.YunApplication.getApp;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class AppInfoUtil {

    public static final String NEW_APK_FILE_NAME = "newqingyun.apk";

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            //注意："com.example.try_downloadfile_progress"对应AndroidManifest.xml里的package="……"部分
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e("VerCodeError:" + e.getMessage());
        }
        return verCode;
    }

    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e("getVerNameError:" + e.getMessage());
        }
        return verName;
    }


    public static void checkVersion(final Context context) {
        CommonApis.getConfig(new ResponseCallback<VersionResponse>() {
            @Override
            public void onSuccess(final VersionResponse response) {
                final VersionResponse.DataBean data = response.getData();
                if (data == null) {
                    return;
                }
                if (AppInfoUtil.getVerCode(getApp()) < data.getVersionCode()) {
                    ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                    if (!response.getData().isForce()) {
                        CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_CANCEL, "下次再说", new CommonDialog.ActionCallback() {
                            @Override
                            public void onAction() {

                            }
                        });
                        actionList.add(cancelItem);
                    }
                    CommonDialog.ActionItem confirmItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "立刻升级", new CommonDialog.ActionCallback() {
                        @Override
                        public void onAction() {
                            boolean result = SystemUtils.goToMarket(context);
                            if (result) {
                                if (data.isForce()) {
                                    System.exit(0);
                                }
                            } else {
                                downloadApk(context, response.getData().getUrl(), data.isForce());
                            }
                        }
                    });
                    actionList.add(confirmItem);
                    CommonDialog.showDialog(context, "升级提醒", data.getIntro(), actionList);

                }
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {

            }


        });
    }

    public static void downloadApk(final Context context, String url, final boolean force) {
        //1.下载apk-Final
        //2.替换安装
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final ProgressDialog pBar = new ProgressDialog(context);
            pBar.setTitle("正在下载");
            pBar.setMessage("请稍后...");
            pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pBar.setCanceledOnTouchOutside(false);
            pBar.setCancelable(false);
            pBar.show();
            pBar.setMax(100);
            CommonApis.download(url, Environment.getExternalStorageDirectory().getAbsolutePath(), NEW_APK_FILE_NAME, new DownloadCallback() {
                @Override
                public void onSuccess(File response) {
                    pBar.cancel();
                    installApk(context);
                    if (force) {
                        System.exit(0);
                    }
                }

                @Override
                public void onFail(Exception e) {

                }

                @Override
                public void onProgress(float progress, long total) {
                    pBar.setProgress((int) (100 * progress));
                    pBar.setProgressNumberFormat(String.format("%.2fM/%.2fM", progress * total / 1024 / 1024, (float) total / 1024 / 1024));
                }
            });
        } else {
            ToastUtil.showToast("sdcard不可用");
        }
    }

    public static void installApk(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), NEW_APK_FILE_NAME)),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

