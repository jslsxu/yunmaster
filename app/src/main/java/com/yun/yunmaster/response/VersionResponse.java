package com.yun.yunmaster.response;

import com.yun.yunmaster.network.base.response.BaseResponse;

/**
 * Created by Xionghu on 2017/9/28.
 * Desc:
 */

public class VersionResponse extends BaseResponse {
    public VersionData data;

    public static class VersionData{
        public VersionInfo version;
    }

    public static class VersionInfo{
        public static final int UPDATE_TYPE_FOREC = 1;
        public static final int UPDATE_TYPE_ALERT = 2;
        public int versionNum;
        public String versionShow;
        public String remark;
        public String downloadUrl;
        public int updateType;
    }
}
