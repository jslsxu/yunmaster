package com.yun.yunmaster.model;

import com.yun.yunmaster.network.base.response.BaseObject;

import java.util.List;


/**
 * Created by jslsxu on 2017/9/21.
 */

public class DriverInfo  extends BaseObject {
    public String real_name;
    public String card_no;
    public List<PhotoItem> card_photo;
    public PhotoItem driver_license_photo;
}

