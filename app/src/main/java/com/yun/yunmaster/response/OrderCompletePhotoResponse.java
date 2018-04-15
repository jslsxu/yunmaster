package com.yun.yunmaster.response;

import com.yun.yunmaster.network.base.response.BaseResponse;

import java.util.List;

public class OrderCompletePhotoResponse extends BaseResponse {

    public OrderCompletePhotoData data;
    public static class OrderCompletePhotoData {
        public List<String> complete_photos;
    }
}
