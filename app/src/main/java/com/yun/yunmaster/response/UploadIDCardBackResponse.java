package com.yun.yunmaster.response;

import com.yun.yunmaster.model.IDCardInfo;
import com.yun.yunmaster.model.UploadImageData;

public class UploadIDCardBackResponse extends UploadImageResponse {

    public UploadIDCardBackData data;
    public static class UploadIDCardBackData extends UploadImageData{
        public IDCardInfo.IDCardBackInfo id_card_back_info;
    }
}
