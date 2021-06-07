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
    void cancel(Class tag);

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


    /**
     * Put请求
     *
     * @param url      地址
     * @param params   参数
     * @param listener 回调
     */
    void put(String url, RequestParams params, OnHttpListener listener);

    /**
     * Delete请求
     *
     * @param url      地址
     * @param params   参数
     * @param listener 回调
     */
    void delete(String url, RequestParams params, OnHttpListener listener);

}
