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

public class VideoSelector {

    public static final int VIDEO_REQUEST_CODE = 4;
    public static final int TYPE_RECORDING = 1;
    public static final int TYPE_SELECT_VIDEO = 2;
    public static final int TYPE_CANCEL = 3;

    private final Context context;
    private final CoreFragment fragment;
    private final CoreActivity activity;
    private final long minSize;
    private final long maxSize;
    private final long duration;
    private final int width;
    private final int height;
    private final OnVideoSelectorListener listener;


    public VideoSelector(Builder builder) {
        this.context = builder.context;
        this.fragment = builder.fragment;
        this.activity = builder.activity;
        this.minSize = builder.minSize;
        this.maxSize = builder.maxSize;
        this.width = builder.width;
        this.height = builder.height;
        this.listener = builder.listener;
        this.duration = builder.duration;
        show(builder);
    }

    public static class Builder {

        private Context context;
        private CoreFragment fragment;
        private CoreActivity activity;
        private long minSize = 0;
        private long maxSize = 20;
        private int width = 1080;
        private int height = 1920;
        private long duration = 15 * 1000;
        private OnVideoSelectorListener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder(CoreFragment fragment) {
            this.fragment = fragment;
            this.context = fragment.getContext();
        }

        public Builder(CoreActivity activity) {
            this.activity = activity;
            this.context = activity;
        }

        public Context getContext() {
            return context;
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
        dialog = new CoreDialog.Builder(builder.context)
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
                    fragment.startActivityForResult(VideoRecordAty.class,VIDEO_REQUEST_CODE, bundle);
                }
                if (activity != null) {
                    activity.startActivityForResult(VideoRecordAty.class, VIDEO_REQUEST_CODE, bundle);
                }
                if (listener != null) {
                    listener.onVideoSelector(builder, TYPE_RECORDING);
                }
            }
        });
        tv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (listener != null) {
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
