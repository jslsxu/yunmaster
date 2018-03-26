package com.yun.yunmaster.view;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.adapter.CarTypeAdapter;
import com.yun.yunmaster.response.PublicParamsResponse;

import java.util.List;

public abstract class CarTypeView {


    public CarTypeView show(Context context, List<PublicParamsResponse.DataBean.CarTypeListBean> carTypeList, int carTypePos) {

        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        View view = View.inflate(context, R.layout.car_type_view, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_title.setText("选择车型");
        ListView lv = (ListView) view
                .findViewById(R.id.lv_date);
        CarTypeAdapter carTypeAdapter = new CarTypeAdapter(context, carTypePos);
        carTypeAdapter.setData(carTypeList);
        lv.setAdapter(carTypeAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, final int pos,
                                    long arg3) {
                onItemSelect(pos);
                dialog.dismiss();
            }
        });


        dialog.setView(view, 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        return this;
    }

    public abstract void onItemSelect(int pos);


}
