package com.yun.yunmaster.model;

import com.yun.yunmaster.network.base.response.BaseObject;

/**
 * Created by jslsxu on 2018/4/5.
 */

public class OrderPickInfo extends BaseObject {
    public String oid;
    public String time;
    public String date;
    public String address;
    public String total;
    public String total_price;
    public int time_slot;
    public double lat;
    public double lng;
}
