package com.yun.yunmaster.base;

import android.support.v4.app.Fragment;

import com.kaopiz.kprogresshud.KProgressHUD;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class BaseFragment extends Fragment {
    private KProgressHUD mProgress = null;

    public void startLoading() {
        if (mProgress == null) {
            mProgress = KProgressHUD.create(getActivity())
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
        }
        mProgress.show();
    }

    public void endLoading() {
        if (mProgress != null) {
            mProgress.dismiss();
        }
    }
}

