package com.androidx.content;

import android.net.Uri;

/**
 * Author: Relin
 * Describe:文件选择监听
 * Date:2020/11/23 23:22
 */
public interface OnDocumentSelectListener {

    /**
     * 文件
     *
     * @param selector 文件选择器
     * @param uri      系统Uri
     * @param path     真实文件路径
     */
    void onDocumentSelectSucceed(DocumentSelector selector, Uri uri, String path);

}
