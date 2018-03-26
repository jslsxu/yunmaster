package com.yun.yunmaster.utils;

import android.os.CountDownTimer;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class SendSmsTimeCount extends CountDownTimer {

    private OnTimeCountListener mOnTimeCountListener;

    public SendSmsTimeCount(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
    }

    public void setOnTimeCountListener(OnTimeCountListener listener) {
        this.mOnTimeCountListener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        if (mOnTimeCountListener != null) {
            mOnTimeCountListener.onTick(millisUntilFinished);
        }
    }

    @Override
    public void onFinish() {

        if (mOnTimeCountListener != null) {
            mOnTimeCountListener.onFinish();
        }
    }

    public interface OnTimeCountListener {

        void onTick(long millisUntilFinished);

        void onFinish();
    }
}
