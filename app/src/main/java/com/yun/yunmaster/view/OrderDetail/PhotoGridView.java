package com.yun.yunmaster.view.OrderDetail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.yun.yunmaster.R;
import com.yun.yunmaster.application.YunApplication;
import com.yun.yunmaster.view.GridViewForScrollView;
import com.yun.yunmaster.view.PhotoBrowserView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2017/10/24.
 */

public class PhotoGridView extends RelativeLayout {

    @BindView(R.id.gridView)
    GridViewForScrollView gridView;
    private Context mContext;
    private List<String> photoList;
    private BaseAdapter mAdapter;

    public PhotoGridView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PhotoGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.photo_grid_view, this);
        ButterKnife.bind(this);
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (photoList != null) {
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
        };
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoBrowserView.showPhotoBrowser(mContext, photoList, position);
            }
        });
    }

    public void setPhotoList(List<String> list) {
        this.photoList = list;
        mAdapter.notifyDataSetChanged();
    }
}
