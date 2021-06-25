package com.androidx.content;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.fragment.app.Fragment;

import java.io.File;

/**
 * Author: Relin
 * Describe:文档选择器
 * Date:2020/11/21 18:44
 */
public class DocumentSelector {

    public final static String TAG = DocumentSelector.class.getSimpleName();
    /**
     * 图片模式
     */
    public final static int MODE_PICK_IMAGE = 1;
    /**
     * 拍照模式
     */
    public final static int MODE_IMAGE_CAPTURE = 2;
    /**
     * 拍照模式
     */
    public final static int MODE_IMAGE_CROP = 3;
    /**
     * 文件模式
     */
    public final static int MODE_GET_DOCUMENT = 4;
    /**
     * 构建者
     */
    public final Builder builder;
    /**
     * 上下文对象
     */
    public final Context context;
    /**
     * Activity页面
     */
    public final Activity activity;
    /**
     * Fragment页面
     */
    public final Fragment fragment;
    /**
     * 模式
     */
    public final int mode;
    /**
     * 文件类型
     */
    public final String mineType;
    /**
     * xml配置FileProvider authority
     */
    public final String authority;
    /**
     * 缓存文件夹名
     */
    public final String dictionary;
    /**
     * 输入Uri
     */
    public final Uri outPutUri;
    /**
     * 输出文件
     */
    public final File outPutFile;
    /**
     * 输出文件名称
     */
    public final String outPutName;
    /**
     * 剪切参数 - 是否剪切图片
     */
    public final boolean crop;
    /**
     * 剪切参数 - 比例X
     */
    public final int aspectX;
    /**
     * 剪切参数 - 剪切比例Y
     */
    public final int aspectY;
    /**
     * 剪切参数 - 输出图片宽度
     */
    public final int outputX;
    /**
     * 剪切参数 - 输出图片高度
     */
    public final int outputY;
    /**
     * 输出格式
     */
    public final String outputFormat;
    /**
     * 剪切参数 - 是否返回数据
     */
    public final boolean returnData;
    /**
     * 剪切参数 - 圆角剪切（部分机型支持）
     */
    public final boolean circleCrop;
    /**
     * 是否面部识别
     */
    public final boolean noFaceDetection;
    /**
     * 需要剪切图像Uri
     */
    public final Uri data;
    /**
     * 文件选择监听
     */
    public final OnDocumentSelectListener listener;

    /**
     * 结果Uri
     */
    private Uri resultUri;

    public DocumentSelector(Builder builder) {
        this.builder = builder;
        this.activity = builder.activity;
        this.fragment = builder.fragment;
        this.context = builder.context();
        this.mode = builder.mode;
        this.mineType = builder.mineType;
        this.authority = builder.authority;
        this.outPutUri = builder.outPutUri;
        this.outPutFile = builder.outPutFile;
        this.outPutName = builder.outPutName;
        this.dictionary = builder.dictionary;
        this.crop = builder.crop;
        this.aspectX = builder.aspectX;
        this.aspectY = builder.aspectY;
        this.outputX = builder.outputX;
        this.outputY = builder.outputY;
        this.outputFormat = builder.outputFormat;
        this.returnData = builder.returnData;
        this.circleCrop = builder.circleCrop;
        this.noFaceDetection = builder.noFaceDetection;
        this.data = builder.data;
        this.listener = builder.listener;
        build();
    }

    /**
     * 构建文件选择器
     */
    private void build() {
        if (mode == MODE_PICK_IMAGE) {
            if (activity != null) {
                IntentProvider.pick(activity, mineType);
            }
            if (fragment != null) {
                IntentProvider.pick(fragment, mineType);
            }
        }
        if (mode == MODE_IMAGE_CAPTURE) {
            if (activity != null) {
                IntentProvider.imageCapture(activity, outPutUri);
            }
            if (fragment != null) {
                IntentProvider.imageCapture(fragment, outPutUri);
            }
        }
        if (mode == MODE_IMAGE_CROP) {
            CropOptions options = new CropOptions();
            options.crop(crop);
            options.aspectX(aspectX);
            options.aspectY(aspectY);
            options.outputX(outputX);
            options.outputY(outputY);
            options.returnData(returnData);
            options.circleCrop(circleCrop);
            options.noFaceDetection(noFaceDetection);
            options.data(data);
            options.outPut(outPutUri);
            if (activity != null) {
                IntentProvider.crop(activity, options);
            }
            if (fragment != null) {
                IntentProvider.crop(fragment, options);
            }
        }
        if (mode == MODE_GET_DOCUMENT) {
            if (activity != null) {
                IntentProvider.getDocument(activity, mineType);
            }
            if (fragment != null) {
                IntentProvider.getDocument(fragment, mineType);
            }
        }
    }

    /**
     * 外部文档文件夹
     *
     * @return
     */
    public File getExternalDocumentDir() {
        return UriProvider.buildDir(context, dictionary);
    }

    /**
     * 外部文档文件夹大小
     *
     * @return
     */
    public long getExternalDocumentDirLength() {
        return IOProvider.length(getExternalDocumentDir());
    }

    /**
     * 删除外部文档文件夹
     */
    public void deleteExternalDocumentDir() {
        getExternalDocumentDir().delete();
    }

    /**
     * 删除文档
     *
     * @param uri 选择文档返回的Uri
     */
    public void deleteExternalDocumentUri(Uri uri) {
        UriProvider.delete(context, uri);
    }

    /**
     * 图片操作返回处理
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IntentProvider.REQUEST_CAPTURE) {
                String path = outPutFile.getAbsolutePath();
                Bitmap rotateBitmap = ImageProvider.rotate(path, ImageProvider.angle(path));
                ImageProvider.decodeBitmap(rotateBitmap, path);
                File file = new File(path);
                resultUri = UriProvider.insertMediaStoreImage(context, file);
                builder.data = outPutUri;
                Log.i(TAG, "->onActivityResult REQUEST_CAPTURE resultUri = " + resultUri + " , path = " + path);
                handleUriPath(resultUri, path);
            }
            if (requestCode == IntentProvider.REQUEST_CROP) {
                String path = outPutFile.getAbsolutePath();
                File file = new File(path);
                resultUri = UriProvider.insertMediaStoreImage(context, file);
                Log.i(TAG, "->onActivityResult REQUEST_CROP resultUri = " + resultUri + " , path = " + path);
                if (listener != null) {
                    listener.onDocumentSelect(this, resultUri, path);
                }
            }
            if (requestCode == IntentProvider.REQUEST_PICK) {
                resultUri = data.getData();
                builder.data = resultUri;
                String path = UriProvider.getData(context, resultUri);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                   path = UriProvider.insertExternalCacheDir(context, dictionary,resultUri);
                }
                Log.i(TAG, "->onActivityResult REQUEST_PICK resultUri = " + resultUri + " , path = " + path);
                handleUriPath(resultUri, path);
            }
            if (requestCode == IntentProvider.REQUEST_GET_CONTENT) {
                resultUri = data.getData();
                String path = UriProvider.getPath(context, resultUri);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                    path = UriProvider.insertExternalCacheDir(context, dictionary,resultUri);
                }
                Log.i(TAG, "->onActivityResult REQUEST_GET_CONTENT resultUri = " + resultUri + " , path = " + path);
                builder.data = UriProvider.buildProviderUri(context, new File(path), authority);
                handleUriPath(builder.data, path);
            }
        }
    }


    /**
     * 处理Uri和路径
     *
     * @param uri  Uri
     * @param path 路径
     */
    private void handleUriPath(Uri uri, String path) {
        if (IOProvider.isImage(path)) {
            if (crop) {
                builder.mode(DocumentSelector.MODE_IMAGE_CROP);
                builder.build();
            } else {
                if (listener != null) {
                    listener.onDocumentSelect(this, uri, path);
                }
            }
        } else {
            if (listener != null) {
                listener.onDocumentSelect(this, uri, path);
            }
        }
    }


    /**
     * 构建者
     */
    public static class Builder {
        /**
         * 上下文对象
         */
        private Context context;
        /**
         * Activity页面
         */
        private Activity activity;
        /**
         * Fragment页面
         */
        private Fragment fragment;
        /**
         * 模式
         */
        private int mode;
        /**
         * 文件类型
         */
        private String mineType;
        /**
         * xml配置FileProvider authority
         */
        private String authority;
        /**
         * 缓存文件夹名
         */
        private String dictionary = UriProvider.DIRECTORY_DOCUMENT;
        /**
         * 输入Uri
         */
        private Uri outPutUri;
        /**
         * 输出文件
         */
        private File outPutFile;
        /**
         * 输出文件名称
         */
        private String outPutName;
        /**
         * 剪切参数 - 是否剪切图片
         */
        private boolean crop = false;
        /**
         * 剪切参数 - 比例X
         */
        private int aspectX = 1;
        /**
         * 剪切参数 - 剪切比例Y
         */
        private int aspectY = 1;
        /**
         * 剪切参数 - 输出图片宽度
         */
        private int outputX = 1080;
        /**
         * 剪切参数 - 输出图片高度
         */
        private int outputY = 1080;
        /**
         * 剪切参数 - 输出图片高度
         */
        private String outputFormat = Bitmap.CompressFormat.JPEG.toString();
        /**
         * 剪切参数 - 是否返回数据
         */
        private boolean returnData = true;
        /**
         * 剪切参数 - 圆角剪切（部分机型支持）
         */
        private boolean circleCrop = false;
        /**
         * 是否面部识别
         */
        private boolean noFaceDetection = false;
        /**
         * 需要剪切图像Uri
         */
        private Uri data;
        /**
         * 文件选择监听
         */
        private OnDocumentSelectListener listener;


        public Builder(Activity activity) {
            context = activity;
            this.activity = activity;
        }

        public Builder(Fragment fragment) {
            if (fragment != null) {
                context = fragment.getContext();
            }
            this.fragment = fragment;
        }

        public Context context() {

            return context;
        }

        public Activity activity() {
            return activity;
        }

        public Fragment fragment() {
            return fragment;
        }

        public int mode() {
            return mode;
        }

        public Builder mode(int mode) {
            this.mode = mode;
            return this;
        }

        public String mineType() {
            return mineType;
        }

        public Builder mineType(String mineType) {
            this.mineType = mineType;
            return this;
        }

        public String authority() {
            return authority;
        }

        public Builder authority(String authority) {
            this.authority = authority;
            return this;
        }

        public String dictionary() {
            return dictionary;
        }

        public Builder dictionary(String dictionary) {
            this.dictionary = dictionary;
            return this;
        }

        public Uri outPutUri() {
            return outPutUri;
        }

        public Builder outPutUri(Uri outPutUri) {
            this.outPutUri = outPutUri;
            return this;
        }

        public File outPutFile() {
            return outPutFile;
        }

        public Builder outPutFile(File outPutFile) {
            this.outPutFile = outPutFile;
            return this;
        }

        public String outPutName() {
            return outPutName;
        }

        public Builder outPutName(String outPutName) {
            this.outPutName = outPutName;
            return this;
        }

        public boolean isCrop() {
            return crop;
        }

        public Builder crop(boolean crop) {
            this.crop = crop;
            return this;
        }

        public int aspectX() {
            return aspectX;
        }

        public Builder aspectX(int aspectX) {
            this.aspectX = aspectX;
            return this;
        }

        public int aspectY() {
            return aspectY;
        }

        public Builder aspectY(int aspectY) {
            this.aspectY = aspectY;
            return this;
        }

        public int outputX() {
            return outputX;
        }

        public Builder outputX(int outputX) {
            this.outputX = outputX;
            return this;
        }

        public int outputY() {
            return outputY;
        }

        public Builder outputY(int outputY) {
            this.outputY = outputY;
            return this;
        }

        public String outputFormat() {
            return outputFormat;
        }

        public Builder outputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        public boolean isReturnData() {
            return returnData;
        }

        public Builder returnData(boolean returnData) {
            this.returnData = returnData;
            return this;
        }

        public boolean isCircleCrop() {
            return circleCrop;
        }

        public Builder circleCrop(boolean circleCrop) {
            this.circleCrop = circleCrop;
            return this;
        }

        public boolean isNoFaceDetection() {
            return noFaceDetection;
        }

        public Builder noFaceDetection(boolean noFaceDetection) {
            this.noFaceDetection = noFaceDetection;
            return this;
        }

        public Uri data() {
            return data;
        }

        public Builder data(Uri data) {
            this.data = data;
            return this;
        }

        public OnDocumentSelectListener listener() {
            return listener;
        }

        public Builder listener(OnDocumentSelectListener listener) {
            this.listener = listener;
            return this;
        }

        public DocumentSelector build() {
            if (mineType == null) {
                if (mode == MODE_GET_DOCUMENT) {
                    mineType = "*/*";
                }
                if (mode == MODE_PICK_IMAGE) {
                    mineType = "image/*";
                }
                if (mode == MODE_IMAGE_CAPTURE || mode == MODE_IMAGE_CROP) {
                    mineType = "image/jpeg";
                }
            }
            if (authority == null || authority.length() == 0) {
                authority = context().getApplicationContext().getPackageName() + ".fileprovider";
            }
            if (outPutName == null || outPutName.length() == 0) {
                outPutName = UriProvider.buildImageName();
            }
            if (outPutFile == null) {
                outPutFile = UriProvider.buildFile(context(), dictionary, outPutName);
            }
            if (outPutUri == null && mode != MODE_IMAGE_CROP) {
                outPutUri = UriProvider.buildProviderUri(context(), outPutFile, authority);
            }
            if (outPutUri == null && mode == MODE_IMAGE_CROP) {
                String name = UriProvider.buildImageName();
                File file = UriProvider.buildFile(context(), dictionary, name);
                outPutUri = UriProvider.buildFileUri(file);
            }
            return new DocumentSelector(this);
        }

    }


}
