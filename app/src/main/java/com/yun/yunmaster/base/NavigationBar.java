package com.yun.yunmaster.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yun.yunmaster.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class NavigationBar extends RelativeLayout {

    Context mContext;
    @BindView(R.id.left_button)
    ImageButton leftButton;
    @BindView(R.id.right_button)
    ImageButton rightButton;
    @BindView(R.id.title_view)
    TextView title_view;

    public NavigationBar(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public NavigationBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init(attributeSet);
    }

    public NavigationBar(Context context, AttributeSet attributeSet, int typeDef) {
        super(context, attributeSet, typeDef);
        mContext = context;
        init(attributeSet);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(mContext).inflate(R.layout.navigation_bar, this);
        ButterKnife.bind(this);

        if (attrs != null) {
            TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.NavigationBar);
            if (typedArray != null) {
                String title = typedArray.getString(R.styleable.NavigationBar_title);
                setTitle(title);
                typedArray.recycle();
            }
        }
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            this.title_view.setText(title);
        }
    }

    public void setLeftItem(int resId, final View.OnClickListener clickListener) {
        this.leftButton.setVisibility(View.VISIBLE);
        this.leftButton.setImageDrawable(mContext.getResources().getDrawable(resId));
        if (clickListener != null) {
            this.leftButton.setOnClickListener(clickListener);
        }
    }

    public void setRightItem(int resId, final View.OnClickListener clickListener) {
        this.rightButton.setVisibility(View.VISIBLE);
        this.rightButton.setImageDrawable(mContext.getResources().getDrawable(resId));
        if (clickListener != null) {
            this.rightButton.setOnClickListener(clickListener);
        }
    }

}


