package com.yun.yunmaster.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import timber.log.Timber;

/**
 * Created by jslsxu on 2018/3/24.
 */

public class ImageUtil {

    public static Bitmap compressImage(String imagePath, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        return bitmap;
    }

    public static Bitmap compressBitmapFromUrl(String url, double size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
        Bitmap bitmap = BitmapFactory.decodeFile(url, options);
        // 防止OOM发生
        options.inJustDecodeBounds = false;
        int mWidth = bitmap.getWidth();
        int mHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = 1;
//        try {
//            ExifInterface exif = new ExifInterface(url);
//            String model = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // 按照固定宽高进行缩放
        // 这里希望知道照片是横屏拍摄还是竖屏拍摄
        // 因为两种方式宽高不同，缩放效果就会不同
        // 这里用了比较笨的方式
        if (mWidth > mHeight) {
            scale = (float) size / mWidth;
        } else {
            scale = (float) size / mHeight;
        }
//        matrix.postRotate(90); /* 翻转90度 */
        // 按照固定大小对图片进行缩放
        matrix.postScale(scale, scale);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, mWidth, mHeight, matrix, true);
        // 用完了记得回收
        bitmap.recycle();
        return newBitmap;
    }
    public static Bitmap compressImageByQuality(String imagePath, int options) {
        File file = new File(imagePath);
        //     FileOutputStream fos;
        if (file.exists()) {
            Bitmap bm = BitmapFactory.decodeFile(imagePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 把压缩后的数据存放到baos中
            bm.compress(Bitmap.CompressFormat.JPEG, options, baos);

            try {
                FileOutputStream fos = new FileOutputStream(imagePath);
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
                Timber.i("save pic OK!");
                return bm;
            } catch (Exception e) {
                Timber.i("Exception");
                e.printStackTrace();
            }

//            try {
//                fos = new FileOutputStream(file);
//
//
//                bm.compress(Bitmap.CompressFormat.JPEG, options, fos);
//                Timber.i("save pic OK!");
//                return bm;
//            } catch (FileNotFoundException e) {
//                Timber.i("Exception");
//                e.printStackTrace();
//            }


        }
        return null;

    }
    public static void savePhotoLibrary(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        context.sendBroadcast(intent);//这
    }

    public static byte[] getByte(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();
        return datas;
    }
}

