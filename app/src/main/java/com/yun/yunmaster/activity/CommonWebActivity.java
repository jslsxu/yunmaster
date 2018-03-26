package com.yun.yunmaster.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.webkit.JavascriptInterface;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.network.base.apis.HttpApiBase;
import com.yun.yunmaster.utils.CommonCallback;
import com.yun.yunmaster.utils.ParametersSorting;
import com.yun.yunmaster.utils.SystemUtils;
import com.yun.yunmaster.utils.UrlParamsUtil;
import com.yun.yunmaster.view.HProgressBarLoading;
import com.yun.yunmaster.view.ShareView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by jslsxu on 2018/3/25.
 */

public class CommonWebActivity extends BaseActivity implements View.OnClickListener {

    public static final String URL_KEY = "url";
    public static final String TITLE_KEY = "title";
    public static final String FROM_KEY = "from";

    @BindView(R.id.close_button)
    TextView close_button;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.rl_right)
    RelativeLayout rl_right;
    @BindView(R.id.progress_bar)
    HProgressBarLoading mProgressBar;
    @BindView(R.id.tv_center_badnet)
    TextView mTvCenterBadnet;
    @BindView(R.id.wb_view)
    WebView wb_view;

    private String title;
    private String url = "";
    private boolean needClearHistory = false;

    public static void intentTo(Context context, String url, String title) {
        Intent intent = new Intent(context, CommonWebActivity.class);
        intent.putExtra(URL_KEY, url);
        intent.putExtra(TITLE_KEY, title);

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_web);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        url = intent.getStringExtra(URL_KEY);
        title = intent.getStringExtra(TITLE_KEY);
        init();
        initData();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        tv_title.setText(title);
        wb_view = (WebView) findViewById(R.id.wb_view);
        rl_right.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initData() {
        WebSettings setting = wb_view.getSettings();
        // 设置支持javascript
        setting.setJavaScriptEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        wb_view.clearCache(true);
        wb_view.destroyDrawingCache();
        setting.setAppCacheMaxSize(1024 * 1024 * 8);
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        setting.setAppCachePath(appCachePath);
        setting.setAllowFileAccess(true);
        setting.setAppCacheEnabled(true);

        //启用数据库
        setting.setDatabaseEnabled(true);
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        //启用地理定位
        setting.setGeolocationEnabled(true);
        //设置定位的数据库路径
        setting.setGeolocationDatabasePath(dir);
        //最重要的方法，一定要设置，这就是出不来的主要原因
        setting.setDomStorageEnabled(true);
        //配置权限（同样在WebChromeClient中实现）
        //wb_view.requestFocus();
        wb_view.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                //如果没有网络直接跳出方法
                if (!SystemUtils.isNetworkAvailable(CommonWebActivity.this)) {
                    return;
                }
                //如果进度条隐藏则让它显示
                if (View.INVISIBLE == mProgressBar.getVisibility()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                mProgressBar.setCurProgress(newProgress, 200, null);
                if (newProgress >= 100) {
                    finishOperation(true);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissionsCallback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }


            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
        wb_view.setWebViewClient(new WebViewClient() {

            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                super.doUpdateVisitedHistory(view, url, isReload);
                if (needClearHistory) {
                    needClearHistory = false;
                    wb_view.clearHistory();//清除历史记录
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String paramsUrl = getParamsUrl(url);
                Timber.e("http55  :  " + paramsUrl);
                view.loadUrl(paramsUrl);
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            //错误页面的逻辑处理
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                finishOperation(false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (TextUtils.isEmpty(title)) {
                    tv_title.setText(view.getTitle());
                }
                if (wb_view.canGoBack()) {
                    close_button.setVisibility(View.VISIBLE);
                }
            }
        });
        wb_view.setDownloadListener(new MyWebViewDownLoadListener());
        wb_view.addJavascriptInterface(new ProductInterface(
                CommonWebActivity.this), "product");
        loadUrl();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        needClearHistory = true;
        loadUrl();
    }

    private void loadUrl() {
        Map<String, String> map = UrlParamsUtil.URLRequest(url);
        url = UrlParamsUtil.UrlPage(url) + "?" + ParametersSorting.createLinkString(map) + "&" + HttpApiBase.getCommonParamsUrl();
        wb_view.loadUrl(url);
    }


    private String getParamsUrl(String url) {
        if (url.contains("?")) {
            url += "&" + HttpApiBase.getCommonParamsUrl();
        } else {
            url += "?" + HttpApiBase.getCommonParamsUrl();
        }
        Map<String, String> map = UrlParamsUtil.URLRequest(url);

        url = UrlParamsUtil.UrlPage(url) + "?" + ParametersSorting.createLinkString(map);
        return url;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (wb_view.canGoBack()) {
                wb_view.goBack();
                return true;
            }

            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @OnClick({R.id.back_button, R.id.close_button, R.id.rl_right})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_button: {
                if (wb_view.canGoBack()) {
                    wb_view.goBack();
                    return;
                }
                finish();
            }
            break;

            case R.id.close_button:
                finish();
                break;
            case R.id.rl_right:

                break;
        }
    }

    /**
     * 结束进行的操作
     */
    private void finishOperation(boolean flag) {
        wb_view.setVisibility(flag ? View.VISIBLE : View.GONE);
        mTvCenterBadnet.setVisibility(flag ? View.INVISIBLE : View.VISIBLE);
        //点击重新连接网络
        mTvCenterBadnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvCenterBadnet.setVisibility(View.INVISIBLE);
                //重新加载网页
                wb_view.reload();
            }
        });
        hideProgressWithAnim();
    }

    /**
     * 隐藏加载对话框
     */
    private void hideProgressWithAnim() {
        AnimationSet animation = getDismissAnim(this);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mProgressBar.startAnimation(animation);
    }


    //进度条相关

    /**
     * 获取消失的动画
     *
     * @param context
     * @return
     */
    private AnimationSet getDismissAnim(Context context) {
        AnimationSet dismiss = new AnimationSet(context, null);
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
        alpha.setDuration(500);
        dismiss.addAnimation(alpha);
        return dismiss;
    }


    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    public class ProductInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        ProductInterface(Context Context) {
            mContext = Context;
        }


        @JavascriptInterface
        public void call(String phone) {

            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }

        @JavascriptInterface
        public void share(String title, String content, String image, String url) {
            Timber.e("share" + title + content);
            ShareView.showShareView(CommonWebActivity.this, title, content, url, new CommonCallback() {
                @Override
                public void onFinish(boolean success) {

                }

                @Override
                public void onCallback() {

                }
            });

        }
    }

}

