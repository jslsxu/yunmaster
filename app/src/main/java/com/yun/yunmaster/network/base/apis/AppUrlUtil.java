package com.yun.yunmaster.network.base.apis;

import android.text.TextUtils;

import com.robin.lazy.cache.CacheLoaderManager;
import com.yun.yunmaster.utils.Constants;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class AppUrlUtil {
    public static final String APP_URL_RELEASE_TYPE_KEY = "AppUrlTypeReleaseKey";

    public static boolean appUtlTypeRelease() {
        String urlType = CacheLoaderManager.getInstance().loadString(APP_URL_RELEASE_TYPE_KEY);
        if (!TextUtils.isEmpty(urlType) && Integer.parseInt(urlType) > 0) {
            return true;
        }
        return false;
    }

    public static void setAppUrltype(boolean urlTypeRelease) {
        if (urlTypeRelease) {
            CacheLoaderManager.getInstance().saveString(APP_URL_RELEASE_TYPE_KEY, "1", Constants.CACHE_TIME);
        } else {
            CacheLoaderManager.getInstance().delete(APP_URL_RELEASE_TYPE_KEY);
        }
    }
}

