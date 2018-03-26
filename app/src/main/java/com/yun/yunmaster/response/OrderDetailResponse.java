package com.yun.yunmaster.response;

import com.yun.yunmaster.model.OrderDetail;
import com.yun.yunmaster.network.base.response.BaseResponse;

/**
 * Created by jslsxu on 2017/9/21.
 */

public class OrderDetailResponse extends BaseResponse {

    public OrderDetailData data;
    public static class OrderDetailData{
        public OrderDetail order;
    }

    public OrderDetail getOrder(){
        if(data != null){
            return data.order;
        }
        return null;
    }
}
