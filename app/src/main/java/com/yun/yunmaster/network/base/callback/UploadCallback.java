package com.yun.yunmaster.network.base.callback;

/**
 * Created by jslsxu on 2018/4/9.
 */

public abstract class UploadCallback {
    public abstract void onSuccess(String path);
    public abstract void onFail();
}
