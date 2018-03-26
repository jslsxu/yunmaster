package com.yun.yunmaster.response;

import android.text.TextUtils;

import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.network.base.response.BaseResponse;

import java.util.List;

/**
 * Created by jslsxu on 2017/9/19.
 */

public class OrderListResponse extends BaseResponse {

    public OrderListData data;
    public static class OrderListData{
        public List<OrderItem> list;
        public String next;

        public boolean hasMore(){
            return !TextUtils.isEmpty(next);
        }
    }
}
