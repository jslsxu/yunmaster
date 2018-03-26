package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.adapter.IncomeExpensesListAdapter;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.IncomeExpensesItem;
import com.yun.yunmaster.model.UserData;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.presenter.RecyclerViewPresenter;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.IncomeExpensesResponse;
import com.yun.yunmaster.utils.AppSettingManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class IncomeExpensesDetailActivity extends BaseActivity implements RecyclerViewPresenter.PresenterInterface {

    protected IncomeExpensesListAdapter mAdapter;
    protected RecyclerViewPresenter mPresenter;
    protected String start = "";
    protected Context mContext;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.emptyTextView)
    TextView emptyTextView;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.actionView)
    RelativeLayout actionView;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, IncomeExpensesDetailActivity.class);
        context.startActivity(intent);
    }


    protected void init() {

        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new IncomeExpensesListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter = new RecyclerViewPresenter();
        mPresenter.bind(mRecyclerView, mAdapter, mRefreshLayout, this, true);

        requestData(RecyclerViewPresenter.REQUEST_REFRESH);

    }

    public void refresh() {
        requestData(RecyclerViewPresenter.REQUEST_REFRESH);
    }

    @Override
    public void requestData(final int requestType) {
        String from = requestType == RecyclerViewPresenter.REQUEST_REFRESH ? "" : start;
        CommonApis.getBalanceList(from, new ResponseCallback<IncomeExpensesResponse>() {
            @Override
            public void onSuccess(IncomeExpensesResponse baseData) {
                start = baseData.data.next;
                actionView.setVisibility(baseData.data.can_encash ? View.VISIBLE : View.GONE);
                List<IncomeExpensesItem> list = baseData.data.list;
                mPresenter.endRequest(requestType, list, baseData.data.hasMore());
                emptyTextView.setVisibility(isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                mPresenter.endRequest(requestType);
                emptyTextView.setVisibility(isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    public boolean isEmpty() {
        return mAdapter.getItemCount() == 0;
    }

    @OnClick(R.id.tv_submit)
    public void onViewClicked() {

        UserData userData = AppSettingManager.getUserData();
        if (userData != null && userData.balance != null) {
            WithdrawCashActivity.intentTo(IncomeExpensesDetailActivity.this);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_expenses_list);
        ButterKnife.bind(this);
        init();
    }
}

