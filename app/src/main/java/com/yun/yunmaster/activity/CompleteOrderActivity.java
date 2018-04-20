package com.yun.yunmaster.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.yun.yunmaster.R;
import com.yun.yunmaster.base.BaseActivity;
import com.yun.yunmaster.base.NavigationBar;
import com.yun.yunmaster.model.ImageUploadItem;
import com.yun.yunmaster.model.OrderDetail;
import com.yun.yunmaster.network.base.callback.ResponseCallback;
import com.yun.yunmaster.network.base.response.BaseResponse;
import com.yun.yunmaster.network.httpapis.OrderApis;
import com.yun.yunmaster.network.httpapis.UploadApis;
import com.yun.yunmaster.response.CalFeeResponse;
import com.yun.yunmaster.response.OrderDetailResponse;
import com.yun.yunmaster.response.UploadImageResponse;
import com.yun.yunmaster.utils.CameraUtils;
import com.yun.yunmaster.utils.Constants;
import com.yun.yunmaster.utils.ImageUtil;
import com.yun.yunmaster.utils.PhotoManager;
import com.yun.yunmaster.utils.ResourceUtil;
import com.yun.yunmaster.utils.ToastUtil;
import com.yun.yunmaster.view.CommonFeeView;
import com.yun.yunmaster.view.GridViewForScrollView;
import com.yun.yunmaster.view.NumOperationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CompleteOrderActivity extends BaseActivity {
    public static final String ORDER_KEY = "order";
    @BindView(R.id.navigationBar)
    NavigationBar navigationBar;
    @BindView(R.id.photoGridView)
    GridViewForScrollView photoGridView;
    @BindView(R.id.commitButton)
    RoundTextView commitButton;
    @BindView(R.id.numView)
    NumOperationView numView;
    @BindView(R.id.extraFeeTextView)
    EditText extraFeeTextView;
    @BindView(R.id.totalFeeTextView)
    TextView totalFeeTextView;

    private OrderDetail mOrderDetail;
    private ArrayList<ImageUploadItem> photoList = new ArrayList<>();
    private BaseAdapter photoAdapter;

    public static void intentTo(Activity context, OrderDetail orderDetail) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ORDER_KEY, orderDetail);
        Intent intent = new Intent(context, CompleteOrderActivity.class);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, Constants.COMPLETE_ORDER_REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mOrderDetail = (OrderDetail) getIntent().getSerializableExtra(ORDER_KEY);
        navigationBar.setLeftItem(R.drawable.icon_nav_back, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        photoAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return Math.min(photoList.size() + 1, 3);
            }

            @Override
            public Object getItem(int i) {
                if (i < photoList.size()) {
                    return photoList.get(i);
                } else {
                    return R.drawable.icon_list_tianjia_nor;
                }
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(final int i, View view, ViewGroup viewGroup) {
                View cell = view;
                Context context = CompleteOrderActivity.this;
                if (cell == null) {
                    cell = LayoutInflater.from(context).inflate(R.layout.image_upload_cell, viewGroup, false);
                }
                ImageButton deleteButton = (ImageButton) cell.findViewById(R.id.removeButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteImageAtIndex(i);
                    }
                });
                ImageView imageView = (ImageView) cell.findViewById(R.id.imageView);
                Drawable addDrawable = ResourceUtil.getDrawable(context, R.drawable.icon_list_tianjia_nor);
                if (i < photoList.size()) {
                    imageView.setImageBitmap(photoList.get(i).bitmap);
                    deleteButton.setVisibility(View.VISIBLE);
                } else {
                    imageView.setImageDrawable(addDrawable);
                    deleteButton.setVisibility(View.GONE);
                }
                return cell;
            }
        };
        photoGridView.setAdapter(photoAdapter);
        photoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == photoList.size()) {
                    PhotoManager.requestPhoto(CompleteOrderActivity.this, 3 - photoList.size());
                }
            }
        });

        numView.setNum(mOrderDetail.transport_times);
        numView.setNumChangeListener(new NumOperationView.NumChangeListener() {
            @Override
            public void numChanged(int num) {
                calFee();
            }
        });
        extraFeeTextView.setText(mOrderDetail.other_fee);
        extraFeeTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calFee();
            }
        });

        totalFeeTextView.setText(mOrderDetail.total_price);
    }

    private void deleteImageAtIndex(int i) {
        photoList.remove(i);
        photoAdapter.notifyDataSetChanged();
    }

    private void addImage(final Bitmap bitmap, String imagePath) {
        startLoading();
        UploadApis.uploadNormalImage(imagePath, new ResponseCallback<UploadImageResponse>() {
            @Override
            public void onSuccess(UploadImageResponse baseData) {
                endLoading();
                ImageUploadItem uploadItem = new ImageUploadItem(bitmap);
                uploadItem.full_path = baseData.data.img_url;
                photoList.add(uploadItem);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });
    }

    private void calFee() {
        int num = numView.getNum();
        String extra = extraFeeTextView.getText().toString();
        OrderApis.callFee(mOrderDetail.oid, num, extra, new ResponseCallback<CalFeeResponse>() {
            @Override
            public void onSuccess(CalFeeResponse baseData) {
                totalFeeTextView.setText(baseData.data.total);
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });
    }

    private void completeOrder() {
        if (photoList.size() == 0) {
            ToastUtil.showToast("请上传完成照片");
            return;
        }
        startLoading();
        String photos = null;
        for (int i = 0; i < photoList.size(); i++){
            ImageUploadItem item = photoList.get(i);
            if(i == 0){
                photos = item.full_path;
            }
            else{
                photos += "," + item.full_path;
            }
        }
        int num = numView.getNum();
        String extra = extraFeeTextView.getText().toString();
        OrderApis.completeOrder(mOrderDetail.oid, photos, num, extra, new ResponseCallback<OrderDetailResponse>() {
            @Override
            public void onSuccess(OrderDetailResponse baseData) {
                endLoading();
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(ORDER_KEY, baseData.data.order);
                intent.putExtras(bundle);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFail(int statusCode, @Nullable BaseResponse failDate, @Nullable Throwable error) {
                endLoading();
                ToastUtil.showToast(failDate.getErrmsg());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_CAPTURE) {
                String imagePath = PhotoManager.getImagePath();
                Bitmap bitmap = CameraUtils.getBitmapByPath(imagePath, 640, 960);
                if (bitmap != null) {
                    addImage(bitmap, imagePath);
                }
            } else if (requestCode == Constants.REQUEST_CODE_IMAGE) {
                List<String> pathList = data.getStringArrayListExtra("result");
                for (int i = 0; i < pathList.size(); i++) {
                    Bitmap bitmap = ImageUtil.compressImage(pathList.get(i), 2);
                    addImage(bitmap, pathList.get(i));
                }
            }
        }
    }

    @OnClick({R.id.commitButton})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commitButton:
                completeOrder();
                break;
        }
    }
}
