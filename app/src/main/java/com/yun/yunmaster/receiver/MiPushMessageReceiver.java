package com.yun.yunmaster.receiver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;
import com.yun.yunmaster.application.YunApplication;
import com.yun.yunmaster.utils.SystemUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class MiPushMessageReceiver extends PushMessageReceiver {

    private String mRegId;
    private String mTopic;
    private String mAlias;
    private String mAccount;
    private String mStartTime;
    private String mEndTime;

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Timber.e("onReceivePassThroughMessage is called. " + message.toString());

        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        }
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Timber.e("onNotificationMessageClicked is called. " + message.toString());
        if(SystemUtils.isAppAlive(context)){
            YunApplication.getApp().handleMessage(message, false);
        }
        else {
            Timber.e("启动应用。。。。。。。。。。。。。。。");
            Intent launchIntent = context.getPackageManager().
                    getLaunchIntentForPackage(context.getPackageName());
            launchIntent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(launchIntent);
            YunApplication.getApp().handleMessage(message, false);
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Timber.e("onNotificationMessageArrived is called. " + message.toString());
        if (SystemUtils.isAppAlive(context)) {
            YunApplication.getApp().handleMessage(message, true);
        }
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Timber.e("onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();


        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;


        if (MiPushClient.COMMAND_REGISTER.equals(command)) {

        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {

        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {

        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {

        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {

        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {

        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {

        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {

        } else {

        }
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Timber.e("onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);

        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;

            } else {

            }
        } else {

        }

    }

}

