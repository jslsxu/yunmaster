package com.yun.yunmaster.response;

import com.yun.yunmaster.network.base.response.BaseResponse;

/**
 * Created by jslsxu on 2017/10/26.
 */

public class WithdrawCashLimitResponse extends BaseResponse {

    public WithdrawCashLimitData data;

    public class WithdrawCashLimitData {
        public String limit;
    }
}
