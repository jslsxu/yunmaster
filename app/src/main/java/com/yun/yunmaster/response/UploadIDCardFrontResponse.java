package com.yun.yunmaster.response;

import com.yun.yunmaster.model.IDCardInfo;
import com.yun.yunmaster.model.UploadImageData;
import com.yun.yunmaster.network.base.response.BaseResponse;

public class UploadIDCardFrontResponse extends BaseResponse {
    public UploadIDCardFrontData data;
    public static class UploadIDCardFrontData extends UploadImageData{
        public IDCardInfo.IDCardFrontInfo id_card_front_info;
    }
}
