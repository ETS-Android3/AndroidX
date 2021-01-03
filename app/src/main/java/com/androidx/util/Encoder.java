package com.androidx.util;

import android.util.Base64;

/**
 * Author: Relin
 * Describe:加密器
 * Date:2020/12/5 18:50
 */
public class Encoder {

    /**
     * 加密
     *
     * @param data 数据
     * @return
     */
    public static String encode(String data) {
        return new String(Base64.encode(data.getBytes(), Base64.NO_PADDING));
    }

}
