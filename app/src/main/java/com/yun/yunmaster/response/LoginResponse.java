package com.yun.yunmaster.response;

import com.yun.yunmaster.model.UserData;
import com.yun.yunmaster.network.base.response.BaseResponse;

/**
 * Created by jslsxu on 2017/9/21.
 */

public class LoginResponse extends BaseResponse {

    public static final int TYPE_LOGIN_TYPE_VERIFY = 1;
    public static final int TYPE_LOGIN_TYPE_PWD = 2;
    public LoginData data;
    public static class LoginData{
        public UserData user_info;
        public String token;
    }
}
