package com.androidx.video;

import java.io.Serializable;

public class VideoListOptions implements Serializable {

    /**
     * 最小大小 - 选择视频可用
     */
    private long minSize = 0;
    /**
     * 最大大小 - 选择视频可用
     */
    private long maxSize = 50;
    /**
     * 反馈按钮资源
     */
    private int backResId;
    /**
     * 标题
     */
    private String title;
    /**
     * 标题文字大小
     */
    private int titleTextSize;
    /**
     * 标题颜色
     */
    private int titleColor;
    /**
     * 标题背景颜色
     */
    private int titleBackgroundColor;
    /**
     * 图片加载器
     */
    private VideoImageLoader videoImageLoader;
    /**
     * 实例
     */
    private static VideoListOptions instance;

    public static VideoListOptions getInstance() {
        if (instance == null) {
            synchronized (VideoListOptions.class) {
                if (instance == null) {
                    instance = new VideoListOptions();
                }
            }
        }
        return instance;
    }

    public long getMinSize() {
        return minSize;
    }

    public void setMinSize(long minSize) {
        this.minSize = minSize;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public void setVideoImageLoader(VideoImageLoader videoImageLoader) {
        this.videoImageLoader = videoImageLoader;
    }

    public VideoImageLoader getVideoImageLoader() {
        return videoImageLoader;
    }

    public int getBackResId() {
        return backResId;
    }

    public void setBackResId(int backResId) {
        this.backResId = backResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTitleTextSize() {
        return titleTextSize;
    }

    public void setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public int getTitleBackgroundColor() {
        return titleBackgroundColor;
    }

    public void setTitleBackgroundColor(int titleBackgroundColor) {
        this.titleBackgroundColor = titleBackgroundColor;
    }
}
