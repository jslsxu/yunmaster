package com.yun.yunmaster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yun.yunmaster.R;
import com.yun.yunmaster.application.YunApplication;

import java.util.List;

/**
 * Created by jslsxu on 2017/9/28.
 */

public class PhotoListAdapter extends BaseAdapter {
    private List<String> photoList;
    private Context mContext;
    public PhotoListAdapter(Context context){
        super();
        mContext = context;
    }

    public PhotoListAdapter(Context context, List<String> list){
        super();
        mContext = context;
        setPhotoList(list);
    }

    public void setPhotoList(List<String> list){
        this.photoList = list;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        if(photoList != null){
            return photoList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.photo_item_cell, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        Glide.with(YunApplication.getApp()).load(photoList.get(position)).into(imageView);
        return convertView;
    }
}
