package com.yun.yunmaster.application;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;

import com.baidu.mapapi.SDKInitializer;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.robin.lazy.cache.CacheLoaderManager;
import com.robin.lazy.cache.disk.naming.HashCodeFileNameGenerator;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.yun.yunmaster.BuildConfig;
import com.yun.yunmaster.network.httpapis.CommonApis;
import com.yun.yunmaster.utils.AppSettingManager;
import com.yun.yunmaster.utils.LoginManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class YunApplication extends Application {
    private static YunApplication fakeApp;
    private Handler mHandler;
    private PushHandler pushHandler;

    public static YunApplication getApp() {
        return fakeApp;
    }

    public static String mipushregid = "";
    private static final String Mipush_APP_ID = "2882303761517620333";
    private static final String Mipush_APP_KEY = "5661762013333";

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        if (BuildConfig.DEBUG) {
            MobclickAgent.setDebugMode(true);
            Timber.plant(new Timber.DebugTree());
        }
        fakeApp = this;
        mHandler = new Handler(Looper.getMainLooper());

        //缓存
        CacheLoaderManager.getInstance().init(this, new HashCodeFileNameGenerator(), 1024 * 1024 * 16, 50, 20);

        //OKhttp
        ClearableCookieJar cookieJar1 = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        //Baidu地图初始化
        SDKInitializer.initialize(this);

        //    CookieJarImpl cookieJar1 = new CookieJarImpl(new MemoryCookieStore());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
                .cookieJar(cookieJar1)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Timber.d(" onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {

            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
        registerMiPush();
    }

    public void runOnUIThread(Runnable action) {
        mHandler.post(action);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    private void registerMiPush() {
        if (shouldInit()) {
            MiPushClient.registerPush(this, Mipush_APP_ID, Mipush_APP_KEY);
            mipushregid = MiPushClient.getRegId(getApplicationContext());
            Timber.d("mipushregid  " + mipushregid);
        }
        if (pushHandler == null) {
            pushHandler = new PushHandler();
        }
        saveRegID();
    }

    public void saveRegID() {
        if (TextUtils.isEmpty(mipushregid)) {
            mipushregid = MiPushClient.getRegId(getApplicationContext());
        }
        Timber.e("pushID is " + mipushregid);
        if (LoginManager.isLogin()) {
            CommonApis.saveRegid(mipushregid);
        }
    }

    public void handleMessage(MiPushMessage message, boolean arrived) {
        if (LoginManager.isLogin()) {
            Message msg = Message.obtain();
            msg.obj = message.getContent();
            msg.what = arrived ? 1 : 0;
            pushHandler.sendMessage(msg);
        }
    }

    public static class PushHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (LoginManager.isLogin()) {
                String content = (String) msg.obj;
                try {
                    Timber.e(content);
                    JSONObject jsonObject = new JSONObject(content);
                    int type = Integer.parseInt(jsonObject.getString("msgtype"));
                    final Activity topActivity = com.yun.yunmaster.utils.ActivityManager.getInstance().currentActivity();
//                    if (type == 1) {
//                        String data = jsonObject.getString("data");
//                        OrderItem orderItem = GsonManager.getGson().fromJson(data, OrderItem.class);
//                        if(msg.what == 1){
//                            PushOrderInfoView.presentPushOrder(topActivity, orderItem);
//                        }
//                        else {
//                            OrderDetailActivity.intentTo(topActivity, orderItem.oid);
//                        }
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}