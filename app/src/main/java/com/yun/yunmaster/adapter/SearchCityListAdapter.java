package com.yun.yunmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.model.CityInfo;

import java.util.List;

/**
 * Created by jslsxu on 2017/10/27.
 */

public class SearchCityListAdapter extends BaseAdapter {
    private List<CityInfo> cityList;

    public void setCityList(List<CityInfo> list){
        this.cityList = list;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        if (cityList != null) {
            return cityList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return cityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_cell, parent, false);
        }
        CityInfo cityInfo = cityList.get(position);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
        nameTextView.setText(cityInfo.name);
        return convertView;
    }
}

