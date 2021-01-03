package com.androidx.lang;

import java.io.File;

/**
 * Created by Relin on 2016/9/6.
 * 保存错误日志的监听
 */

public interface OnBugListener {

    /**
     * 保存错误日志成功回调函数
     *
     * @param file 错误文件
     * @param bug 错误内容
     */
    void onBug(File file, String bug);

}
