package com.androidx.util;

import com.androidx.app.CoreApplication;
import com.androidx.content.DataStorage;

/**
 * Author: Relin
 * Describe:常用缓存
 * Date:2020/5/25 11:48
 */
public class UseCache {

    /**
     * Token
     */
    public static final String TOKEN = "CACHE_TOKEN";

    /**
     * 基础
     */
    public static final String URL = "CACHE_URL";

    /**
     * 设置Token
     *
     * @param value
     */
    public static void token(String value) {
        DataStorage.with(CoreApplication.app).put(TOKEN, value);
    }

    /**
     * 获取Token
     *
     * @return
     */
    public static String token() {
        return DataStorage.with(CoreApplication.app).getString(TOKEN, "");
    }

    /**
     * 设置缓存URL
     *
     * @param value
     */
    public static void url(String value) {
        DataStorage.with(CoreApplication.app).put(URL, value);
    }

    /**
     * 获取缓存URL
     *
     * @return
     */
    public static String url() {
        return DataStorage.with(CoreApplication.app).getString(URL, "");
    }

    /**
     * 拼接Url
     *
     * @param url
     * @return
     */
    public static String spliceUrl(String url) {
        if (url != null && url.startsWith("http")) {
            return url;
        }
        return url() + url;
    }

}
