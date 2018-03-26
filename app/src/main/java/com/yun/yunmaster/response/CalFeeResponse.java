package com.yun.yunmaster.response;

import com.yun.yunmaster.network.base.response.BaseResponse;

/**
 * Created by jslsxu on 2017/10/26.
 */

public class CalFeeResponse extends BaseResponse {
    public CalFeeData data;

    public static class CalFeeData {
        public String total;
    }
}
