package com.yun.yunmaster.model;

import com.yun.yunmaster.network.base.response.BaseObject;

/**
 * Created by jslsxu on 2018/4/7.
 */

public class IDCardInfo extends BaseObject {

    public static class IDCardFrontInfo extends BaseObject{
        public String img_url;
        public String id_card_address;
        public String id_card_birthday;
        public String id_card_name;
        public String id_card_no;
        public String id_card_sex;
        public String id_card_nation;
    }

    public static class IDCardBackInfo extends BaseObject{
        public String img_url;
        public String id_card_sign_start;
        public String id_card_sign_end;
        public String id_card_sign_station;
    }
}
