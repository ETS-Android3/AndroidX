package com.androidx.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
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
    /**
     * 压缩文件夹名
     */
    public static final String DIR = ImageCompressor.class.getSimpleName();
    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 位图对象
     */
    private Bitmap bitmap;
    /**
     * 源文件
     */
    private File srcFile;
    /**
     * 输出路径
     */
    private String outPutPath;
    /**
     * 宽度
     */
    private int width = -1;
    /**
     * 高度
     */
    private int height = -1;
    /**
     * 压缩最大文件大小
     */
    private int max = -1;
    /**
     * 压缩结束文件
     */
    private File file;
    /**
     * 压缩格式
     */
    private Bitmap.CompressFormat format;
    /**
     * 压缩Handler
     */
    private CompressorHandler compressorHandler;
    /**
     * 压缩监听
     */
    private OnImageCompressListener onImageCompressListener;


    /**
     * 图片压缩
     *
     * @param context 上下文
     */
    public ImageCompressor(Context context) {
        this.context = context;
    }

    /**
     * 图片写入文件
     *
     * @param srcFile                 图片文件
     * @param outPutPath              图片输出路径
     * @param onImageCompressListener 监听
     */
    public ImageCompressor(Context context, File srcFile, String outPutPath, OnImageCompressListener onImageCompressListener) {
        this.context = context;
        this.srcFile = srcFile;
        this.outPutPath = outPutPath;
        this.onImageCompressListener = onImageCompressListener;
        compressorHandler = new CompressorHandler(Looper.getMainLooper());
    }

    /**
     * 图片写入文件
     *
     * @param bitmap                  图片位图
     * @param outPutPath              图片输出路径
     * @param onImageCompressListener 监听
     */
    public ImageCompressor(Context context, Bitmap bitmap, String outPutPath, OnImageCompressListener onImageCompressListener) {
        this.context = context;
        this.bitmap = bitmap;
        this.outPutPath = outPutPath;
        this.onImageCompressListener = onImageCompressListener;
        compressorHandler = new CompressorHandler(Looper.getMainLooper());
    }

    @Override
    public void run() {
        super.run();
        long useTime = System.currentTimeMillis();
        if (onImageCompressListener != null) {
            onImageCompressListener.onImageCompressStart(this);
        }
        File source = null;
        if (bitmap != null) {
            source = ImageProvider.decodeBitmap(context, bitmap);
        }
        if (srcFile != null) {
            bitmap = BitmapFactory.decodeFile(srcFile.getAbsolutePath());
            source = srcFile;
        }
        if (bitmap != null && width == -1 && height == -1) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }
        if (source != null) {
            Bitmap bitmap = ImageProvider.decodeBounds(source.getAbsolutePath(), width, height);
            file = ImageProvider.compress(bitmap, Bitmap.CompressFormat.JPEG, max, outPutPath);
            compressorHandler.sendEmptyMessage(0);
            if (source.exists()) {
                source.delete();
            }
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

    /**
     * 图片宽度
     * @return
     */
    public int width() {
        return width;
    }

    /**
     * 设置图片宽度
     * @param width
     * @return
     */
    public ImageCompressor width(int width) {
        this.width = width;
        return this;
    }

    /**
     * 图片高度
     * @return
     */
    public int height() {
        return height;
    }

    /**
     * 设置图片高度
     * @param height
     * @return
     */
    public ImageCompressor height(int height) {
        this.height = height;
        return this;
    }

    /**
     * 图片大小
     * @return
     */
    public int max() {
        return max;
    }

    /**
     * 设置图片大小
     * @param max
     * @return
     */
    public ImageCompressor max(int max) {
        this.max = max;
        return this;
    }

    /**
     * 压缩后的文件大小
     * @return
     */
    public File file() {
        return file;
    }

    /**
     * 设置输出文件路径
     * @param outPutPath
     * @return
     */
    public ImageCompressor outPutPath(String outPutPath) {
        this.outPutPath = outPutPath;
        return this;
    }

    /**
     * 输出文件路径
     * @return
     */
    public String outPutPath() {
        return outPutPath;
    }


    private class CompressorHandler extends Handler {

        public CompressorHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (onImageCompressListener != null) {
                onImageCompressListener.onImageCompressSucceed(ImageCompressor.this, file);
            }
        }
    }

}
