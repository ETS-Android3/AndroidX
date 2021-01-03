package com.androidx.upload;

import java.io.IOException;

public interface OnUploadListener {

    /**
     * 文件上传进度
     * @param response
     * @param contentLength
     * @param progress
     */
    void onUploadProgress(UploadResponse response, long contentLength, long progress);

    /**
     * 文件上传失败
     * @param response
     * @param e
     */
    void onUploadFailure(UploadResponse response, IOException e);

    /**
     * 文件上传
     * @param response
     */
    void onUploadResponse(UploadResponse response);

}
