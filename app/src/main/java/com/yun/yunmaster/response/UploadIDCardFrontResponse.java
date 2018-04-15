package com.yun.yunmaster.response;

import com.yun.yunmaster.model.IDCardInfo;
import com.yun.yunmaster.model.UploadImageData;

public class UploadIDCardFrontResponse extends UploadImageResponse {
    public UploadIDCardFrontData data;
    public static class UploadIDCardFrontData extends UploadImageData{
        public IDCardInfo.IDCardFrontInfo id_card_front_info;
    }
}
