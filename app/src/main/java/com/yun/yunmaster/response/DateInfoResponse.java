package com.yun.yunmaster.response;

import com.yun.yunmaster.network.base.response.BaseResponse;

public class DateInfoResponse extends BaseResponse {
    public DateInfoData data;
    public static class DateInfoData{
        public String date;
        public String limit;
        public String week;
    }
}
