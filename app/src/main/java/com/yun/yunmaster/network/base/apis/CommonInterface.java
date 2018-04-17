package com.yun.yunmaster.network.base.apis;

/**
 * Created by jslsxu on 2018/3/24.
 */

public interface CommonInterface {

    /**
     * 公共
     */

    public static final String GET_VERSION_INFO = "common/get_config";

    public static final String COMMON_PUBLIC_PARAMS = "common/public_params";

    public static final String CITY_LIST = "city/city_list";

    public static final String EXP_CODE = "scan_code/exp_code";

    public static final String PAY_WASTE_FEE = "pay/pay_waste_fee";

    /**
     * 用户
     */

    public static final String USER_LOGIN = "login";

    public static final String USER_ACCOUNT_LOGIN = "user/account_login";

    public static final String USER_REGISTER = "user/register";

    public static final String USER_CHANGE_PWD = "user/change_pwd";

    public static final String USER_INFO = "user_info";

    public static final String USER_DRIVER_CERTIFICATE = "user/driver_certificate";

    public static final String USER_MY_VEHICLE_LIST = "my_vehicle_list";

    public static final String USER_DRIVER_INFO = "user/driver_info";

    public static final String USER_VEHICLE_DELETE = "vehicle_del";

    public static final String USER_DRIVER_AUTH = "user/driver_auth";

    public static final String USER_VEHICLE_AUTH = "vehicle_auth";

    public static final String USER_SEND_VERIFY_CODE = "sendSMS";

    public static final String USER_SAVE_REGID = "save_regid";

    public static final String USER_SET_ORDER_PUSH = "set_order_push";

    public static final String USER_WITHDRAW_CASH = "user/withdraw_cash";

    public static final String USER_BALANCE_LIST = "balance_list";

    public static final String USER_REPORT_YARD_INFO = "user/report_yard_info";

    public static final String USER_CASH_LIMIT = "user/cash_limit";

    public static final String SELECT_CITY = "user/select_city";

    public static final String DRIVER_LOCATION = "user/driver_location";

    public static final String FEEDBACK = "feedback";
    /**
     * 订单
     */

    public static final String MY_ORDER_LIST = "my_orders";

    public static final String ALL_ORDER_LIST = "all_orders";

    public static final String TAKE_ORDER = "take_order";

    public static final String CANCEL_ORDER = "cancel_order";

    public static final String ORDER_DETAIL = "order_detail";

    public static final String MODIFY_PRICE = "adjust_price";

    public static final String ORDER_COMPLETE = "uploadOrderImages";

    public static final String UPDATE_ORDER_STATUS = "update_order_status";

    public static final String CAL_FEE = "cal_fee";

    public static final String USER_ADD_YARD = "user/add_yard";

    public static final String UPDATE_LOCATION = "update_location";

    /**
     * 上传图片
     */

    public static final String UPLOAD_IMAGE = "uploadImage";
}

