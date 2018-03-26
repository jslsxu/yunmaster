package com.yun.yunmaster.network.base.callback;

import java.io.File;

/**
 * Created by jslsxu on 2018/3/24.
 */

public abstract class DownloadCallback {
    public abstract void onSuccess(File response);

    public abstract void onFail(Exception e);

    public abstract void onProgress(float progress, long total);
}
