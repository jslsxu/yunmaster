package com.yun.yunmaster.model;

import com.yun.yunmaster.network.base.response.BaseObject;

import java.util.List;


/**
 * Created by jslsxu on 2017/9/21.
 */

public class DriverInfo  extends BaseObject {
    public IDCardInfo.IDCardFrontInfo id_card_front_info;
    public IDCardInfo.IDCardBackInfo id_card_back_info;
    public DriverLicenseInfo driving_license;
}

