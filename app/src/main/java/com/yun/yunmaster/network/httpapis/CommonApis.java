package com.yun.yunmaster.network.httpapis;

import android.text.TextUtils;

import com.yun.yunmaster.network.base.apis.HttpApiBase;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.response.CityListResponse;
import com.yun.yunmaster.response.DriverInfoResponse;
import com.yun.yunmaster.response.ExpCodeResponse;
import com.yun.yunmaster.response.IncomeExpensesResponse;
import com.yun.yunmaster.response.LoginResponse;
import com.yun.yunmaster.response.PublicParamsResponse;
import com.yun.yunmaster.response.UserInfoResponse;
import com.yun.yunmaster.response.VehicleListResponse;
import com.yun.yunmaster.response.VersionResponse;
import com.yun.yunmaster.response.WithdrawCashLimitResponse;

import java.util.HashMap;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class CommonApis extends HttpApiBase {
    public static void getConfig(ResponseCallback<VersionResponse> callback) {
        get(GET_VERSION_INFO, null, callback);
    }

    public static void userLogin(String mobile, String verify_code,String password,int loginType, ResponseCallback<LoginResponse> callback) {
        if(loginType== LoginResponse.TYPE_LOGIN_TYPE_VERIFY){
            HashMap<String, String> params = new HashMap<>();
            params.put("mobile", mobile);
            params.put("verify_code", verify_code);
            get(USER_LOGIN, params, callback);
        }else {
            HashMap<String, String> params = new HashMap<>();
            params.put("mobile", mobile);
            params.put("password", password);
            get(USER_ACCOUNT_LOGIN, params, callback);
        }

    }

    public static void userRegister(String mobile, String password, String verify_code, String invite_code, ResponseCallback<LoginResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("password", password);
        params.put("verify_code", verify_code);
        params.put("invite_code",invite_code);
        get(USER_REGISTER, params, callback);
    }
    public static void userChangePwd(String mobile, String password, String verify_code,ResponseCallback<BaseResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile", mobile);
        params.put("password", password);
        params.put("verify_code", verify_code);
        get(USER_CHANGE_PWD, params, callback);
    }
    public static void saveRegid(String regid) {
        HashMap<String, String> params = new HashMap<>();
        params.put("regid", regid);
        get(USER_SAVE_REGID, params, null);
    }


    public static void sendSms(String mobile, ResponseCallback<BaseResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile", mobile);
        get(USER_SEND_VERIFY_CODE, params, callback);
    }

    public static void getUserInfo(ResponseCallback<UserInfoResponse> callback) {
        get(USER_INFO, null, callback);
    }


    public static void getPublicParams(ResponseCallback<PublicParamsResponse> callback) {
        get(COMMON_PUBLIC_PARAMS, null, callback);
    }

    public static void vehicleDelete(String vehicle_id, ResponseCallback<BaseResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("vehicle_id", vehicle_id);
        get(USER_VEHICLE_DELETE, params, callback);
    }

    public static void getVehicleList(String start, ResponseCallback<VehicleListResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("start", start);
        get(USER_MY_VEHICLE_LIST, params, callback);
    }

    public static void driverAuth(String real_name, String card_no, String card_photo, String driver_license_photo, ResponseCallback<BaseResponse> callback) {

        HashMap<String, String> params = new HashMap<>();
        params.put("real_name", real_name);
        params.put("card_no", card_no);
        params.put("card_photo", card_photo);
        params.put("driver_license_photo", driver_license_photo);
        get(USER_DRIVER_AUTH, params, callback);
    }

    public static void getDriverInfo(ResponseCallback<DriverInfoResponse> callback) {
        get(USER_DRIVER_INFO, null, callback);
    }

    public static void vehicleAuth( String vehicle_id,String vehicle_type, String vehicle_no, String vehicle_brand,
                                    String license_photo, String vehicle_photo, ResponseCallback<BaseResponse> callback) {

        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(vehicle_id)) {
            params.put("vehicle_id", vehicle_id);
        }
        params.put("vehicle_type", vehicle_type);
        params.put("vehicle_no", vehicle_no);
        params.put("vehicle_brand", vehicle_brand);
        params.put("license_photo", license_photo);
        params.put("vehicle_photo", vehicle_photo);
        get(USER_VEHICLE_AUTH, params, callback);
    }


    public static void driverCertificate(String truename, String id_no, String bankcard_num, String bank_deposit, String driving_license_pic_url,
                                         String vehicle_type, String vehicle_no, String vehicle_license_pic_url, ResponseCallback<BaseResponse> callback) {

        HashMap<String, String> params = new HashMap<>();
        params.put("truename", truename);
        params.put("id_no", id_no);
        params.put("bankcard_num", bankcard_num);
        params.put("bank_deposit", bank_deposit);
        params.put("driving_license_pic_url", driving_license_pic_url);
        params.put("vehicle_type", vehicle_type);
        params.put("vehicle_no", vehicle_no);
        params.put("vehicle_license_pic_url", vehicle_license_pic_url);
        get(USER_DRIVER_CERTIFICATE, params, callback);
    }

    public static void setOrderPush(int orderPush, ResponseCallback<BaseResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("open", Integer.toString(orderPush));
        get(USER_SET_ORDER_PUSH, params, callback);
    }


    public static void commonWithdrawCash(String bank_name, String name, String bankcard_no, String amount, ResponseCallback<BaseResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("bank_name", bank_name);
        params.put("name", name);
        params.put("bankcard_no", bankcard_no);
        params.put("amount", amount);
        get(USER_WITHDRAW_CASH, params, callback);
    }

    public static void getBalanceList(String start, ResponseCallback<IncomeExpensesResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("start", start);
        get(USER_BALANCE_LIST, params, callback);
    }

    public static void reportYardInfo(String yard_id, String info, ResponseCallback<BaseResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("yard_id", yard_id);
        params.put("info", info);
        post(USER_REPORT_YARD_INFO, params, callback);
    }


    public static void cityList(String ver, ResponseCallback<CityListResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("ver", ver);
        get(CITY_LIST, params, callback);
    }

    public static void withdrawCashLimit(ResponseCallback<WithdrawCashLimitResponse> callback) {
        get(USER_CASH_LIMIT, null, callback);
    }


    public static void addYard(String yard_address, String lng, String lat, String yard_name, String opening_time, String other_info, ResponseCallback<BaseResponse> callback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("yard_address", yard_address);
        params.put("lng", lng);
        params.put("lat", lat);
        params.put("yard_name", yard_name);
        params.put("opening_time", opening_time);
        if (!TextUtils.isEmpty(other_info)) {
            params.put("other_info", other_info);
        }
        post(USER_ADD_YARD, params, callback);
    }


    public static void selectCity() {
        get(SELECT_CITY, null, null);
    }

    public static void driverLocation(double lat, double lng, ResponseCallback<BaseResponse
            > callback){
        HashMap<String, String> params = new HashMap<>();
        params.put("lat", lat + "");
        params.put("lng", lng + "");
        get(DRIVER_LOCATION, params, callback);
    }

    public static void expCode(String qrCode, ResponseCallback<ExpCodeResponse> callback){
        HashMap<String, String> params = new HashMap<>();
        params.put("code", qrCode);
        get(EXP_CODE, params, callback);
    }

    public static void payWasteFee(String oid, String fee, String yardId, ResponseCallback<BaseResponse> callback){
        HashMap<String, String> params = new HashMap<>();
        params.put("oid", oid);
        params.put("fee", fee);
        params.put("yard_id", yardId);
        get(PAY_WASTE_FEE, params, callback);
    }
}
