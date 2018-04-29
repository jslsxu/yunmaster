package com.yun.yunmaster.response;

import com.yun.yunmaster.network.base.response.BaseResponse;

public class PickOrderResponse extends BaseResponse {
    public PickOrderData data;
    public static class PickOrderData{
        public int is_get_order;
    }
}
