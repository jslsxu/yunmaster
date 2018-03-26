package com.yun.yunmaster.response;

import com.yun.yunmaster.network.base.response.BaseResponse;

/**
 * Created by jslsxu on 2018/1/23.
 */

public class ExpCodeResponse extends BaseResponse {
    public ExpCodeData data;

    public static class ExpCodeData{
        public String action;
    }
}
