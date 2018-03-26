package com.yun.yunmaster.base;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;
import com.yun.yunmaster.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class PublicLoadPage extends RelativeLayout {
    public static final int STATUS_NO_NETWORK = 0;
    public static final int STATUS_NO_DATA = 1;
    private int[] ImgFailType = {R.drawable.icon_no_network, R.drawable.icon_no_data};
    @BindView(R.id.loadingIndicator)
    AVLoadingIndicatorView mLoadingIndicator;
    @BindView(R.id.resultPage)
    View mResultPage;
    @BindView(R.id.statusImageView)
    ImageView mStatusImageView;
    @BindView(R.id.statusTextView)
    TextView mStatusTextView;
    @BindView(R.id.actionButton)
    Button mActionButton;
    private Context mContext;
    private int statusType;

    public PublicLoadPage(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PublicLoadPage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    public PublicLoadPage(Context context, AttributeSet attributeSet, int typeDef) {
        super(context, attributeSet, typeDef);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.load_page, this);
        ButterKnife.bind(this);
    }

    public int getStatusType() {
        return statusType;
    }

    public void setActionListener(View.OnClickListener clickListener) {
        if (clickListener != null && mActionButton != null) {
            mActionButton.setOnClickListener(clickListener);
        }
    }

    public void startLoad() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mResultPage.setVisibility(View.GONE);
    }

    public void loadSuccess() {
        setVisibility(GONE);
    }

    public void loadFail(int statusType, String statusTitle, int statusResId, String actionTitle) {
        setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.GONE);
        mResultPage.setVisibility(View.VISIBLE);
        this.statusType = statusType;
        if (!TextUtils.isEmpty(statusTitle)) {
            mStatusTextView.setText(statusTitle);
        }
        mStatusImageView.setImageDrawable(mContext.getResources().getDrawable(statusResId));
        if (TextUtils.isEmpty(actionTitle)) {
            mActionButton.setVisibility(View.GONE);
        } else {
            mActionButton.setVisibility(View.VISIBLE);
            mActionButton.setText(actionTitle);
        }

    }
}


