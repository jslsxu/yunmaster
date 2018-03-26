package com.yun.yunmaster.view;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yun.yunmaster.R;
import com.yun.yunmaster.utils.CommonCallback;
import com.yun.yunmaster.utils.ResourceUtil;
import com.yun.yunmaster.utils.ShareManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jslsxu on 2017/12/9.
 */

public class ShareView extends RelativeLayout {

    @BindView(R.id.gridView)
    GridView gridView;
    private Context mContext;
    private ShareListener mListener;
    private int[] photoList = {R.drawable.wechat_session, R.drawable.wechat_timeline};

    public ShareView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ShareView(Context context, AttributeSet attributes) {
        super(context, attributes);
        mContext = context;
        init();
    }

    public void setShareListener(ShareListener listener){
        this.mListener = listener;
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        inflater.inflate(R.layout.layout_share, this);
        ButterKnife.bind(this);

        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.photo_item_cell, parent, false);
                }
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
                imageView.setBackgroundColor(ResourceUtil.getColor(mContext, R.color.transparent));
                imageView.setImageDrawable(ResourceUtil.getDrawable(mContext, photoList[position]));
                return convertView;
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mListener != null){
                    mListener.onShareItemClick(position);
                }
            }
        });
    }

    @OnClick({R.id.cancelButton})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.cancelButton:
                if(mListener != null){
                    mListener.onCancel();
                }
                break;
            default:
                break;
        }
    }

    public static void showShareView(final Context context, final String title, final String content, final String url, final CommonCallback callback){
        final Dialog dialog = new Dialog(context);
        ShareView shareView = new ShareView(context);
        shareView.setShareListener(new ShareListener() {
            @Override
            public void onShareItemClick(int position) {
                ShareManager.sharedInstance().share(position + 1, context, title, content, url, callback);
                dialog.dismiss();
            }

            @Override
            public void onCancel() {
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ViewGroup.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        shareView.setLayoutParams(layoutParams);
        dialog.setContentView(shareView);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setWindowAnimations(R.style.ActionSheetDialogAnimation);
        dialog.show();
    }

    public interface ShareListener{
        void onShareItemClick(int index);
        void onCancel();
    }
}

