package com.androidx.net;

import java.util.TreeMap;

/**
 * Author: Relin
 * Describe:Http头部
 * Date:2020/11/29 18:49
 */
public class Header extends TreeMap<String, String> {

    /**
     * Header - Content-Type
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * Header - User-Agent
     */
    public static final String USER_AGENT = "User-Agent";
    /**
     * 连接
     */
    public static final String CONNECTION = "Connection";
    /**
     * Header[自定义] - Cookie-Expires
     */
    public static final String COOKIE_EXPIRES = "Cookie-Expires";
    /**
     * Media-type - json
     */
    public static final String MEDIA_JSON = "application/json; charset=utf-8";
    /**
     * Media-type - form
     */
    public static final String MEDIA_FORM = "multipart/form-data; charset=utf-8";
    /**
     * Media-type - stream
     */
    public static final String MEDIA_STREAM = "application/octet-stream; charset=utf-8";
    /**
     * 请求内容 - JSON
     */
    public static final String CONTENT_JSON = "JSON";
    /**
     * 请求内容 - 表单
     */
    public static final String CONTENT_FORM = "FORM";
    /**
     * 请求内容 - 字符串
     */
    public static final String CONTENT_STRING = "STRING";


    public Header(){
        add(USER_AGENT,"Android");
        add(CONTENT_TYPE,CONTENT_JSON);
    }

    /**
     * 添加Header
     *
     * @param key   键
     * @param value 值
     */
    public void add(String key, String value) {
        put(key, value);
    }

}
