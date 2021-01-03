package com.androidx.net;

/**
 * Author: Relin
 * Describe:接口请求工具
 * Date:2020/11/29 13:33
 */
public interface Request {

    /**
     * 取消请求
     *
     * @param tag 标识
     */
    void cancel(String tag);

    /**
     * Get请求
     *
     * @param url      地址
     * @param params   参数
     * @param listener 回调
     */
    void get(String url, RequestParams params, OnHttpListener listener);

    /**
     * Post请求
     *
     * @param url      地址
     * @param params   参数
     * @param listener 回调
     */
    void post(String url, RequestParams params, OnHttpListener listener);

}
