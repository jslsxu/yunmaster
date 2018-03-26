package com.yun.yunmaster.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.yunmaster.R;
import com.yun.yunmaster.model.IncomeExpensesItem;
import com.yun.yunmaster.network.base.presenter.BaseRecyclerAdapter;


/**
 * Created by jslsxu on 2017/9/19.
 */

public class IncomeExpensesListAdapter extends BaseRecyclerAdapter<IncomeExpensesItem> {
    private Context mContext;

    public IncomeExpensesListAdapter(Context context) {
        super(R.layout.income_expenses_item);
        mContext = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, final IncomeExpensesItem item) {
        helper.setText(R.id.title, item.title);
        helper.setText(R.id.time, item.time);
        helper.setText(R.id.amount, item.amount);


    }

}