package com.androidx.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.androidx.R;
import com.androidx.app.CoreFragment;
import com.androidx.app.NavigationBar;
import com.androidx.content.IOProvider;

import java.util.List;

public abstract class VideoListFgt extends CoreFragment implements VideoScanner.OnVideoScanListener, VideoAdapter.OnItemClickListener {

    public static final String VIDEO_MEDIA = "video_media";
    public static final String VIDEO_MIN_SIZE = "videoMinSize";
    public static final String VIDEO_MAX_SIZE = "videoMaxSize";

    private long minSize;
    private long maxSize;

    private VideoAdapter adapter;
    private List<VideoMedia> list;
    private ListView android_lv_content;

    @Override
    protected int getContentViewResId() {
        return R.layout.android_video_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, NavigationBar bar) {
        minSize = getActivity().getIntent().getLongExtra(VIDEO_MIN_SIZE, 0);
        maxSize = getActivity().getIntent().getLongExtra(VIDEO_MAX_SIZE, 20);
        android_lv_content = findViewById(R.id.android_lv_content);
        if (VideoScanner.list == null) {
            showLoading();
            VideoScanner.Builder builder = new VideoScanner.Builder(getContext());
            builder.path(IOProvider.getExternalCacheDir(getContext()));
            builder.minSize(minSize);
            builder.maxSize(maxSize);
            builder.listener(this);
            builder.build();
        } else {
            list = VideoScanner.list;
            adapter = new VideoAdapter(this, list, onCreateVideoImageLoader(), this);
            android_lv_content.setAdapter(adapter);
        }
    }

    public VideoAdapter getAdapter() {
        return adapter;
    }

    public List<VideoMedia> getList() {
        return list;
    }


    /**
     * 设置视频图片加载器
     *
     * @return
     */
    public abstract VideoAdapter.VideoImageLoader onCreateVideoImageLoader();

    @Override
    public void onVideoScan(List<VideoMedia> list) {
        adapter = new VideoAdapter(VideoListFgt.this, list, onCreateVideoImageLoader(), VideoListFgt.this);
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
