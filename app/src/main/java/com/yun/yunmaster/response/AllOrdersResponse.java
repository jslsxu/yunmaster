package com.yun.yunmaster.response;

import android.text.TextUtils;

import com.yun.yunmaster.model.OrderPickInfo;
import com.yun.yunmaster.network.base.response.BaseResponse;

import java.util.List;

/**
 * Created by jslsxu on 2018/4/5.
 */

public class AllOrdersResponse extends BaseResponse {
    public AllOrdersData data;
    public static class AllOrdersData {
        public List<OrderPickInfo> list;
        public String next;
        public boolean hasMore(){
            return !TextUtils.isEmpty(next);
        }
    }
}
