package com.androidx.app;

import com.androidx.net.ResponseBody;

public interface OnFragmentRequestListener {

    /**
     * Fragment请求失败
     *
     * @param responseBody
     */
    void onFragmentRequestFailure(ResponseBody responseBody);

    /**
     * Fragment请求成功
     *
     * @param responseBody
     */
    void onFragmentRequestSucceed(ResponseBody responseBody);

}
