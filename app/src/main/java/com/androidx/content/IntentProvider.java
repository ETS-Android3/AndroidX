package com.androidx.content;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.File;

/**
 * Author: Relin
 * Describe:Action提供者
 * Date:2020/11/19 21:25
 */
public class IntentProvider {

    /**
     * 日志标识
     */
    public static String TAG = IntentProvider.class.getSimpleName();
    /**
     * 选择器
     */
    public final static int REQUEST_PICK = 10;
    /**
     * 拍照
     */
    public final static int REQUEST_CAPTURE = 11;
    /**
     * 剪切
     */
    public final static int REQUEST_CROP = 12;
    /**
     * 选择文件
     */
    public final static int REQUEST_GET_CONTENT = 13;

    /**
     * 打开选择器
     *
     * @param activity 页面
     * @param mineType 媒体类型 例如:image/*
     */
    public static void pick(Activity activity, String mineType) {
        if (activity == null) {
            Log.e(TAG, "->pick activity is null.");
            return;
        }
        if (mineType == null) {
            Log.e(TAG, "->pick mineType is null.");
            return;
        }
        activity.startActivityForResult(buildPickIntent(mineType), REQUEST_PICK);
    }

    /**
     * 打开选择器
     *
     * @param fragment 页面
     * @param mineType 媒体类型 例如:image/*
     */
    public static void pick(Fragment fragment, String mineType) {
        if (fragment == null) {
            Log.e(TAG, "->pick fragment is null.");
            return;
        }
        if (mineType == null) {
            Log.e(TAG, "->pick mineType is null.");
            return;
        }
        fragment.startActivityForResult(buildPickIntent(mineType), REQUEST_PICK);
    }

    /**
     * 构建选择器Intent
     *
     * @param mineType 媒体类型 例如:image/*
     * @return
     */
    public static Intent buildPickIntent(String mineType) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType(mineType);
        return intent;
    }

    /**
     * 系统拍照
     *
     * @param activity  页面
     * @param outPutUri 输出URI
     */
    public static void imageCapture(Activity activity, Uri outPutUri) {
        if (activity == null) {
            Log.e(TAG, "->imageCapture activity is null.");
            return;
        }
        if (outPutUri == null) {
            Log.e(TAG, "->imageCapture outPutUri is null.");
            return;
        }
        activity.startActivityForResult(buildImageCaptureIntent(outPutUri), REQUEST_CAPTURE);
    }

    /**
     * 系统拍照
     *
     * @param fragment  页面
     * @param outPutUri 输出URI
     */
    public static void imageCapture(Fragment fragment, Uri outPutUri) {
        if (fragment == null) {
            Log.e(TAG, "->imageCapture fragment is null.");
            return;
        }
        if (outPutUri == null) {
            Log.e(TAG, "->imageCapture outPutUri is null.");
            return;
        }
        fragment.startActivityForResult(buildImageCaptureIntent(outPutUri), REQUEST_CAPTURE);
    }

    /**
     * 构建系统拍照Intent
     *
     * @param outPutUri 图片输出URI
     * @return
     */
    public static Intent buildImageCaptureIntent(Uri outPutUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        return intent;
    }

    /**
     * 剪切图片
     *
     * @param activity 页面
     * @param options  参数
     */
    public static void crop(Activity activity, CropOptions options) {
        if (activity == null) {
            Log.e(TAG, "->crop activity is null.");
            return;
        }
        activity.startActivityForResult(buildCropIntent(options), REQUEST_CROP);
    }

    /**
     * 剪切图片
     *
     * @param fragment
     * @param options
     */
    public static void crop(Fragment fragment, CropOptions options) {
        if (fragment == null) {
            Log.e(TAG, "->crop fragment is null.");
            return;
        }
        fragment.startActivityForResult(buildCropIntent(options), REQUEST_CROP);
    }

    /**
     * 构建剪切图片Intent
     *
     * @param options 图片参数
     * @return
     */
    public static Intent buildCropIntent(@NonNull CropOptions options) {
        Log.i(TAG, "->buildCropIntent " + options.toString());
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(options.data(), "image/*");
        intent.putExtra("crop", String.valueOf(options.isCrop()));
        intent.putExtra("aspectX", options.aspectX());
        intent.putExtra("aspectY", options.aspectY());
        intent.putExtra("outputX", options.outputX());
        intent.putExtra("outputY", options.outputY());
        intent.putExtra("return-data", String.valueOf(options.isReturnData()));
        intent.putExtra("circleCrop", String.valueOf(options.isCircleCrop()));
        intent.putExtra("noFaceDetection", String.valueOf(options.isNoFaceDetection()));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, options.outPut());
        return intent;
    }

    /**
     * 打开文档
     *
     * @param activity 页面
     */
    public static void getDocument(Activity activity) {
        activity.startActivityForResult(buildGetDocumentIntent("*/*"), REQUEST_GET_CONTENT);
    }

    /**
     * 打开文档
     *
     * @param activity 页面
     * @param mintType 文件类型
     */
    public static void getDocument(Activity activity, String mintType) {
        activity.startActivityForResult(buildGetDocumentIntent(mintType), REQUEST_GET_CONTENT);
    }

    /**
     * 打开文档
     *
     * @param fragment 页面
     */
    public static void getDocument(Fragment fragment) {
        fragment.startActivityForResult(buildGetDocumentIntent("*/*"), REQUEST_GET_CONTENT);
    }

    /**
     * 打开文档
     *
     * @param fragment 页面
     * @param mimeType 文档类型
     */
    public static void getDocument(Fragment fragment, String mimeType) {
        fragment.startActivityForResult(buildGetDocumentIntent(mimeType), REQUEST_GET_CONTENT);
    }

    /**
     * 构建获取文件Intent
     *
     * @param mimeType 文件类型
     * @return
     */
    public static Intent buildGetDocumentIntent(String mimeType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType(mimeType);
        return intent;
    }

    /**
     * 打开文件
     *
     * @param context 上下文
     * @param path    文件路径
     */
    public static void openDocument(Context context, String path) {
        openDocument(context, path, IOProvider.getMimeType(path));
    }

    /**
     * 打开文件
     *
     * @param context 上下文
     * @param file    文件
     */
    public static void openDocument(Context context, File file) {
        String path = file.getAbsolutePath();
        openDocument(context, path, IOProvider.getMimeType(path));
    }

    /**
     * 打开文件
     *
     * @param context  上下文
     * @param path     文件路径
     * @param mimeType 文件类型
     */
    public static void openDocument(Context context, String path, String mimeType) {
        File file = new File(path);
        if (null == file || !file.exists()) {
            return;
        }
        openDocument(context, file, mimeType);
    }

    /**
     * 打开文件
     *
     * @param context  上下文
     * @param file     文件
     * @param mimeType 文件类型
     */
    public static void openDocument(Context context, File file, String mimeType) {
        Intent intent = buildOpenDocumentIntent(Uri.fromFile(file), mimeType);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建打开文档意图
     *
     * @param uri      文件Uri
     * @param mimeType 文件类型
     * @return
     */
    public static Intent buildOpenDocumentIntent(Uri uri, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, mimeType);
        return intent;
    }


}
