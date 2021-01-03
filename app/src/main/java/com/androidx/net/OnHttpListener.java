package com.androidx.net;

/**
 * Created by Relin
 * OkHttp回调函数
 * on 2017/3/20.
 */

public interface OnHttpListener {

    /**
     * 请求服务器失败
     *
     * @param responseBody 返回数据
     */
    void onHttpFailure(ResponseBody responseBody);

    /**
     * 请求服务器成功
     *
     * @param responseBody 返回数据
     */
    void onHttpSucceed(ResponseBody responseBody);

}
