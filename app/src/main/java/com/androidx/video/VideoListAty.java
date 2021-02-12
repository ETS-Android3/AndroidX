package com.androidx.video;

import android.os.Bundle;

import com.androidx.R;
import com.androidx.app.CoreActivity;
import com.androidx.app.CoreFragment;
import com.androidx.app.NavigationBar;

/**
 * 视频列表
 */
public class VideoListAty extends CoreActivity {

    /**
     * 请求代码
     */
    public static final int REQUEST_CODE = 1000;
    /**
     * 视频图片加载器
     */
    private VideoImageLoader videoImageLoader;

    @Override
    protected int getContentViewResId() {
        return R.layout.android_video_list_content;
    }

    @Override
    protected int getContainerViewId() {
        return R.id.android_frame_video;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, NavigationBar bar) {
        VideoListOptions options = (VideoListOptions) getIntent().getSerializableExtra("options");
        if (options != null) {
            bar.setBackResource(options.getBackResId());
            bar.setTitle(options.getTitle());
            bar.setBackgroundColor(options.getTitleBackgroundColor());
        }
        VideoListFgt fragment = new VideoListFgt();
        Bundle bundle = new Bundle();
        bundle.putSerializable("options", options);
        fragment.setArguments(bundle);
        addFragment(fragment);
    }

    /**
     * 跳转到当前页面
     *
     * @param activity 页面
     * @param options  参数
     */
    public static void start(CoreActivity activity, VideoListOptions options) {
        if (activity == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("options", options);
        activity.startActivityForResult(VideoListAty.class, REQUEST_CODE, bundle);
    }

    /**
     * 跳转到当前页面
     *
     * @param fragment 页面
     * @param options  参数
     */
    public static void start(CoreFragment fragment, VideoListOptions options) {
        if (fragment == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("options", options);
        fragment.startActivityForResult(VideoListAty.class, REQUEST_CODE, bundle);
    }

}
