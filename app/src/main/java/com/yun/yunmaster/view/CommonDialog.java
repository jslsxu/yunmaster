package com.yun.yunmaster.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.flyco.roundview.RoundViewDelegate;
import com.yun.yunmaster.R;

import java.util.List;


/**
 * Created by jslsxu on 2017/9/22.
 */

public class CommonDialog {

//    private static Dialog dialog = null;

    public static void showDialog(final Context context, String title, String message, final List<ActionItem> actionList) {
        if (actionList == null || actionList.size() == 0) {
            return;
        }
//        dismissCurrentDialog();
        View view = View.inflate(context, R.layout.layout_common_dialog, null);
        final Dialog dialog = showWithContentView(context, view);
        TextView titleView = (TextView) view.findViewById(R.id.tv_title);
        if (title == null || title.length() == 0) {
            title = "提示";
        }
        titleView.setText(title);

        TextView messageView = (TextView) view.findViewById(R.id.messageTextView);
        if (message == null || message.length() == 0) {
            messageView.setVisibility(View.GONE);
        } else {
            messageView.setVisibility(View.VISIBLE);
        }
        messageView.setText(message);

        GridView actionGridView = (GridView) view.findViewById(R.id.actionGridView);
        actionGridView.setNumColumns(actionList.size());
        actionGridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return actionList.size();
            }

            @Override
            public Object getItem(int position) {
                return actionList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                final ActionItem actionItem = actionList.get(position);
                View itemView = LayoutInflater.from(context).inflate(R.layout.action_item_view, parent, false);
                RoundTextView actionButton = (RoundTextView) itemView.findViewById(R.id.actionButton);
                actionButton.setText(actionItem.title);
                actionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (actionItem != null && actionItem.callBack != null) {
                            actionItem.callBack.onAction();
                        }
                        dialog.dismiss();
                    }
                });
                int textColor = R.color.white;
                int backgroundColor = R.color.color_blue;
                int backgroundPressedColor = R.color.color_blue_pressed;
                int borderColor = R.color.transparent;
                if (actionItem.actionType == ActionItem.ACTION_TYPE_CANCEL) {
                    backgroundColor = R.color.white;
                    textColor = R.color.color9;
                    borderColor = R.color.color9;
                    backgroundPressedColor = R.color.color_d;
                } else if (actionItem.actionType == ActionItem.ACTION_TYPE_DESTRUCTIVE) {
                    backgroundColor = R.color.white;
                    textColor = R.color.colorAlert;
                    borderColor = R.color.colorAlert;
                    backgroundPressedColor = R.color.color_d;
                }
                Resources resources = context.getResources();
                RoundViewDelegate delegate = actionButton.getDelegate();
                delegate.setBackgroundColor(resources.getColor(backgroundColor));
                delegate.setBackgroundPressColor(resources.getColor(backgroundPressedColor));
                delegate.setStrokeColor(resources.getColor(borderColor));
                actionButton.setTextColor(resources.getColor(textColor));
                return itemView;
            }
        });
        dialog.show();
    }

    public static Dialog showWithContentView(Context context, View contentView) {
        if (contentView != null) {
            Dialog dialogTmp = new Dialog(context);
            dialogTmp.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialogTmp.requestWindowFeature(Window.FEATURE_NO_TITLE);

            dialogTmp.setContentView(contentView);
            dialogTmp.getWindow().setGravity(Gravity.CENTER);
            WindowManager.LayoutParams params = dialogTmp.getWindow().getAttributes();
            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogTmp.getWindow().setAttributes(params);
            dialogTmp.setCancelable(false);
            dialogTmp.setCanceledOnTouchOutside(false);
            return dialogTmp;
        }
        return null;
    }

    public static void dismissCurrentDialog() {
//        if (dialog != null) {
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//                dialog = null;
//            }
//        }
    }

    public static class ActionItem {
        public static final int ACTION_TYPE_NORMAL = 0;
        public static final int ACTION_TYPE_CANCEL = 1;
        public static final int ACTION_TYPE_DESTRUCTIVE = 2;
        public int actionType;
        public String title;
        public ActionCallback callBack;

        public ActionItem(int type, String title, ActionCallback callBack) {
            super();
            this.actionType = type;
            this.title = title;
            this.callBack = callBack;
        }
    }

    public interface ActionCallback {
        void onAction();
    }
}
