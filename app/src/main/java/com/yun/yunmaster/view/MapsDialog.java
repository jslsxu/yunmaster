package com.yun.yunmaster.view;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.route.BaiduMapRoutePlan;
import com.baidu.mapapi.utils.route.RouteParaOption;
import com.yun.yunmaster.R;
import com.yun.yunmaster.model.AppInfo;
import com.yun.yunmaster.model.Location;
import com.yun.yunmaster.utils.DisplayUtils;
import com.yun.yunmaster.utils.MyDistanceUtil;
import com.yun.yunmaster.utils.OpenMapAppUtils;

import java.util.LinkedList;
import java.util.List;


public class MapsDialog extends Dialog {
    private Context context;

    private TextView negativeButton, positiveButton;
    private TextView title;
    private LinearLayout lay_apps;

    private List<AppInfo> apps;
    private String msg = "选择您需要打开的应用";
    private String msg_default = "您的手机中没有安装地图导航工具，我们将打开浏览器进行web导航，我们任建议您下载百度或高德地图进行导航";
    private String positiveStr = "继续导航";
    private String negativeStr = "取消";
    private Location loc_now;
    private Location loc_end;
    /**
     * 默认的监听器
     */
    private View.OnClickListener deflistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MapsDialog.this.dismiss();
        }
    };
    /**
     * App图标点击监听，启动app进行导航
     */
    private View.OnClickListener applistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String pak = (String) v.getTag();
            switch (pak) {
                case "com.baidu.BaiduMap":
                    OpenMapAppUtils.startNative_Baidu(context, loc_now, loc_end);
                    break;
                case "com.autonavi.minimap":
                    OpenMapAppUtils.startNative_Gaode(context, loc_end);
                    break;
            }
            MapsDialog.this.dismiss();
        }
    };
    /**
     * 启动web进行导航
     */
    private View.OnClickListener weblistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RouteParaOption para = new RouteParaOption()
                    .startPoint(MyDistanceUtil.entity2Baidu(loc_now)).endPoint(MyDistanceUtil.entity2Baidu(loc_end));
            try {
                BaiduMapRoutePlan.openBaiduMapDrivingRoute(para, context);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MapsDialog.this.dismiss();
        }
    };

    public MapsDialog(Context context, LatLng stLatLng, LatLng enLatLng) {
        super(context, R.style.MapDialog);
        this.context = context;
        this.loc_now = new Location(stLatLng.latitude, stLatLng.longitude, "");
        this.loc_end = new Location(enLatLng.latitude, enLatLng.longitude, "");
        initApps();
        setMsgDialog();
    }

    private void initApps() {
        apps = OpenMapAppUtils.getMapApps(context);
        //只显示前5个应用
        if (apps != null && apps.size() > 5) {
            apps = apps.subList(0, 5);
        }
    }

    private void setMsgDialog() {
        View mView;
        if (apps != null && apps.size() != 0) {
            mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_native, null);

            title = (TextView) mView.findViewById(R.id.title);
            negativeButton = (TextView) mView.findViewById(R.id.negativeButton);
            if (title != null) title.setText(msg);
            if (negativeButton != null) negativeButton.setText(negativeStr);
            if (negativeButton != null) negativeButton.setOnClickListener(deflistener);

            LinkedList<TextView> views = new LinkedList<TextView>();
            lay_apps = (LinearLayout) mView.findViewById(R.id.lay_apps);
            lay_apps.setOrientation(LinearLayout.VERTICAL);
//    		for (AppInfo app : apps) {
            for (int i = 0; i < apps.size(); i++) {
                AppInfo app = apps.get(i);
                //定义左右边距15
                LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                para.setMargins(15, 0, 15, 0); // left,top,right, bottom
                para.gravity = Gravity.CENTER;

                TextView textView = new TextView(context);
                textView.setCompoundDrawablesWithIntrinsicBounds(null, app.getAppIcon(), null, null);    //设置图标
                textView.setText(app.getAppName());                                //设置文字
                textView.setTextAppearance(context, R.style.text_small_dark);    //设置风格
                textView.setLayoutParams(para);                                    //设置边距
                textView.setGravity(Gravity.CENTER_HORIZONTAL);                    //设置图标文字水平居中
                textView.setSingleLine(true);                                    //设置单行显示
                textView.setEllipsize(TruncateAt.END);                            //设置超出长度显示省略…
                textView.setMaxEms(6);                                            //设置最大长度
                textView.setTag(app.getPackageName());                            //设置包名为tag
                textView.setOnClickListener(applistener);                        //设置监听

                views.add(textView);

                if (views.size() == 3 || i == apps.size() - 1) {
                    LinearLayout.LayoutParams para_lay = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    para_lay.setMargins(0, 30, 0, 0); // left,top,right, bottom
                    para_lay.gravity = Gravity.CENTER;

                    LinearLayout row = new LinearLayout(context);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    row.setGravity(Gravity.CENTER_HORIZONTAL);
                    row.setLayoutParams(para_lay);
                    for (TextView view : views) {
                        row.addView(view);
                    }
                    lay_apps.addView(row);
                    views.clear();
                }
            }
        } else {
            mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_native_default, null);

            title = (TextView) mView.findViewById(R.id.title);
            positiveButton = (TextView) mView.findViewById(R.id.positiveButton);
            negativeButton = (TextView) mView.findViewById(R.id.negativeButton);
            if (title != null) title.setText(msg_default);
            if (positiveButton != null) positiveButton.setText(positiveStr);
            if (negativeButton != null) negativeButton.setText(negativeStr);
            if (positiveButton != null) positiveButton.setOnClickListener(weblistener);
            if (negativeButton != null) negativeButton.setOnClickListener(deflistener);
        }

        super.setContentView(mView);
    }

    @Override
    public void show() {
        super.show();
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        int screenWidth = DisplayUtils.screenWidth(context);
        /////////设置高宽
        lp.width = (int) (screenWidth * 0.85); // 宽度
        dialogWindow.setAttributes(lp);
    }


    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 取消键监听器
     *
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener) {
        negativeButton.setOnClickListener(listener);
    }
}
