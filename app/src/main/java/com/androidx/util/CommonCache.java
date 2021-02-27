package com.androidx.util;

import android.content.Context;

import com.androidx.app.CoreApplication;
import com.androidx.content.DataStorage;

/**
 * Author: Relin
 * Describe:常用缓存
 * Date:2020/5/25 11:48
 */
public class CommonCache {

    /**
     * Token
     */
    public static final String TOKEN = "CACHE_TOKEN";

    /**
     * url
     */
    public static final String URL = "CACHE_URL";

    /**
     * 登录状态
     */
    public static final String LOGIN = "STATUS_LOGIN";

    /**
     * 设置Token
     *
     * @param value
     */
    public static void setToken(String value) {
        DataStorage.with(CoreApplication.app).put(TOKEN, value);
    }

    /**
     * 获取Token
     *
     * @return
     */
    public static String getToken() {
        return DataStorage.with(CoreApplication.app).getString(TOKEN, "");
    }

    /**
     * 设置缓存URL
     *
     * @param value
     */
    public static void setUrl(String value) {
        DataStorage.with(CoreApplication.app).put(URL, value);
    }

    /**
     * 获取缓存URL
     *
     * @return
     */
    public static String getUrl() {
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
        return getUrl() + url;
    }

    /**
     * 设置登录
     *
     * @param context 上下文
     * @param login   是否登录
     */
    public static void setLogin(Context context, boolean login) {
        DataStorage.with(context).put(LOGIN, login);
    }

    /**
     * 是否登录
     *
     * @param context 上下文
     * @return
     */
    public static boolean isLogin(Context context) {
        return DataStorage.with(context).getBoolean(LOGIN, false);
    }

    /**
     * 设置用户
     *
     * @param context 上下文
     * @param obj     对象
     */
    public static void setObject(Context context, Object obj) {
        DataStorage.with(context).put(obj);
    }

    /**
     * 获取用户
     *
     * @param context 上下文
     * @param cls     类
     * @param <T>     对象
     * @return
     */
    public static <T> T getObject(Context context, Class<T> cls) {
        return DataStorage.with(context).getObject(cls);
    }

}
