package com.yun.yunmaster.network.httpapis;

import com.yun.yunmaster.network.base.apis.HttpApiBase;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.response.CalFeeResponse;
import com.yun.yunmaster.response.OrderDetailResponse;
import com.yun.yunmaster.response.OrderListResponse;

import java.util.HashMap;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class OrderApis extends HttpApiBase {
    public static void getMyOrderList(int type, String start, ResponseCallback<OrderListResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", Integer.toString(type));
        params.put("start", start);
        get(MY_ORDER_LIST, params, callback);
    }

    public static void getAllOrderList(String start, ResponseCallback<OrderListResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("start", start);
        get(ALL_ORDER_LIST, params, callback);
    }

    public static void takeOrder(String oid, ResponseCallback<BaseResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("oid", oid);
        get(TAKE_ORDER, params, callback);
    }

    public static void cancelOrder(String oid, ResponseCallback<BaseResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("oid", oid);
        get(CANCEL_ORDER, params, callback);
    }

    public static void orderDetail(String oid, ResponseCallback<OrderDetailResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("oid", oid);
        get(ORDER_DETAIL, params, callback);
    }

    public static void modifyPrice(String oid, int times, String extra, ResponseCallback<OrderDetailResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("oid", oid);
        params.put("times", times + "");
        params.put("extra", extra);
        get(MODIFY_PRICE, params, callback);
    }

    public static void updateOrderStatus(String oid, ResponseCallback<OrderDetailResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("oid", oid);
        get(UPDATE_ORDER_STATUS, params, callback);
    }

    public static void orderComplete(String oid, String photo, ResponseCallback<OrderDetailResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("oid", oid);
        params.put("photo", photo);
        get(ORDER_COMPLETE, params, callback);
    }

    public static void callFee(String oid, int times, String extra, ResponseCallback<CalFeeResponse> callback){
        HashMap<String, String> params = new HashMap<>();
        params.put("oid", oid);
        params.put("times", Integer.toString(times));
        params.put("extra", extra);
        get(CAL_FEE, params, callback);
    }
}
