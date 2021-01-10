package com.androidx.video;

import android.hardware.Camera;
import android.media.MediaRecorder;

import java.io.Serializable;

public class VideoRecordParams implements Serializable {

    /**
     * 视频文件夹
     */
    private String videoFolder;
    /**
     * 视频路径
     */
    private String videoPath;
    /**
     * 视频宽度
     */
    private int width = 1920;
    /**
     * 视频高度
     */
    private int height = 1080;
    /**
     * 录制时间限制
     */
    private int duration = 15 * 1000;
    /**
     * 视频编码
     */
    private int videoEncoder = MediaRecorder.VideoEncoder.H264;
    /**
     * 相机id
     */
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    public String getVideoFolder() {
        return videoFolder;
    }

    public void setVideoFolder(String videoFolder) {
        this.videoFolder = videoFolder;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getVideoEncoder() {
        return videoEncoder;
    }

    public void setVideoEncoder(int videoEncoder) {
        this.videoEncoder = videoEncoder;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }
}
