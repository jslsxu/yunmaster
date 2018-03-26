package com.yun.yunmaster.network.base.response;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class BaseResponse extends BaseObject {
    public static final int NETWORK_ERROR = -1;
    public static final int RESPONSE_SUCCESS = 0;

    private String errmsg;
    private int errno;

    public BaseResponse() {
        super();
        this.errno = NETWORK_ERROR;
        this.errmsg = "网络请求失败，请稍后再试";
    }

    public boolean success() {
        return errno == RESPONSE_SUCCESS;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

}


