package com.yun.yunmaster.model;

import android.graphics.Bitmap;

/**
 * Created by jslsxu on 2017/10/25.
 */

public class ImageUploadItem {
    public Bitmap bitmap;
    public String full_path;
    public String short_path;
    public ImageUploadItem(Bitmap bitmap){
        this.bitmap = bitmap;
    }
}
