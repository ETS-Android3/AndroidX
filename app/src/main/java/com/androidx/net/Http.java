package com.androidx.net;

import com.androidx.os.Build;
import com.androidx.sqlite.SQLite;
import com.androidx.util.Log;

/**
 * Author: Relin
 * Describe:网络
 * Date:2020/11/29 0:03
 */
public  class Http {

    /**
     * 日志标识
     */
    public static String TAG = Http.class.getSimpleName();
    /**
     * Http未初始化
     */
    public final static String NOT_INIT = Http.class.getSimpleName() + " is not initialized.";
    /**
     * 没有初始化的信息
     */
    public final static String[] MSG_NOT_INIT = new String[]{
            "版本名：" + Build.NAME,
            "初始化：Http.init(options);",
            "调试模式：options.debug(true);",
            "使用缓存：options.cache(true)；",
            "详情使用：https://github.com/RelinRan/AndroidX"};
    /**
     * 请求
     */
    private static Request request;

    /**
     * 参数
     */
    private static RequestOptions config;

    /**
     * 初始化
     *
     * @param options 配置参数
     */
    public static void init(RequestOptions options) {
        config = options;
        request = new OkHttp();
        SQLite.with(config.context()).createTable(ResponseTable.class);
    }

    /**
     * 是否初始化
     *
     * @return
     */
    public static boolean isInit() {
        if (config != null) {
            return true;
        }
        return false;
    }

    /**
     * Http对象
     *
     * @return
     */
    private static Request request() {
        return request;
    }

    /**
     * 配置参数
     *
     * @return
     */
    public static RequestOptions options() {
        return config;
    }

    /**
     * Get请求
     *
     * @param url      地址
     * @param params   参数
     * @param listener 监听
     */
    public static void get(String url, RequestParams params, OnHttpListener listener) {
        if (!isInit()) {
            Log.e(TAG, NOT_INIT, MSG_NOT_INIT);
            return;
        }
        request().get(url, params, listener);
    }

    /**
     * Post请求
     *
     * @param url      地址
     * @param params   参数
     * @param listener 监听
     */
    public static void post(String url, RequestParams params, OnHttpListener listener) {
        if (!isInit()) {
            Log.e(TAG, NOT_INIT, MSG_NOT_INIT);
            return;
        }
        request().post(url, params, listener);
    }

    /**
     * Put请求
     *
     * @param url      地址
     * @param params   参数
     * @param listener 监听
     */
    public static void put(String url, RequestParams params, OnHttpListener listener) {
        if (!isInit()) {
            Log.e(TAG, NOT_INIT, MSG_NOT_INIT);
            return;
        }
        request().put(url, params, listener);
    }

    /**
     * Delete请求
     *
     * @param url      地址
     * @param params   参数
     * @param listener 监听
     */
    public static void delete(String url, RequestParams params, OnHttpListener listener) {
        if (!isInit()) {
            Log.e(TAG, NOT_INIT, MSG_NOT_INIT);
            return;
        }
        request().delete(url, params, listener);
    }

    /**
     * 取消请求
     *
     * @param tag 标识
     */
    public static void cancel(String tag) {
        request().cancel(tag);
    }

}
