package com.yun.yunmaster.response;

import com.yun.yunmaster.model.DriverInfo;
import com.yun.yunmaster.network.base.response.BaseResponse;

/**
 * Created by Xionghu on 2017/11/30.
 * Desc:
 */

public class DriverInfoResponse  extends BaseResponse {
    public  DriverInfoData data;
    public static class DriverInfoData{
        public DriverInfo user;

    }
}
