package com.androidx.net;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.androidx.app.CoreApplication;
import com.androidx.content.ImageProvider;

import java.io.File;
import java.util.TreeMap;

/**
 * Created by Relin
 * on 2017/3/13.
 */

public class RequestParams {

    /**
     * 文件参数
     */
    private TreeMap<String, File> files;
    /**
     * 文字参数
     */
    private TreeMap<String, String> params;
    /**
     * Header参数
     */
    private TreeMap<String, String> header;
    /**
     * 字符串参数
     */
    private String body;
    /**
     * 标识
     */
    private String tag;

    public RequestParams() {

    }

    /**
     * 添加文字参数
     * Add String Params
     *
     * @param key
     * @param value
     */
    public void add(String key, String value) {
        if (params == null) {
            params = new TreeMap<>();
        }
        params.put(key, value == null ? "" : value);
    }

    /**
     * 添加文件参数
     * Add File Params
     *
     * @param key
     * @param value
     * @param max   压缩大小,kb为单位
     */
    public void add(String key, File value, int max) {
        if (value == null) {
            return;
        }
        if (files == null) {
            files = new TreeMap<>();
        }
        if (!value.exists()) {
            Log.e(this.getClass().getSimpleName(), "addParams file is not exist!" + value.getAbsolutePath());
        }
        //压缩图片
        String path = value.getAbsolutePath();
        if (!TextUtils.isEmpty(path)) {
            path = path.toUpperCase();
        }
        Log.e(this.getClass().getSimpleName(), "addParams value:" + path);
        Bitmap bitmap = null;
        if (ImageProvider.isImage(path)) {
            bitmap = ImageProvider.decodeBounds(path, max);
        }
        if (path.contains(".JPG") || path.contains(".JPEG")) {
            value = ImageProvider.decodeBitmap(CoreApplication.app, bitmap, Bitmap.CompressFormat.JPEG, max);
        }
        if (path.contains(".PNG")) {
            value = ImageProvider.decodeBitmap(CoreApplication.app, bitmap, Bitmap.CompressFormat.PNG, max);
        }
        files.put(key, value);
    }

    /**
     * 添加文件参数
     * Add File Params
     *
     * @param key
     * @param value
     */
    public void add(String key, File value) {
        add(key, value, 1024);
    }

    /**
     * 添加头文件参数
     * Add Header Params
     *
     * @param key
     * @param value
     */
    public void addHeader(String key, String value) {
        if (header == null) {
            header = new TreeMap<>();
        }
        if (value == null) {
            return;
        }
        header.put(key, value);
    }

    /**
     * 获取文字参数
     *
     * @return
     */
    public TreeMap<String, String> params() {
        return params;
    }

    /**
     * 获取文件参数
     *
     * @return
     */
    public TreeMap<String, File> files() {
        return files;
    }

    /**
     * 获取头文件参数
     *
     * @return
     */
    public TreeMap<String, String> header() {
        if (header == null) {
            header = new TreeMap<>();
            header.put(Header.USER_AGENT, "Android");
            header.put(Header.CONTENT_TYPE, Header.CONTENT_JSON);
        }
        return header;
    }

    /**
     * 添加字符串实例
     *
     * @param body
     */
    public void addBody(String body) {
        this.body = body;
    }

    /**
     * 返回字符串的Body实例
     *
     * @return
     */
    public String body() {
        return body;
    }

    /**
     * 获取标识
     *
     * @return
     */
    public String tag() {
        if (tag == null) {
            tag = String.valueOf(System.currentTimeMillis());
        }
        return tag;
    }

    /**
     * 添加标识
     *
     * @param tag
     */
    public void tag(String tag) {
        this.tag = tag;
    }

}
