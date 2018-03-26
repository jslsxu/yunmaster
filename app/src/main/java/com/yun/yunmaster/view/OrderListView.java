package com.yun.yunmaster.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yun.yunmaster.R;
import com.yun.yunmaster.activity.OrderDetailActivity;
import com.yun.yunmaster.adapter.OrderListAdapter;
import com.yun.yunmaster.model.OrderItem;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.presenter.RecyclerViewPresenter;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.OrderApis;
import com.yun.yunmaster.response.OrderListResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2017/10/24.
 */

public class OrderListView extends RelativeLayout implements RecyclerViewPresenter.PresenterInterface {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    protected OrderListAdapter mAdapter;
    protected RecyclerViewPresenter mPresenter;
    protected String start = "";
    protected int type;
    protected Context mContext;
    @BindView(R.id.emptyView)
    LinearLayout emptyView;

    public OrderListView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public OrderListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    protected void init() {
        LayoutInflater.from(mContext).inflate(R.layout.order_list_view, this);
        ButterKnife.bind(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new OrderListAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mPresenter = new RecyclerViewPresenter();
        mPresenter.bind(mRecyclerView, mAdapter, mRefreshLayout, this, true);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OrderItem orderItem = mAdapter.getItem(position);
                OrderDetailActivity.intentTo(getContext(), orderItem.oid);
            }
        });
    }

    public void refresh() {
        requestData(RecyclerViewPresenter.REQUEST_REFRESH);
    }

    public void setType(int type) {
        this.type = type;
        refresh();
    }

    @Override
    public void requestData(final int requestType) {
        String from = requestType == RecyclerViewPresenter.REQUEST_REFRESH ? "" : start;
        OrderApis.getMyOrderList(type, from, new ResponseCallback<OrderListResponse>() {
            @Override
            public void onSuccess(OrderListResponse baseData) {
                start = baseData.data.next;
                List<OrderItem> list = baseData.data.list;
                mPresenter.endRequest(requestType, list, baseData.data.hasMore());
                emptyView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                mPresenter.endRequest(requestType);
                emptyView.setVisibility(mAdapter.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

}

