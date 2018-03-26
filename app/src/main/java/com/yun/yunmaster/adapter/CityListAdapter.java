package com.yun.yunmaster.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.model.CityInfo;
import com.yun.yunmaster.response.CityListResponse;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by jslsxu on 2017/10/25.
 */

public class CityListAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    private int selectedIndex = 0;
    private List<CityListResponse.CityGroup> cityList;

    public CityListAdapter(List<CityListResponse.CityGroup> list) {
        super();
        this.cityList = list;
    }

    public void updateList(List<CityListResponse.CityGroup> list) {
        this.cityList = list;
        selectedIndex = 0;
        notifyDataSetChanged();
    }

    public void select(int index) {
        this.selectedIndex = index;
        notifyDataSetChanged();
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public CityInfo cityAtIndex(int position){
        int num = 0;
        for (int i = 0; i < cityList.size(); i++){
            CityListResponse.CityGroup cityGroup = cityList.get(i);
            if(position >= i && position < num + cityGroup.cities.size()){
                return cityGroup.cities.get(position - num);
            }
            num += cityGroup.cities.size();
        }
        return null;
    }

    public int sectionForPosition(int position){
        int num = 0;
        for (int i = 0; i < cityList.size(); i++){
            CityListResponse.CityGroup cityGroup = cityList.get(i);
            if(position >= i && position < num + cityGroup.cities.size()){
                return i;
            }
            num += cityGroup.cities.size();
        }
        return 0;
    }

    public int getPositionForSection(String index){
        int num = 0;
        for (int i = 0; i < cityList.size(); i++){
            CityListResponse.CityGroup cityGroup = cityList.get(i);
            if(index.equals(cityGroup.getShortIndex())){
                return num;
            }
            num += cityGroup.cities.size();
        }
        return 0;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeadViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (HeadViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_header_view, null);
            viewHolder = new HeadViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        int section = sectionForPosition(position);
        //必须加""连接，否则会出现空指针异常，原因是setText方法传入整型参数会被解析成资源类型
        viewHolder.mHeadText.setText("" + cityList.get(section).index);
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        int section = sectionForPosition(position);
        return cityList.get(section).index.charAt(0);
    }

    @Override
    public int getCount() {
        if (cityList != null) {
            int total = 0;
            for (int i = 0; i < cityList.size(); i++){
                CityListResponse.CityGroup cityGroup = cityList.get(i);
                total += cityGroup.cities.size();
            }
            return total;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return cityAtIndex(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_cell, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        CityInfo cityInfo = cityAtIndex(position);
        viewHolder.nameTextView.setText(cityInfo.name);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.nameTextView)
        TextView nameTextView;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class HeadViewHolder {
        @BindView(R.id.headText)
        TextView mHeadText;

        public HeadViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

