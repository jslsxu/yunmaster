package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.adapter.ViewPagerAdapter;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.ResourceUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class GuideActivity extends BaseActivity {
    @BindView(R.id.viewpager)
    ViewPager viewpager;

    public static void intentTo(Context context){
        if(AppSettingManager.needGuide()){
            Intent intent = new Intent(context, GuideActivity.class);
            context.startActivity(intent);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        init();
    }

    private void init(){
        ArrayList<View> viewList = new ArrayList<>();
        int[] pics = {R.drawable.guide1, R.drawable.guide2, R.drawable.guide3};
        for (int i = 0; i < pics.length; i++){
            ImageView imageView = new ImageView(this);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            );
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageDrawable(ResourceUtil.getDrawable(this, pics[i]));
            viewList.add(imageView);
        }
        viewpager.setAdapter(new ViewPagerAdapter(viewList));
        viewList.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GuideActivity.this.finish();
            }
        });

        AppSettingManager.updateGuide();
    }
}

