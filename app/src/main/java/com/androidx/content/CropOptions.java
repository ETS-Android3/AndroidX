package com.androidx.content;

import android.net.Uri;

/**
 * Author: Relin
 * Describe:
 * Date:2020/11/21 17:14
 */
public class CropOptions {
    /**
     * 剪切参数 - 是否剪切图片
     */
    private boolean crop;
    /**
     * 剪切参数 - 比例X
     */
    private int aspectX = 1;
    /**
     * 剪切参数 - 剪切比例Y
     */
    private int aspectY = 1;
    /**
     * 剪切参数 - 输出图片宽度
     */
    private int outputX = 1080;
    /**
     * 剪切参数 - 输出图片高度
     */
    private int outputY = 1080;
    /**
     * 剪切参数 - 是否返回数据
     */
    private boolean returnData = false;
    /**
     * 剪切参数 - 圆角剪切（部分机型支持）
     */
    private boolean circleCrop = false;
    /**
     * 是否面部识别
     */
    private boolean noFaceDetection = false;
    /**
     * 需要剪切图像Uri
     */
    private Uri data;
    /**
     * 输入Uri
     */
    private Uri outPut;

    public boolean isCrop() {
        return crop;
    }

    public CropOptions crop(boolean crop) {
        this.crop = crop;
        return this;
    }

    public int aspectX() {
        return aspectX;
    }

    public CropOptions aspectX(int aspectX) {
        this.aspectX = aspectX;
        return this;
    }

    public int aspectY() {
        return aspectY;
    }

    public CropOptions aspectY(int aspectY) {
        this.aspectY = aspectY;
        return this;
    }

    public int outputX() {
        return outputX;
    }

    public CropOptions outputX(int outputX) {
        this.outputX = outputX;
        return this;
    }

    public int outputY() {
        return outputY;
    }

    public CropOptions outputY(int outputY) {
        this.outputY = outputY;
        return this;
    }

    public boolean isReturnData() {
        return returnData;
    }

    public CropOptions returnData(boolean returnData) {
        this.returnData = returnData;
        return this;
    }

    public boolean isCircleCrop() {
        return circleCrop;
    }

    public CropOptions circleCrop(boolean circleCrop) {
        this.circleCrop = circleCrop;
        return this;
    }

    public boolean isNoFaceDetection() {
        return noFaceDetection;
    }

    public CropOptions noFaceDetection(boolean noFaceDetection) {
        this.noFaceDetection = noFaceDetection;
        return this;
    }

    public Uri data() {
        return data;
    }

    public CropOptions data(Uri data) {
        this.data = data;
        return this;
    }

    public Uri outPut() {
        return outPut;
    }

    public CropOptions outPut(Uri outPut) {
        this.outPut = outPut;
        return this;
    }

    @Override
    public String toString() {
        return "CropOptions{" +
                "crop=" + crop +
                ", aspectX=" + aspectX +
                ", aspectY=" + aspectY +
                ", outputX=" + outputX +
                ", outputY=" + outputY +
                ", returnData=" + returnData +
                ", circleCrop=" + circleCrop +
                ", noFaceDetection=" + noFaceDetection +
                ", data=" + data +
                ", outPut=" + outPut +
                '}';
    }
}
