package com.yun.yunmaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.model.InfoItem;
import com.yun.yunmaster.utils.ResourceUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2017/9/20.
 */

public class CommonFeeView extends RelativeLayout {

    @BindView(R.id.fee_list_view)
    ListViewForScrollView fee_list_view;
    BaseAdapter mAdapter;
    private Context mContext;
    private List<InfoItem> feeList;

    public CommonFeeView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public CommonFeeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.fee_view, this);
        ButterKnife.bind(this);

        setClickable(false);
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (feeList != null) {
                    return feeList.size();
                }
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return feeList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View cell = convertView;
                if (convertView == null) {
                    cell = LayoutInflater.from(mContext).inflate(R.layout.fee_item_cell, parent, false);
                }
                TextView nameTextView = (TextView) cell.findViewById(R.id.name_textView);
                TextView valueTextView = (TextView) cell.findViewById(R.id.value_textView);
                InfoItem feeItem = feeList.get(position);
                nameTextView.setText(feeItem.key);
                valueTextView.setText(feeItem.value);
                valueTextView.setTextColor(ResourceUtil.getColor(mContext,R.color.color_blue));
                return cell;
            }
        };
        fee_list_view.setAdapter(mAdapter);
    }

    public void setFeeList(List<InfoItem> list) {
        feeList = list;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }
}

