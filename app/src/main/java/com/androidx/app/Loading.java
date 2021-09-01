package com.androidx.app;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import com.androidx.R;
import com.androidx.util.Log;
import com.androidx.video.Orientation;
import com.androidx.view.LoadingView;
import com.androidx.view.ShapeButton;

/**
 * Author: Relin
 * Describe:数据加载
 * Date:2020/12/14 21:52
 */
public class Loading implements View.OnClickListener {

    public final String TAG = Loading.class.getSimpleName();
    /**
     * 上方
     */
    public final static int TOP = 2;
    /**
     * 中间
     */
    public final static int CENTER = 3;
    /**
     * 上方
     */
    public final static int HORIZONTAL = 0;
    /**
     * 中间
     */
    public final static int VERTICAL = 1;
    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 布局资源id
     */
    private int layoutId;
    /**
     * 父控件
     */
    private FrameLayout parent;
    /**
     * 加载视图
     */
    private LoadingView loading;
    /**
     * 加载文字
     */
    private TextView textView;
    /**
     * 内容布局
     */
    private FrameLayout rootLayout;
    /**
     * 内容布局
     */
    private LinearLayout contentLayout;
    /**
     * 消失延迟时间
     */
    private long delayMillis = 0;
    /**
     * 动画持续时间
     */
    private long duration = 200;
    /**
     * 顶部显示上部分间距
     */
    private int loadingTopMargin = -1;
    /**
     * 显示
     */
    private boolean showing;

    /**
     * 构造Loading
     *
     * @param context  上下文对象
     * @param parent   父级
     * @param layoutId 布局资源id
     */
    public Loading(Context context, FrameLayout parent, int layoutId) {
        this.context = context;
        this.parent = parent;
        this.layoutId = layoutId;
        onCreate(LayoutInflater.from(context).inflate(layoutId, null, false));
    }

    /**
     * 构造Loading
     *
     * @param context     上下文对象
     * @param parent      父级
     * @param loadingView 布局View
     */
    public Loading(Context context, FrameLayout parent, View loadingView) {
        this.context = context;
        this.parent = parent;
        onCreate(loadingView);
    }

    /**
     * 加载视图创建
     *
     * @param loadingView
     */
    public void onCreate(View loadingView) {
        delayMillis = context.getResources().getInteger(R.integer.loading_delay_millis_duration);
        rootLayout = loadingView.findViewById(R.id.android_loading_root);
        contentLayout = loadingView.findViewById(R.id.android_loading_content);
        loading = loadingView.findViewById(R.id.android_loading);
        textView = loadingView.findViewById(R.id.android_loading_txt);
    }

    /**
     * 设置加载文字
     *
     * @param text
     */
    public void setLoadingText(String text) {
        textView.setText(text);
        textView.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
    }

    /**
     * 获取文本控件
     *
     * @return
     */
    public TextView getTextView() {
        return textView;
    }

    /**
     * 获取根布局
     *
     * @return
     */
    public FrameLayout getRootLayout() {
        return rootLayout;
    }

    /**
     * 设置是否可用
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        if (rootLayout != null) {
            rootLayout.setEnabled(enabled);
        }
    }

    /**
     * 设置加载顶部间距，只有
     *
     * @param loadingTopMargin
     */
    public void setLoadingTopMargin(int loadingTopMargin) {
        this.loadingTopMargin = loadingTopMargin;
    }

    /**
     * 设置根布局背景
     *
     * @param resId
     */
    public void setRootBackgroundResource(@DrawableRes int resId) {
        if (rootLayout == null) {
            return;
        }
        rootLayout.setBackgroundResource(resId);
    }

    /**
     * 设置内容背景
     *
     * @param background 背景Drawable
     */
    public void setContentBackground(Drawable background) {
        if (contentLayout == null) {
            return;
        }
        contentLayout.setBackground(background);
    }

    /**
     * 设置内容背景
     *
     * @param resId 资源id
     */
    public void setContentBackgroundResource(@DrawableRes int resId) {
        if (contentLayout == null) {
            return;
        }
        contentLayout.setBackgroundResource(resId);
    }

    /**
     * 设置加载控件背景
     *
     * @param background 背景Drawable
     */
    public void setLoadingBackground(Drawable background) {
        if (loading == null) {
            return;
        }
        loading.setBackground(background);
    }

    /**
     * 设置加载控件背景
     *
     * @param resId 资源
     */
    public void setLoadingBackgroundResource(@DrawableRes int resId) {
        if (loading == null) {
            return;
        }
        loading.setBackgroundResource(resId);
    }


    /**
     * 设置线条颜色
     *
     * @param color 颜色
     */
    public void setLoadingStreakColor(int color) {
        loading.setStreakColor(color);
    }


    /**
     * 设置加载视图参数
     *
     * @param params 参数
     */
    public void setContentLayoutParams(FrameLayout.LayoutParams params) {
        if (contentLayout != null) {
            contentLayout.setLayoutParams(params);
        }
    }

    /**
     * 水平居中参数
     *
     * @param contentLayout 加载View
     * @param topMargin     上部分间距
     */
    protected FrameLayout.LayoutParams buildHorizontalCenterLayoutParams(LinearLayout contentLayout, int topMargin) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) contentLayout.getLayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.topMargin = topMargin;
        return params;
    }

    /**
     * 居中参数
     *
     * @param contentLayout 内容
     * @param orientation   方向
     */
    protected FrameLayout.LayoutParams buildCenterInParentParams(LinearLayout contentLayout, int orientation) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) contentLayout.getLayoutParams();
        params.gravity = Gravity.CENTER;
        LinearLayout.LayoutParams textParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        if (orientation == HORIZONTAL) {
            textParams.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.loading_text_margin_left);
            textParams.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.loading_text_margin_right);
            params.height = context.getResources().getDimensionPixelSize(R.dimen.loading_dialog_height_horizontal);
        }
        if (orientation == VERTICAL) {
            params.height = context.getResources().getDimensionPixelSize(R.dimen.loading_dialog_height_vertical);
            textParams.leftMargin = 0;
            textParams.rightMargin = 0;
        }
        return params;
    }

    /**
     * 是否显示
     *
     * @return
     */
    public boolean isShowing() {
        return showing;
    }

    /**
     * 显示顶部Loading
     */
    public void show() {
        show(TOP, LinearLayout.HORIZONTAL, "");
    }

    /**
     * 显示顶部Loading
     *
     * @param text 文字
     */
    public void show(String text) {
        show(TOP, LinearLayout.HORIZONTAL, text);
    }

    /**
     * Loading显示位置
     */
    private int location;

    /**
     * 显示位置
     *
     * @return
     */
    public int getLocation() {
        return location;
    }

    /**
     * 设置显示方向
     *
     * @param orientation
     */
    public void setOrientation(int orientation) {
        contentLayout.setOrientation(orientation);
    }

    /**
     * 显示Dialog类型
     *
     * @param orientation 方向
     * @param text        文字
     */
    public void showDialog(int orientation, String text) {
        showDialog(orientation, text, R.drawable.android_shape_radius8_grayf8);
    }

    /**
     * 显示Dialog类型
     *
     * @param orientation     方向
     * @param text            文字
     * @param backgroundResId 背景资源
     */
    public void showDialog(int orientation, String text, @DrawableRes int backgroundResId) {
        contentLayout.setBackgroundResource(backgroundResId);
        loading.setBackground(null);
        show(CENTER, orientation, text);
    }

    /**
     * 显示Cover类型
     *
     * @param orientation 方向
     * @param text        文字
     */
    public void showCover(int orientation, String text) {
        showCover(orientation, text, R.color.colorLoadingBackground);
    }

    /**
     * 显示Cover类型
     *
     * @param orientation 方向
     * @param text        文字
     * @param color       背景颜色
     */
    public void showCover(int orientation, String text, @ColorRes int color) {
        int value = context.getResources().getColor(color);
        rootLayout.setBackgroundColor(value);
        contentLayout.setBackgroundColor(value);
        loading.setBackground(null);
        show(CENTER, orientation, text);
    }

    /**
     * 显示顶部类型
     */
    public void showUpper() {
        show(TOP, HORIZONTAL, "");
    }

    /**
     * 显示
     *
     * @param location    位置{@link Loading#TOP} OR {@link Loading#CENTER}
     * @param orientation 方向
     * @param text        文字
     */
    public void show(int location, int orientation, String text) {
        if (isShowing()) {
            return;
        }
        this.location = location;
        if (location == Loading.TOP) {
            rootLayout.setScaleX(1.0F);
            rootLayout.setScaleY(1.0F);
        }
        if (location == Loading.CENTER) {
            rootLayout.setScaleX(1.0F);
            rootLayout.setScaleY(1.0F);
        }
        contentLayout.setOrientation(orientation);
        addLoadingLayout();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) contentLayout.getLayoutParams();
        if (location == Loading.CENTER) {
            rootLayout.setOnClickListener(this);
            params = buildCenterInParentParams(contentLayout, orientation);
        }
        if (location == Loading.TOP) {
            rootLayout.setBackgroundColor(Color.TRANSPARENT);
            if (loadingTopMargin == -1) {
                loadingTopMargin = context.getResources().getDimensionPixelOffset(R.dimen.loading_margin_top);
            }
            params = buildHorizontalCenterLayoutParams(contentLayout, loadingTopMargin);
        }
        setContentLayoutParams(params);
        setLoadingText(text);
        if (loading != null) {
            loading.start();
            showing = true;
        }
    }

    /**
     * 隐藏
     */
    public void dismiss() {
        if (isShowing()) {
            if (location == Loading.TOP) {
                startAnimator(rootLayout, 1, 0, ANIMATOR_SCALE);
            }
            if (location == Loading.CENTER) {
                startAnimator(rootLayout, 1, 0, ANIMATOR_SCALE);
            }
        }
    }

    /**
     * 动画类型
     */
    private int animatorType;
    /**
     * 缩放类型
     */
    private final int ANIMATOR_SCALE = 1;
    /**
     * 位移类型
     */
    private final int ANIMATOR_TRANSLATE = 2;
    /**
     * 动画值
     */
    private ValueAnimator animator;

    /**
     * 动画类型
     *
     * @return
     */
    public int getAnimatorType() {
        return animatorType;
    }

    /***
     * 开启动画
     * @param view 视图
     * @param start 开始值
     * @param end 结束值
     * @param animatorType 动画类型
     */
    private void startAnimator(final View view, float start, float end, final int animatorType) {
        this.animatorType = animatorType;
        if (animator == null) {
            animator = ValueAnimator.ofFloat(start, end);
        }
        animator.setStartDelay(delayMillis);
        animator.setDuration(duration);
        if (animator != null && animator.isStarted() && animator.isRunning()) {
            return;
        }
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                Log.i(TAG, "->onAnimationUpdate value=" + value);
                if (animatorType == ANIMATOR_SCALE) {
                    view.setScaleX(value);
                    view.setScaleY(value);
                }
                if (animatorType == ANIMATOR_TRANSLATE) {
                    view.setTranslationY(value);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i(TAG, "->onAnimationEnd");
                onDismissAnimationEnd();
                showing = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });

        animator.start();
    }

    /**
     * 隐藏倒计时完成
     */
    protected void onDismissAnimationEnd() {
        cancel();
    }

    /**
     * 是否添加Loading
     *
     * @return
     */
    protected boolean isAddLoadingLayout() {
        return rootLayout.getParent() != null;
    }

    /**
     * 添加加载布局
     */
    protected void addLoadingLayout() {
        if (isAddLoadingLayout()) {
            removeLoadingLayout();
        }
        parent.addView(rootLayout);
    }

    /**
     * 取消动画
     */
    protected void cancel() {
        if (loading != null) {
            loading.cancel();
        }
    }

    /**
     * 删除loading
     */
    protected void removeLoadingLayout() {
        if (rootLayout.getParent() != null) {
            parent.removeView(rootLayout);
        }
    }

    /**
     * 获取上下文对象
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取布局资源id
     *
     * @return
     */
    public int getLayoutId() {
        return layoutId;
    }

    /**
     * 设置布局资源id
     *
     * @param layoutId
     */
    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
        onCreate(LayoutInflater.from(context).inflate(layoutId, null, false));
    }

    /**
     * 获取数据加载的父控件
     *
     * @return
     */
    public ViewGroup getParent() {
        return parent;
    }

    /**
     * 设置父控件
     *
     * @param parent
     */
    public void setParent(FrameLayout parent) {
        this.parent = parent;
    }

    /**
     * 获取LoadingView
     *
     * @return
     */
    public LoadingView getLoadingView() {
        return loading;
    }

    /**
     * 设置LoadingView
     *
     * @param loadingView
     */
    public void setLoadingView(LoadingView loadingView) {
        this.loading = loadingView;
    }

    /**
     * 获取内容布局
     *
     * @return
     */
    public LinearLayout getContentLayout() {
        return contentLayout;
    }

    /**
     * 设置内容布局
     *
     * @param contentLayout
     */
    public void setContentLayout(LinearLayout contentLayout) {
        this.contentLayout = contentLayout;
    }

    /**
     * 设置加载视图消失延时时间
     *
     * @param delayMillis
     */
    public void setDelayMillis(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    /**
     * 获取加载视图消失延时时间
     *
     * @return
     */
    public long getDelayMillis() {
        return delayMillis;
    }

    @Override
    public void onClick(View v) {

    }

}
