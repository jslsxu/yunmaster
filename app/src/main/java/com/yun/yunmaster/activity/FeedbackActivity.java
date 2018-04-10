package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.flyco.roundview.RoundTextView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2018/4/8.
 */

public class FeedbackActivity extends BaseActivity {
    public static final int FEEDBACK_MAX = 200;
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.inputTextView)
    EditText inputTextView;
    @BindView(R.id.numTextView)
    TextView numTextView;
    @BindView(R.id.inputView)
    RoundRelativeLayout inputView;
    @BindView(R.id.commitButton)
    RoundTextView commitButton;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commit();
            }
        });

        numTextView.setText("0/200");
        inputTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                numTextView.setText(text.length() + "/200");
            }
        });
    }

    private void commit(){
        String text = inputTextView.getText().toString().trim();
        if(text.length() > 0){
            startLoading();
            CommonApis.feedback(text, new ResponseCallback<BaseResponse>() {
                @Override
                public void onSuccess(BaseResponse baseData) {
                    endLoading();
                    ToastUtil.showToast("提交反馈成功");
                    finish();
                }

                @Override
                public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                    endLoading();
                    ToastUtil.showToast(failDate.getErrmsg());
                }
            });
        }
        else {
            ToastUtil.showToast("请输入反馈内容");
        }
    }

}
