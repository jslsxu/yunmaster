package com.yun.yunmaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yun.yunmaster.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2017/10/29.
 */

public class NumOperationView extends LinearLayout {
    @BindView(R.id.button_minus)
    ImageView buttonMinus;
    @BindView(R.id.textView_num)
    TextView textViewNum;
    @BindView(R.id.button_add)
    ImageView buttonAdd;
    private Context mContext;
    private int mNum;
    private NumChangeListener mNumChangeListener;
    private int maxNum = 100;

    public NumOperationView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public NumOperationView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    public NumOperationView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.num_operation_view, this);
        ButterKnife.bind(this);
    }

    public void setNumChangeListener(NumChangeListener listener) {
        mNumChangeListener = listener;
    }

    public int getNum() {
        return mNum;
    }

    public void setNum(int num) {
        if (num > maxNum) {
            num = maxNum;
        } else if (num < 1) {
            num = 1;
        }
        if(mNum != num){
            mNum = num;
            String text = Integer.toString(mNum);
            textViewNum.setText(text);
            if(mNumChangeListener != null){
                mNumChangeListener.numChanged(mNum);
            }
            refreshStatus();
        }
    }

    private void refreshStatus(){
        buttonAdd.setEnabled(mNum < maxNum);
        buttonMinus.setEnabled(mNum > 1);
    }

    @OnClick({R.id.button_add, R.id.button_minus})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add:
                setNum(mNum + 1);
                break;
            case R.id.button_minus:
                setNum(mNum - 1);
                break;
        }
    }

    public interface NumChangeListener {
        void numChanged(int num);
    }
}

