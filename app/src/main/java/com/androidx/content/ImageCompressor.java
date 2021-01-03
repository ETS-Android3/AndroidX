package com.androidx.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;

/**
 * Author: Relin
 * Describe:Bitmap压缩器
 * Date:2020/11/21 21:06
 */
public class ImageCompressor extends Thread {

    public final static String TAG = ImageCompressor.class.getSimpleName();
    private Context context;
    private Bitmap bitmap;
    private String outPutPath;
    private int width;
    private int height;
    private int max = -1;
    private File file;
    private Bitmap.CompressFormat format;
    private CompressorHandler compressorHandler;
    private OnBitmapEncodeListener onBitmapEncodeListener;

    /**
     * 图片写入文件
     *
     * @param bitmap                 图片位图
     * @param outPutPath             图片输出路径
     * @param onBitmapEncodeListener 监听
     */
    public ImageCompressor(Context context, Bitmap bitmap, String outPutPath, OnBitmapEncodeListener onBitmapEncodeListener) {
        this.context = context;
        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.outPutPath = outPutPath;
        this.onBitmapEncodeListener = onBitmapEncodeListener;
        compressorHandler = new CompressorHandler();
    }

    @Override
    public void run() {
        super.run();
        long useTime = System.currentTimeMillis();
        File source = ImageProvider.decodeBitmap(context, bitmap);
        Bitmap bitmap = ImageProvider.decodeBounds(source.getAbsolutePath(), width, height);
        file = ImageProvider.compress(bitmap, Bitmap.CompressFormat.JPEG, max, outPutPath);
        compressorHandler.sendEmptyMessage(0);
        if (source.exists()) {
            source.delete();
        }
        useTime = System.currentTimeMillis() - useTime;
        Log.i(TAG, "->width = " + width + ",height = " + height + ",outPutPath = " + outPutPath + ",useTime = " + useTime + "ms");
    }


    /**
     * 设置压缩格式
     *
     * @param format 图片格式{@link Bitmap.CompressFormat#PNG}
     */
    public ImageCompressor format(Bitmap.CompressFormat format) {
        this.format = format;
        return this;
    }

    /**
     * 设置压缩格式
     *
     * @return 图片格式{@link Bitmap.CompressFormat#PNG}
     */
    public Bitmap.CompressFormat format() {
        return format;
    }

    public int width() {
        return width;
    }

    public ImageCompressor width(int width) {
        this.width = width;
        return this;
    }

    public int height() {
        return height;
    }

    public ImageCompressor height(int height) {
        this.height = height;
        return this;
    }

    public int max() {
        return max;
    }

    public ImageCompressor max(int max) {
        this.max = max;
        return this;
    }

    public interface OnBitmapEncodeListener {

        void onBitmapEncodeResult(ImageCompressor encoder, File file);

    }

    private class CompressorHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (onBitmapEncodeListener != null) {
                onBitmapEncodeListener.onBitmapEncodeResult(ImageCompressor.this, file);
            }
        }
    }

}
