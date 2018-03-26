package com.yun.yunmaster.response;

import com.yun.yunmaster.model.CityInfo;
import com.yun.yunmaster.network.base.response.BaseResponse;

import java.util.List;

/**
 * Created by jslsxu on 2017/10/25.
 */

public class CityListResponse extends BaseResponse {

    public CityListData data;
    public static class CityListData {
        public String ver;
        public List<CityGroup> list;
    }

    public static class CityGroup {
        public String index;
        public List<CityInfo> cities;

        public String getShortIndex() {
            if (index.length() > 1) {
                return "★";
            } else return index;
        }
    }
}