package com.yun.yunmaster.network.base.presenter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by jslsxu on 2018/3/24.
 */

public abstract class BaseRecyclerAdapter<T> extends BaseQuickAdapter<T, BaseViewHolder> {

    public BaseRecyclerAdapter(int layoutResID) {
        super(layoutResID, null);
    }

    public void clear() {
        setNewData(null);
    }

    public void setData(List<T> data, boolean isRefresh) {
        if (isRefresh) {
            setNewData(data);
        } else {
            if (data != null) {
                addData(data);
            }
        }
    }

    public boolean isEmpty(){
        return mData == null || mData.size() == 0;
    }

}

