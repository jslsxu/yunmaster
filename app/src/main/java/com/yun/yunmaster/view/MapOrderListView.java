package com.yun.yunmaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.yun.yunmaster.R;

import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2018/4/2.
 */

public class MapOrderListView extends RelativeLayout {
    private Context mContext;

    protected String start = "";
    public MapOrderListView(Context context){
        super(context);
        mContext = context;
        init();
    }
    public MapOrderListView(Context context, AttributeSet attributes){
        super(context, attributes);
        mContext = context;
        init();
    }

    private void init(){
        LayoutInflater.from(mContext).inflate(R.layout.map_order_list_view, this);
        ButterKnife.bind(this);

    }

}
