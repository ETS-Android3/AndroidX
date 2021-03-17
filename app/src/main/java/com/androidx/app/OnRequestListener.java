package com.androidx.app;

import com.androidx.net.ResponseBody;

public interface OnRequestListener {

    /**
     * Fragment请求失败
     *
     * @param responseBody
     */
    void onRequestFailure(ResponseBody responseBody);

    /**
     * Fragment请求成功
     *
     * @param responseBody
     */
    void onRequestSucceed(ResponseBody responseBody);

}
