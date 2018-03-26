package com.yun.yunmaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.yun.yunmaster.R;
import com.yun.yunmaster.model.UserData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2017/9/22.
 */

public class HomeHeaderView extends RelativeLayout {

    @BindView(R.id.starBar)
    StarBar starBar;
    private Context mContext;

    public HomeHeaderView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public HomeHeaderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    public HomeHeaderView(Context context, AttributeSet attributeSet, int typeDef) {
        super(context, attributeSet, typeDef);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.home_header_view, this);
        ButterKnife.bind(this);

        starBar.setClickable(false);
    }

    public void updateWithUserData(UserData userData){
        starBar.setStarMark(userData.rate);
    }
}
