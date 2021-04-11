package com.androidx.app;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.DrawableRes;

import com.androidx.R;
import com.androidx.util.Log;
import com.androidx.view.LoadingView;

/**
 * Author: Relin
 * Describe:数据加载
 * Date:2020/12/14 21:52
 */
public class Loading {

    public final String TAG = Loading.class.getSimpleName();

    /**
     * 上方
     */
    public final static int TOP = 0;
    /**
     * 中间
     */
    public final static int CENTER = 1;
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
     * 内容布局
     */
    private RelativeLayout contentLayout;
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
        loading = loadingView.findViewById(R.id.android_loading);
        contentLayout = loadingView.findViewById(R.id.android_loading_content);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        contentLayout.setLayoutParams(params);
    }

    /**
     * 设置是否可用
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        if (contentLayout != null) {
            contentLayout.setEnabled(enabled);
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
     * @param resId 资源
     */
    public void setLoadingBackgroundResource(@DrawableRes int resId) {
        if (loading == null) {
            return;
        }
        loading.setBackgroundResource(resId);
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
     * 设置加载控件背景
     *
     * @param background 背景Drawable
     */
    public void setLoadingBackground(Drawable background) {
        if (contentLayout == null) {
            return;
        }
        contentLayout.setBackground(background);
    }

    /**
     * 设置加载视图参数
     *
     * @param params 参数
     */
    public void setLoadingLayoutParams(RelativeLayout.LayoutParams params) {
        if (loading != null) {
            loading.setLayoutParams(params);
        }
    }

    /**
     * 水平居中参数
     *
     * @param loading   加载View
     * @param topMargin 上部分间距
     */
    protected RelativeLayout.LayoutParams buildHorizontalCenterLayoutParams(LoadingView loading, int topMargin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) loading.getLayoutParams();
        params.topMargin = topMargin;
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        return params;
    }

    /**
     * 居中参数
     *
     * @param loading 加载View
     */
    protected RelativeLayout.LayoutParams buildCenterInParentParams(LoadingView loading) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) loading.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
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
        show(TOP);
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

    private float loadingY;

    /**
     * 显示
     *
     * @param location 位置{@link Loading#TOP} OR {@link Loading#CENTER}
     */
    public void show(int location) {
        if (isShowing()) {
            return;
        }
        this.location = location;
        if (location == Loading.TOP) {
            loading.setScaleX(1.0F);
            loading.setScaleY(1.0F);
        }
        if (location == Loading.CENTER) {
            loading.setScaleX(1.0F);
            loading.setScaleY(1.0F);
        }
        addLoadingLayout();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) loading.getLayoutParams();
        if (location == Loading.CENTER) {
            params = buildCenterInParentParams(loading);
        }
        if (location == Loading.TOP) {
            if (loadingTopMargin == -1) {
                loadingTopMargin = context.getResources().getDimensionPixelOffset(R.dimen.loading_margin_top);
            }
            params = buildHorizontalCenterLayoutParams(loading, loadingTopMargin);
        }
        setLoadingLayoutParams(params);
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
            if (location == Loading.CENTER) {
                startAnimator(loading, 1, 0, ANIMATOR_SCALE);
            }
            if (location == Loading.TOP) {
                float y = loadingY + loading.getMeasuredHeight() + loadingTopMargin;
                startAnimator(loading, 1, 0, ANIMATOR_SCALE);
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
        return contentLayout.getParent() != null;
    }

    /**
     * 添加加载布局
     */
    protected void addLoadingLayout() {
        if (isAddLoadingLayout()) {
            removeLoadingLayout();
        }
        parent.addView(contentLayout);
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
        if (contentLayout.getParent() != null) {
            parent.removeView(contentLayout);
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
    public RelativeLayout getContentLayout() {
        return contentLayout;
    }

    /**
     * 设置内容布局
     *
     * @param contentLayout
     */
    public void setContentLayout(RelativeLayout contentLayout) {
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

}
