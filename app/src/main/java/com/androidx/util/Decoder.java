package com.androidx.util;

import android.util.Base64;

/**
 * Author: Relin
 * Describe:解码器
 * Date:2020/12/12 20:39
 */
public class Decoder {

    /**
     * 解析器
     *
     * @param data 数据
     * @return
     */
    public static String decode(String data) {
        return new String(Base64.decode(data, Base64.NO_PADDING));
    }

}
