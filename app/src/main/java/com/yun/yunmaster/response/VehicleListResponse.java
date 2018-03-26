package com.yun.yunmaster.response;

import android.text.TextUtils;

import com.yun.yunmaster.model.VehicleItem;
import com.yun.yunmaster.network.base.response.BaseResponse;

import java.util.List;

/**
 * Created by jslsxu on 2017/9/19.
 */

public class VehicleListResponse extends BaseResponse {

    public VehicleListData data;
    public static class VehicleListData{
        public List<VehicleItem> list;
        public String next;
        public boolean hasMore(){
            return !TextUtils.isEmpty(next);
        }
    }

}
