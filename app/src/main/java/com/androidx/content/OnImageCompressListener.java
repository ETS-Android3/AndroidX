package com.androidx.content;

import java.io.File;

public interface OnImageCompressListener {

    /**
     * 图片压缩开始
     *
     * @param compressor
     */
    void onImageCompressStart(ImageCompressor compressor);

    /**
     * 图片压缩结束
     *
     * @param compressor
     * @param file
     */
    void onImageCompressSucceed(ImageCompressor compressor, File file);

}
