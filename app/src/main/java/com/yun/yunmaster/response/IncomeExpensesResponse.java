package com.yun.yunmaster.response;

import android.text.TextUtils;

import com.yun.yunmaster.model.IncomeExpensesItem;
import com.yun.yunmaster.network.base.response.BaseResponse;

import java.util.List;

/**
 * Created by jslsxu on 2017/9/19.
 */

public class IncomeExpensesResponse extends BaseResponse {

    public IncomeExpensesListData data;
    public static class IncomeExpensesListData{
        public List<IncomeExpensesItem> list;
        public String next;
        public boolean can_encash;

        public boolean hasMore(){
            return !TextUtils.isEmpty(next);
        }
    }

}
