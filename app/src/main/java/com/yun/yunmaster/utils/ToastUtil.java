package com.yun.yunmaster.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.yun.yunmaster.application.YunApplication;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class ToastUtil {
    private static Toast toast;

    /**
     * 显示提示信息
     *
     * @param msg 文本内容
     */
    public static void showToast(String msg) {
        showToast(YunApplication.getApp(), msg, false);
    }

    /**
     * 显示提示信息
     *
     * @param resId 文本资源Id
     */
    public static void showToast(int resId) {
        if (resId <= 0) return;
        Context context = YunApplication.getApp();
        showToast(context, context.getString(resId), false);
    }

    /**
     * 显示提示信息
     *
     * @param msg 文本内容
     */
    public static void showLongToast(String msg) {
        showToast(YunApplication.getApp(), msg, true);
    }
    /**
     * 显示提示信息
     *
     * @param resId 文本资源Id
     */
    public static void showLongToast(int resId) {
        if (resId <= 0) return;
        Context context = YunApplication.getApp();
        showToast(context, context.getString(resId), true);
    }

    /**
     * 显示提示信息
     *
     * @param context 上下文信息
     * @param msg     文本内容
     */
    public static void showToast(final Context context, final String msg, final boolean lengthLong) {
        if (context == null || TextUtils.isEmpty(msg)) return;
        Runnable runnable = new Runnable() {
            public void run() {
                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
                int duration = lengthLong ? toast.LENGTH_LONG : Toast.LENGTH_SHORT;
                toast = Toast.makeText(context, msg, duration);
                toast.show();
            }
        };
        YunApplication application = YunApplication.getApp();
        application.runOnUIThread(runnable);
    }
}

