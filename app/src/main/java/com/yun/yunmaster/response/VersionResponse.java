package com.yun.yunmaster.response;

import com.yun.yunmaster.network.base.response.BaseResponse;

/**
 * Created by Xionghu on 2017/9/28.
 * Desc:
 */

public class VersionResponse extends BaseResponse {

    /**
     * data : {"versionCode":3,"versionName":"1.0.1","url":"http://haocaisong.oss-cn-hangzhou.aliyuncs.com/driver/app_driver_1.0.1.apk","force":true,"intro":"1.优化了界面和交互\n2.增加了运行安全性和稳定性\n"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * versionCode : 3
         * versionName : 1.0.1
         * url : http://haocaisong.oss-cn-hangzhou.aliyuncs.com/driver/app_driver_1.0.1.apk
         * force : true
         * intro : 1.优化了界面和交互
         2.增加了运行安全性和稳定性

         */

        private int versionCode;
        private String versionName;
        private String url;
        private boolean force;
        private String intro;

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isForce() {
            return force;
        }

        public void setForce(boolean force) {
            this.force = force;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }
    }
}
