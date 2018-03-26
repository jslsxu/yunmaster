package com.yun.yunmaster.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yun.yunmaster.R;
import com.yun.yunmaster.model.InfoItem;
import com.yun.yunmaster.model.OrderDetail;
import com.yun.yunmaster.utils.ResourceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jslsxu on 2017/9/20.
 */

public class OrderInfoView extends RelativeLayout {
    @BindView(R.id.info_list_view)
    ListViewForScrollView info_list_view;
    BaseAdapter mAdapter;
    private Context mContext;
    private OrderDetail mOrderDetail;
    private List<InfoItem> infoList;

    public OrderInfoView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public OrderInfoView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.order_info_view, this);
        ButterKnife.bind(this);

        setClickable(false);
        mAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (infoList != null) {
                    return infoList.size();
                }
                return 0;
            }

            @Override
            public Object getItem(int position) {
                return infoList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View cell = convertView;
                if (convertView == null) {
                    cell = LayoutInflater.from(mContext).inflate(R.layout.order_info_cell, parent, false);
                }
                TextView nameTextView = (TextView) cell.findViewById(R.id.name_textView);
                TextView valueTextView = (TextView) cell.findViewById(R.id.value_textView);
                final InfoItem infoItem = infoList.get(position);
                nameTextView.setText(infoItem.key);
                valueTextView.setText(infoItem.value);
                valueTextView.setTextColor(ResourceUtil.getColor(mContext, R.color.color6));

                if (infoItem.isAddress) {
                    Drawable icon = ResourceUtil.getDrawable(mContext, R.drawable.icon_list_weizhi_nor);
                    icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
                    valueTextView.setCompoundDrawables(null, null, icon, null);
                } else if (infoItem.isDestination) {
                    Drawable icon = ResourceUtil.getDrawable(mContext, R.drawable.icon_list_shangbao_nor);
                    icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
                    valueTextView.setCompoundDrawables(null, null, icon, null);
                } else if (infoItem.isMobile) {
                    Drawable icon = ResourceUtil.getDrawable(mContext, R.drawable.icon_list_bodadianhua_nav);
                    icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
                    valueTextView.setCompoundDrawables(null, null, icon, null);
                } else {
                    valueTextView.setCompoundDrawables(null, null, null, null);
                }
                valueTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (infoItem.isAddress) {
//                            AddressInfo address = mOrderDetail.detail_address;
//                            BaiduRoutePlanActivity.intentTo(mContext, address.address, address.lat, address.lng);
                        } else if (infoItem.isDestination) {
//                            final OrderItem.WasteYard yardInfo = mOrderDetail.waste_yard;
//                            String[] photoSource = {"查看路线", "报错"};
//                            new ActionSheetDialog(mContext)
//                                    .builder()
//                                    .setCancelable(false)
//                                    .setCanceledOnTouchOutside(true)
//                                    .setSheetItems(Arrays.asList(photoSource), ActionSheetDialog.SheetItemColor.Blue,
//                                            new ActionSheetDialog.OnSheetItemClickListener() {
//                                                @Override
//                                                public void onClick(int which) {
//                                                    if (which == 1) {
//                                                        BaiduRoutePlanActivity.intentTo(mContext, yardInfo.name, yardInfo.lat, yardInfo.lng);
//                                                    } else if (which == 2) {
//                                                        ReportYardInfoActivity.intentTo(mContext, mOrderDetail.waste_yard);
//                                                    }
//                                                }
//                                            })
//                                    .show();
                        } else if (infoItem.isMobile) {
                            CommonDialog.ActionItem cancelItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_CANCEL, "取消", null);
                            CommonDialog.ActionItem confirmItem = new CommonDialog.ActionItem(CommonDialog.ActionItem.ACTION_TYPE_NORMAL, "确定", new CommonDialog.ActionCallback() {
                                @Override
                                public void onAction() {
                                    Utils.call(mContext, infoItem.value);
                                }
                            });
                            ArrayList<CommonDialog.ActionItem> actionList = new ArrayList<>();
                            actionList.add(cancelItem);
                            actionList.add(confirmItem);
                            CommonDialog.showDialog(mContext, "提示", "确定拨打电话" + infoItem.value + "吗?", actionList);
                        }
                    }
                });
                return cell;
            }
        };
        info_list_view.setAdapter(mAdapter);
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.mOrderDetail = orderDetail;
        this.infoList = this.mOrderDetail.orderInfoList();
        mAdapter.notifyDataSetChanged();
    }

}
