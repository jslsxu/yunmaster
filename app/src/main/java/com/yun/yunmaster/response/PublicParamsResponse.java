package com.yun.yunmaster.response;


import com.yun.yunmaster.network.base.response.BaseResponse;

import java.util.List;

/**
 * Created by xionhgu on 2016/12/19.
 * Email：965705418@qq.com
 */

public class PublicParamsResponse extends BaseResponse {


    /**
     * data : {"contact_phone":"400-056-1615","policy":{"register_policy":"http://shop.test.haocaisong.cn/article/detail.php?id=1"},"car_type_list":[{"name":"金杯","car_type":1},{"name":"小货车","car_type":2}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * contact_phone : 400-056-1615
         * policy : {"register_policy":"http://shop.test.haocaisong.cn/article/detail.php?id=1"}
         * car_type_list : [{"name":"金杯","car_type":1},{"name":"小货车","car_type":2}]
         */

        private String contact_phone;
        private PolicyBean policy;
        private List<CarTypeListBean> car_type_list;

        public String getContact_phone() {
            return contact_phone;
        }

        public void setContact_phone(String contact_phone) {
            this.contact_phone = contact_phone;
        }

        public PolicyBean getPolicy() {
            return policy;
        }

        public void setPolicy(PolicyBean policy) {
            this.policy = policy;
        }

        public List<CarTypeListBean> getCar_type_list() {
            return car_type_list;
        }

        public void setCar_type_list(List<CarTypeListBean> car_type_list) {
            this.car_type_list = car_type_list;
        }

        public static class PolicyBean {
            /**
             * register_policy : http://shop.test.haocaisong.cn/article/detail.php?id=1
             */

            public String register_policy;
            public String charge_standard_policy;
            public String invite_url;
            public String fee_policy;
        }

        public static class CarTypeListBean {
            /**
             * name : 金杯
             * car_type : 1
             */

            private String name;
            private String car_type;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCar_type() {
                return car_type;
            }

            public void setCar_type(String car_type) {
                this.car_type = car_type;
            }
        }
    }
}
