package com.yun.yunmaster.response;

import com.yun.yunmaster.model.UserData;
import com.yun.yunmaster.network.base.response.BaseResponse;

/**
 * Created by jslsxu on 2017/9/27.
 */

public class UserInfoResponse extends BaseResponse {

    public  Data  data;
    public static class Data{
        public UserData user_info;
    }
}
