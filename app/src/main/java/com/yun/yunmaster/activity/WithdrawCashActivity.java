package com.yun.yunmaster.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.response.WithdrawCashLimitResponse;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.ActionSheetDialog;
import com.yun.yunmaster.view.ClearEditText;

import java.math.BigDecimal;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class WithdrawCashActivity extends BaseActivity {
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.et_name)
    ClearEditText etName;
    @BindView(R.id.et_card_no)
    ClearEditText etCardNo;
    @BindView(R.id.et_amount)
    ClearEditText etAmount;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.bankNameTextView)
    TextView bankNameTextView;
    @BindView(R.id.backNameView)
    RelativeLayout backNameView;

    private String limit = null;

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, WithdrawCashActivity.class);
        context.startActivity(intent);
    }

    private void init() {
        getCashLimit();
        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        etAmount.setText(s);
                        etAmount.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    etAmount.setText(s);
                    etAmount.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        etAmount.setText(s.subSequence(0, 1));
                        etAmount.setSelection(1);
                        return;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private String getErrorMsg() {
        String errorMsg = null;
        if (TextUtils.isEmpty(etName.getText().toString().trim())) {
            errorMsg = "请输入持卡人姓名";
        } else if (TextUtils.isEmpty(etCardNo.getText().toString().trim())) {
            errorMsg = "请输入银行卡号";
        } else if (TextUtils.isEmpty(etAmount.getText().toString().trim())) {
            errorMsg = "请输入提现金额";
        } else if (limit != null && compareTo(Double.valueOf(limit.trim()), Double.valueOf(etAmount.getText().toString().trim())) < 0) {
            errorMsg = "提现金额超限";
        }
        if (errorMsg != null) ToastUtil.showToast(errorMsg);
        return errorMsg;
    }

    public int compareTo(double d1, double d2) {
        BigDecimal num1 = new BigDecimal(d1);
        BigDecimal num2 = new BigDecimal(d2);
        return num1.compareTo(num2);
    }

    @OnClick({R.id.backNameView, R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backNameView:
                showBankList();
                break;
            case R.id.tv_submit:
                if (getErrorMsg() == null) requestWithdrawCash();
                break;
        }
    }

    private void showBankList(){
        final String[] bankList = getResources().getStringArray(R.array.bank_list);
        new ActionSheetDialog(this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true)
                .setSheetItems(Arrays.asList(bankList), ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                                bankNameTextView.setText(bankList[which - 1]);
                            }
                        })
                .show();
    }

    public void getCashLimit() {
        startLoading();
        CommonApis.withdrawCashLimit(new ResponseCallback<WithdrawCashLimitResponse>() {
            @Override
            public void onSuccess(WithdrawCashLimitResponse baseData) {
                endLoading();
                if (baseData.data != null && baseData.data.limit != null) {
                    limit = baseData.data.limit;
                    tvTips.setVisibility(View.VISIBLE);
                    tvTips.setText("本次可提现上限为" + limit + "元");
                }

            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
            }
        });
    }

    public void requestWithdrawCash() {

        startLoading();
        String bankName = bankNameTextView.getText().toString();
        CommonApis.commonWithdrawCash(bankName, etName.getText().toString().trim(), etCardNo.getText().toString().trim(), etAmount.getText().toString().trim(), new ResponseCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse baseData) {
                endLoading();
                ToastUtil.showLongToast("提现申请提交成功");
                finish();
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                ToastUtil.showToast(failDate.getErrmsg());
                endLoading();
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_cash);
        ButterKnife.bind(this);
        init();
    }
}

