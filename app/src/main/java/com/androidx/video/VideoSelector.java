package com.androidx.video;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidx.R;
import com.androidx.app.CoreActivity;
import com.androidx.app.CoreFragment;
import com.androidx.dialog.CoreDialog;

/**
 * 视频选择器
 */
public class VideoSelector {

    /**
     * 视频请求代码
     */
    public static final int VIDEO_REQUEST_CODE = 4;
    /**
     * 录制
     */
    public static final int TYPE_RECORDING = 1;
    /**
     * 选择
     */
    public static final int TYPE_SELECT_VIDEO = 2;
    /**
     * 取消
     */
    public static final int TYPE_CANCEL = 3;

    private final CoreFragment fragment;
    private final CoreActivity activity;
    private final long duration;
    private final int width;
    private final int height;
    private final VideoListOptions listOptions;
    private final OnVideoSelectorListener listener;


    public VideoSelector(Builder builder) {
        this.fragment = builder.fragment;
        this.activity = builder.activity;
        this.width = builder.width;
        this.height = builder.height;
        this.listener = builder.listener;
        this.duration = builder.duration;
        this.listOptions = builder.listOptions;
        show(builder);
    }

    public Context getContext() {
        if (fragment != null) {
            return fragment.getContext();
        }
        if (activity != null) {
            return activity;
        }
        return null;
    }

    public static class Builder {

        private CoreFragment fragment;
        private CoreActivity activity;
        private long minSize = 0;
        private long maxSize = 20;
        private int width = 1080;
        private int height = 1920;
        private long duration = 15 * 1000;
        private OnVideoSelectorListener listener;
        private VideoListOptions listOptions;

        public Builder() {
        }

        public Builder(CoreFragment fragment) {
            this.fragment = fragment;
        }

        public Builder(CoreActivity activity) {
            this.activity = activity;
        }

        public CoreFragment fragment() {
            return fragment;
        }

        public Builder fragment(CoreFragment fragment) {
            this.fragment = fragment;
            return this;
        }

        public CoreActivity activity() {
            return activity;
        }

        public Builder activity(CoreActivity activity) {
            this.activity = activity;
            return this;
        }

        public long minSize() {
            return minSize;
        }

        public Builder minSize(long minSize) {
            this.minSize = minSize;
            return this;
        }

        public long maxSize() {
            return maxSize;
        }

        public Builder maxSize(long maxSize) {
            this.maxSize = maxSize;
            return this;
        }

        public int width() {
            return width;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public int height() {
            return height;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public long duration() {
            return duration;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public OnVideoSelectorListener listener() {
            return listener;
        }

        public Builder listener(OnVideoSelectorListener listener) {
            this.listener = listener;
            return this;
        }

        public VideoListOptions listOptions() {
            return listOptions;
        }

        public Builder listOptions(VideoListOptions listOptions) {
            this.listOptions = listOptions;
            return this;
        }

        public VideoSelector build() {
            return new VideoSelector(this);
        }


    }

    private CoreDialog dialog;

    /**
     * 显示选择图片对话框
     *
     * @param builder 参数类
     */
    private android.app.Dialog show(final Builder builder) {
        dialog = new CoreDialog.Builder(getContext())
                .width(LinearLayout.LayoutParams.MATCH_PARENT)
                .height(LinearLayout.LayoutParams.WRAP_CONTENT)
                .layoutResId(R.layout.android_dialog_video_selector)
                .animResId(CoreDialog.ANIM_BOTTOM)
                .themeResId(CoreDialog.THEME_TRANSLUCENT)
                .gravity(Gravity.BOTTOM)
                .build();
        TextView tv_take = dialog.contentView.findViewById(R.id.android_tv_take);
        TextView tv_video = dialog.contentView.findViewById(R.id.android_tv_video);
        TextView tv_cancel = dialog.contentView.findViewById(R.id.android_tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onVideoSelector(builder, TYPE_CANCEL);
                }
            }
        });
        tv_take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putInt("width", width);
                bundle.putInt("height", height);
                bundle.putLong("duration", duration);
                if (fragment != null) {
                    fragment.startActivityForResult(VideoRecordAty.class, VIDEO_REQUEST_CODE, bundle);
                }
                if (activity != null) {
                    activity.startActivityForResult(VideoRecordAty.class, VIDEO_REQUEST_CODE, bundle);
                }
                if (listener != null && activity == null && fragment == null) {
                    listener.onVideoSelector(builder, TYPE_RECORDING);
                }
            }
        });
        tv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (fragment != null) {
                    VideoListAty.start(fragment, listOptions);
                }
                if (activity != null) {
                    VideoListAty.start(activity, listOptions);
                }
                if (listener != null && activity == null && fragment == null) {
                    listener.onVideoSelector(builder, TYPE_SELECT_VIDEO);
                }
            }
        });
        return dialog.show().dialog;
    }


    public interface OnVideoSelectorListener {

        void onVideoSelector(Builder builder, int selector);

    }

}
