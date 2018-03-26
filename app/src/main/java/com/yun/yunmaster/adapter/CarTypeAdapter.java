package com.yun.yunmaster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.response.PublicParamsResponse;

import java.util.List;

public class CarTypeAdapter extends BaseAdapter {

    private List<PublicParamsResponse.DataBean.CarTypeListBean> list;
    private LayoutInflater inflater = null;
    private int selectedPos =-1;
    private Context context;

    public CarTypeAdapter(Context context,int selectedPos ) {
        this.context = context;
        this.selectedPos = selectedPos;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<PublicParamsResponse.DataBean.CarTypeListBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_car_type,
                    parent, false);
            holder = new ViewHolder();
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        String name = list.get(position).getName();

        if (position == selectedPos) {
            holder.iv_select.setImageResource(R.drawable.icon_nav_check_selected);
        } else {
            holder.iv_select.setImageResource(R.drawable.icon_nav_check_default);
        }
        holder.tv_name.setText(name);

        return convertView;
    }


    public static class ViewHolder {
        TextView tv_name;
        ImageView iv_select;
    }
}
