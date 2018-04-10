package com.yun.yunmaster.model;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jslsxu on 2017/9/21.
 */

public class OrderDetail extends OrderItem {
    public OrderItem.CustomerInfo customer;
    public AddressInfo detail_address;
    public List<InfoItem> fee_items;
    public List<String> photo;
    public List<String> complete_photo;

    public List<InfoItem> orderInfoList(){
        ArrayList<InfoItem> infoList = new ArrayList<>();
        InfoItem oidItem = new InfoItem();
        oidItem.key = "订单号";
        oidItem.value = oid;
        infoList.add(oidItem);

        InfoItem item = new InfoItem();
        item.key = "车型";
        item.value = vehicle;
        infoList.add(item);

        InfoItem item2 = new InfoItem();
        item2.key = "车次";
        item2.value = transport_times + "";
        infoList.add(item2);

        InfoItem item3 = new InfoItem();
        item3.key = "用车时间";
        item3.value = time;
        infoList.add(item3);

        if(detail_address != null){
            if(!TextUtils.isEmpty(detail_address.address)){
                InfoItem item4 = new InfoItem();
                item4.key = "地址";
                item4.value = detail_address.address;
                item4.isAddress = true;
                infoList.add(item4);
            }

            if(!TextUtils.isEmpty(detail_address.name)){
                InfoItem nameItem = new InfoItem();
                nameItem.key = "联系人";
                nameItem.value = detail_address.name;
                infoList.add(nameItem);
            }

            if(!TextUtils.isEmpty(detail_address.mobile)){
                InfoItem mobileItem = new InfoItem();
                mobileItem.key = "联系电话";
                mobileItem.value = detail_address.mobile;
                mobileItem.isMobile = true;
                infoList.add(mobileItem);
            }
        }

        if(!TextUtils.isEmpty(comment)){
            InfoItem item6 = new InfoItem();
            item6.key = "订单备注";
            item6.value = comment;
            infoList.add(item6);
        }

        return infoList;
    }

    public boolean needTimer(){
        if(!this.is_cancel && this.step >= ORDER_STATUS_ACCEPTED && this.step <= ORDER_STATUS_COMPLETED_CONFIRMED){
            return true;
        }
        return false;
    }
}
