package com.yun.yunmaster.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yun.yunmaster.R;

/**
 * Created by jslsxu on 2017/12/9.
 */

public class ShareManager {
    public static final int WX_SHARE_SESSION = 1;
    public static final int WX_SHARE_TIMELINE = 2;
    private static ShareManager sharedManager = null;
    private CommonCallback mCallback;

    public static ShareManager sharedInstance() {
        synchronized (ShareManager.class) {
            if (sharedManager == null) {
                sharedManager = new ShareManager();
            }
        }
        return sharedManager;
    }
    public void share(int shareType, Context context, String title, String content, String url, CommonCallback callback){
        this.mCallback = callback;
        IWXAPI wxApi = WXAPIFactory.createWXAPI(context, Constants.WECHAT_APP_ID);
        wxApi.registerApp(Constants.WECHAT_APP_ID);

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = content;
        Bitmap thumb = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        msg.thumbData = ImageUtil.getByte(thumb);

        SendMessageToWX.Req request = new SendMessageToWX.Req();
        request.message = msg;
        request.scene = shareType == WX_SHARE_SESSION ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(request);
    }

    public void onWxShareResponse(BaseResp baseResp){
        if(baseResp.errCode == BaseResp.ErrCode.ERR_OK){
            if(mCallback != null){
                mCallback.onFinish(true);
            }
        }
        else{
            if(mCallback != null){
                mCallback.onFinish(false);
            }
        }
    }
}

