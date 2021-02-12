package com.androidx.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.androidx.R;
import com.androidx.app.CoreFragment;
import com.androidx.app.NavigationBar;

import java.util.List;

public class VideoListFgt extends CoreFragment implements VideoScanner.OnVideoScanListener, VideoAdapter.OnItemClickListener {

    /**
     * 视频实体
     */
    public static final String VIDEO_MEDIA = "video_media";
    /**
     * 最小视频大小，单位M
     */
    public static final String VIDEO_OPTIONS = "options";
    /**
     * 最小大小
     */
    private long minSize = 0;
    /**
     * 最大大小
     */
    private long maxSize = 50;
    /**
     * 数据适配器
     */
    private VideoAdapter adapter;
    /**
     * 列表
     */
    private List<VideoMedia> list;
    /**
     * 列表
     */
    private ListView android_lv_content;
    /**
     * 视频图片加载器
     */
    private VideoImageLoader videoImageLoader;
    /**
     * 视频列表参数
     */
    private VideoListOptions videoListOptions;

    @Override
    protected int getContentViewResId() {
        return R.layout.android_video_list;
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        videoListOptions = (VideoListOptions) args.getSerializable(VIDEO_OPTIONS);
        if (videoListOptions != null) {
            minSize = videoListOptions.getMinSize();
            maxSize = videoListOptions.getMaxSize();
            setVideoImageLoader(videoListOptions.getVideoImageLoader());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, NavigationBar bar) {
        bar.hide();
        android_lv_content = findViewById(R.id.android_lv_content);
        notifyDataSetChanged();
    }

    /**
     * 通知数据改变
     */
    public void notifyDataSetChanged() {
        if (Video.with(getContext()).getList() == null) {
            showLoading();
            VideoScanner.Builder builder = new VideoScanner.Builder(getContext());
            builder.minSize(minSize);
            builder.maxSize(maxSize);
            builder.listener(this);
            builder.build();
        } else {
            list = Video.with(getContext()).getList();
            adapter = new VideoAdapter(this, list, getVideoImageLoader(), this);
            android_lv_content.setAdapter(adapter);
        }
    }

    /**
     * 获取数据适配器对象
     *
     * @return
     */
    public VideoAdapter getAdapter() {
        return adapter;
    }

    /**
     * 获取数据列表
     *
     * @return
     */
    public List<VideoMedia> getList() {
        return list;
    }

    /**
     * 设置视频加载器
     *
     * @param videoImageLoader
     */
    public void setVideoImageLoader(VideoImageLoader videoImageLoader) {
        this.videoImageLoader = videoImageLoader;
        if (videoImageLoader != null) {
            VideoListOptions.getInstance().setVideoImageLoader(videoImageLoader);
        }
    }

    /**
     * 获取视频图片加载器
     *
     * @return
     */
    public VideoImageLoader getVideoImageLoader() {
        if (VideoListOptions.getInstance().getVideoImageLoader() != null) {
            return VideoListOptions.getInstance().getVideoImageLoader();
        }
        return videoImageLoader;
    }

    @Override
    public void onVideoScan(List<VideoMedia> list) {
        adapter = new VideoAdapter(this, list, getVideoImageLoader(), this);
        android_lv_content.setAdapter(adapter);
        dismissLoading();
    }

    @Override
    public void onItemClick(List<VideoMedia> list, int position) {
        Intent intent = new Intent();
        intent.putExtra(VIDEO_MEDIA, list.get(position));
        if (getActivity() != null) {
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        } else {
            throw new RuntimeException("The Activity is null");
        }
    }

}
