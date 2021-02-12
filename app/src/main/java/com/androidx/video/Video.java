package com.androidx.video;

import android.content.Context;

import com.androidx.util.Log;

import java.util.List;

public class Video implements VideoScanner.OnVideoScanListener {

    private List<VideoMedia> list;
    private Context context;
    private long minSize = 0;
    private long maxSize = 100;

    private static Video instance;

    public static Video with(Context context) {
        if (instance == null) {
            synchronized (Video.class) {
                if (instance == null) {
                    instance = new Video();
                }
            }
        }
        instance.setContext(context);
        return instance;
    }

    public Video() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setMinSize(long minSize) {
        this.minSize = minSize;
    }

    public long getMinSize() {
        return minSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public long getMaxSize() {
        return maxSize;
    }


    public void init() {
        VideoScanner.Builder builder = new VideoScanner.Builder(getContext());
        builder.listener(this);
        builder.minSize(minSize);
        builder.maxSize(maxSize);
        builder.build();
    }

    public void notifyDataChanged() {
        init();
    }

    @Override
    public void onVideoScan(List<VideoMedia> list) {
        this.list = list;
        Log.i("RRL", "->onVideoScan " + list.size());
    }

    public List<VideoMedia> getList() {
        return list;
    }

}
