package com.androidx.app;

import com.androidx.net.ResponseBody;

public interface FragmentHttpCallback {

    /**
     * Fragment请求失败
     *
     * @param responseBody
     */
    void onFragmentHttpFailure(ResponseBody responseBody);

    /**
     * Fragment请求成功
     *
     * @param responseBody
     */
    void onFragmentHttpSucceed(ResponseBody responseBody);

}
