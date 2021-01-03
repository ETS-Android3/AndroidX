package com.androidx.net;

/**
 * Author: Relin
 * Describe:内容转义
 * Date:2020/11/29 20:07
 */
public interface EscapeJar {

    /**
     * 反转义
     *
     * @param content 内容
     * @return
     */
    String unescape(String content);

    /**
     * 转义
     *
     * @param content 内容
     * @return
     */
    String escape(String content);

}
