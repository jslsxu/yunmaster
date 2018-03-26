package com.yun.yunmaster.network.base.presenter;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class RecyclerViewPresenter implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public static final int REQUEST_REFRESH = 0;    //刷新
    public static final int REQUEST_GETMORE = 1;    //加载更多
    protected RecyclerView mRecyclerView;
    protected SwipeRefreshLayout mRefreshLayout;
    protected PresenterInterface mPresenterInterface;
    protected BaseRecyclerAdapter mAdapter;
    private boolean mSupportPullUp;                 //是否支持上拉
    private boolean mLoading;
    private boolean mHasMore;

    public void bind(RecyclerView recyclerView,
                     BaseRecyclerAdapter recyclerAdapter,
                     SwipeRefreshLayout refreshLayout,
                     PresenterInterface ptrInterface,
                     boolean supportPullUp) {
        mRecyclerView = recyclerView;
        mAdapter = recyclerAdapter;
        mPresenterInterface = ptrInterface;
        mRefreshLayout = refreshLayout;
        if (mRefreshLayout != null) {
            mRefreshLayout.setOnRefreshListener(this);
            mRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_blue_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);
        }
        mSupportPullUp = supportPullUp;
        if (mSupportPullUp) {
            mAdapter.setOnLoadMoreListener(this);
        }
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void endRequest(int requestType) {
        mLoading = false;
        if (requestType == REQUEST_REFRESH) {
            if (mRefreshLayout != null) {
                mRefreshLayout.setRefreshing(false);
            }
        } else {
            mAdapter.loadMoreComplete();
        }
        mAdapter.setEnableLoadMore(mHasMore);
    }

    public void endRequest(int requestType, List data, boolean hasMore) {
        mHasMore = hasMore;
        mAdapter.setData(data, requestType == REQUEST_REFRESH);
        mAdapter.notifyDataSetChanged();
        endRequest(requestType);
    }

    @Override
    public void onRefresh() {
        if (mLoading) {
            mRefreshLayout.setRefreshing(false);
        } else {
            if (mPresenterInterface != null) {
                mLoading = true;
                mPresenterInterface.requestData(REQUEST_REFRESH);
            }
        }
    }

    @Override
    public void onLoadMoreRequested() {
        if (mLoading) {
            mAdapter.loadMoreEnd();
        } else {
            if (mPresenterInterface != null) {
                mLoading = true;
                mPresenterInterface.requestData(REQUEST_GETMORE);
            }
        }
    }

    public interface PresenterInterface {
        void requestData(int requestType);
    }
}


