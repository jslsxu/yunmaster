package com.yun.yunmaster.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Created by jslsxu on 2017/9/29.
 */

public class Utils {
    public static void call(Context context, String mobile) {
        if (!TextUtils.isEmpty(mobile)) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + mobile);
            intent.setData(data);
            context.startActivity(intent);
        }
    }
}
