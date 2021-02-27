package com.androidx.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.androidx.R;
import com.androidx.widget.BannerAdapter;

/**
 * Created by Relin
 * on 2018-10-09.
 * </br>
 * 轮播图视图页控件，可以通过自定义的图片显示指示器的样式形状，shape图片或者
 * 设置数据通过{@link BannerPager#setAdapter},同时支持设置指示器布局的边距
 * 和指示器本身的边距大小，指示器的位置 {@link BannerPager#setIndicatorGravity}
 */
public class BannerPager extends FrameLayout implements ViewPager.OnPageChangeListener, BannerAdapter.OnDataSetChangeListener {

    public final String TAG = BannerPager.class.getSimpleName();
    //上下文
    private Context context;
    //轮播页
    private NestedViewPager pager;
    //轮播数据
    private BannerAdapter adapter;
    //指示器布局
    private LinearLayout indicatorLayout;
    //轮播控制
    private PlayHandler handler;
    //选中图资源
    private int indicatorSelectedResource = R.drawable.android_indicator_selected;
    //未选中图资源
    private int indicatorUnSelectedResource = R.drawable.android_indicator_unselected;
    //指示器布局间距
    private float indicatorLayoutMargin = 0;
    //指示器布局左间距
    private float indicatorLayoutMarginLeft = dpToPx(10);
    //指示器布局上间距
    private float indicatorLayoutMarginTop = dpToPx(10);
    //指示器布局右间距
    private float indicatorLayoutMarginRight = dpToPx(10);
    //指示器布局下间距
    private float indicatorLayoutMarginBottom = dpToPx(10);
    //指示器间距
    private float indicatorMargin = 0;
    //指示器左间距
    private float indicatorMarginLeft = dpToPx(5);
    //指示器上间距
    private float indicatorMarginTop = 0;
    //指示器右间距
    private float indicatorMarginRight = dpToPx(5);
    //指示器下间距
    private float indicatorMarginBottom = 0;
    //指示器位置
    private int indicatorGravity = Gravity.BOTTOM | Gravity.CENTER;
    //是否自动播放
    private boolean isAutoPlay = true;
    //轮播时间
    private int duration = 3 * 1000;
    //是否循环
    private boolean isLoop = true;
    //页面改变监听
    private OnPageChangeListener onPageChangeListener;

    public BannerPager(@NonNull Context context) {
        super(context);
        initAttrs(context, null);
    }

    public BannerPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public BannerPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        this.context = context;
        handler = new PlayHandler();
        pager = new NestedViewPager(context);
        indicatorLayout = new LinearLayout(context);
        //自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerPager);
        indicatorSelectedResource = typedArray.getResourceId(R.styleable.BannerPager_indicatorSelected, indicatorSelectedResource);
        indicatorUnSelectedResource = typedArray.getResourceId(R.styleable.BannerPager_indicatorUnSelected, indicatorUnSelectedResource);
        isAutoPlay = typedArray.getBoolean(R.styleable.BannerPager_isAutoPlay, isAutoPlay);
        isLoop = typedArray.getBoolean(R.styleable.BannerPager_isLoop, isLoop);
        duration = typedArray.getInt(R.styleable.BannerPager_duration, duration);
        indicatorGravity = typedArray.getInt(R.styleable.BannerPager_indicatorGravity, indicatorGravity);
        indicatorLayoutMargin = typedArray.getDimension(R.styleable.BannerPager_indicatorLayoutMargin, indicatorLayoutMargin);
        indicatorLayoutMarginLeft = typedArray.getDimension(R.styleable.BannerPager_indicatorLayoutMarginLeft, indicatorLayoutMarginLeft);
        indicatorLayoutMarginTop = typedArray.getDimension(R.styleable.BannerPager_indicatorLayoutMarginTop, indicatorLayoutMarginTop);
        indicatorLayoutMarginRight = typedArray.getDimension(R.styleable.BannerPager_indicatorLayoutMarginRight, indicatorLayoutMarginRight);
        indicatorLayoutMarginBottom = typedArray.getDimension(R.styleable.BannerPager_indicatorLayoutMarginBottom, indicatorLayoutMarginBottom);
        indicatorMargin = typedArray.getDimension(R.styleable.BannerPager_indicatorMargin, indicatorMargin);
        indicatorMarginLeft = typedArray.getDimension(R.styleable.BannerPager_indicatorMarginLeft, indicatorMarginLeft);
        indicatorMarginTop = typedArray.getDimension(R.styleable.BannerPager_indicatorMarginTop, indicatorMarginTop);
        indicatorMarginRight = typedArray.getDimension(R.styleable.BannerPager_indicatorMarginRight, indicatorMarginRight);
        indicatorMarginBottom = typedArray.getDimension(R.styleable.BannerPager_indicatorMarginBottom, indicatorMarginBottom);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //图片
        LayoutParams pagerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        pager.setLayoutParams(pagerParams);
        addView(pager);
        //指示器容器
        LayoutParams indicatorLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        indicatorLayoutParams.gravity = indicatorGravity;
        if (indicatorLayoutMargin != 0) {
            indicatorLayoutParams.leftMargin = (int) indicatorLayoutMargin;
            indicatorLayoutParams.topMargin = (int) indicatorLayoutMargin;
            indicatorLayoutParams.rightMargin = (int) indicatorLayoutMargin;
            indicatorLayoutParams.bottomMargin = (int) indicatorLayoutMargin;
        } else {
            indicatorLayoutParams.leftMargin = (int) indicatorLayoutMarginLeft;
            indicatorLayoutParams.topMargin = (int) indicatorLayoutMarginTop;
            indicatorLayoutParams.rightMargin = (int) indicatorLayoutMarginRight;
            indicatorLayoutParams.bottomMargin = (int) indicatorLayoutMarginBottom;
        }
        indicatorLayout.setLayoutParams(indicatorLayoutParams);
        indicatorLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(indicatorLayout);
    }

    private float downX, downY, moveX, moveY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveX = moveY = 0F;
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX += event.getX() - downX;
                moveY += event.getY() - downY;
                if (Math.abs(moveY) - Math.abs(moveX) > 0) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveX = moveY = 0F;
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX += event.getX() - downX;
                moveY += event.getY() - downY;
                if (Math.abs(moveY) - Math.abs(moveX) > 0) {
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置数据适配器
     *
     * @param adapter
     */
    public void setAdapter(BannerAdapter adapter) {
        this.adapter = adapter;
        initAdapter(adapter);
    }

    protected void initAdapter(BannerAdapter adapter) {
        adapter.setLoop(isLoop());
        adapter.setOnDataSetChangeListener(this);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(this);
        addIndicatorItems();
        pager.setOffscreenPageLimit(adapter.getCount());
        pager.setCurrentItem(adapter.isLoop() ? 1 : 0);
        if (isAutoPlay) {
            play();
        }
    }

    @Override
    public void onDataSetChanged(BannerAdapter adapter) {
        setAdapter(adapter);
    }

    /**
     * 设置指示器显示
     *
     * @param visibility
     */
    public void setIndicatorVisibility(int visibility) {
        if (indicatorLayout != null) {
            indicatorLayout.setVisibility(visibility);
        }
    }

    /**
     * 添加页面改变监听
     *
     * @param onPageChangeListener
     */
    public void addOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //指示器位置
        if (isLoop) {
            if (pager.getCurrentItem() == 0) {
                setCurrentIndicator(adapter.getCount() - 2);
            }
            if (pager.getCurrentItem() == adapter.getCount() - 1) {
                setCurrentIndicator(1);
            }
        }
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrolled(isLoop ? position - 1 : position, positionOffset, positionOffsetPixels);
        }
    }

    private int position = 0;

    @Override
    public void onPageSelected(int position) {
        setCurrentIndicator(position);
        if (onPageChangeListener != null) {
            position = isLoop ? position - 1 : position;
            if (position == -1) {
                position = 0;
            }
            if (indicatorLayout != null && position == indicatorLayout.getChildCount() - 2) {
                position = indicatorLayout.getChildCount() - 3;
            }
            if (position == this.position) {
                return;
            }
            this.position = position;
            onPageChangeListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_SETTLING) {

        }
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            stop();
        }
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (isAutoPlay) {
                play();
            }
            //图片位置
            if (isLoop) {
                if (pager.getCurrentItem() == 0) {
                    pager.setCurrentItem(adapter.getCount() - 2, false);
                }
                if (pager.getCurrentItem() == adapter.getCount() - 1) {
                    pager.setCurrentItem(1, false);
                }
            }
        }
        if (onPageChangeListener != null) {
            onPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    /**
     * 添加指示器Item
     */
    private void addIndicatorItems() {
        if (adapter == null || indicatorLayout == null) {
            return;
        }
        int childCount = indicatorLayout.getChildCount();
        int adapterCount = adapter.getCount();
        if (childCount != 0) {
            clearIndicator();
        }
        for (int i = 0; i < adapterCount; i++) {
            addIndicator();
        }
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            if (isLoop) {
                ImageView indicator = (ImageView) indicatorLayout.getChildAt(i);
                if (i == 0 || i == indicatorLayout.getChildCount() - 1) {
                    indicator.setVisibility(GONE);
                } else {
                    indicator.setVisibility(View.VISIBLE);
                }
            }
        }
        setCurrentIndicator(isLoop ? 1 : 0);
    }

    /**
     * 清空指示器
     */
    protected void clearIndicator() {
        int childCount = indicatorLayout.getChildCount();
        if (childCount == 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            indicatorLayout.removeAllViews();
        }
    }

    /**
     * 添加指示器
     */
    protected void addIndicator() {
        LinearLayout.LayoutParams indicatorImageParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        if (indicatorMargin != 0) {
            indicatorImageParams.setMargins((int) indicatorMargin, (int) indicatorMargin, (int) indicatorMargin, (int) indicatorMargin);
        } else {
            indicatorImageParams.setMargins((int) indicatorMarginLeft, (int) indicatorMarginTop, (int) indicatorMarginRight, (int) indicatorMarginBottom);
        }
        ImageView indicator = new ImageView(context);
        indicator.setLayoutParams(indicatorImageParams);
        //添加指示器到容器
        indicatorLayout.addView(indicator, indicatorImageParams);
    }

    /**
     * 设置当前指示器位置
     *
     * @param position
     */
    private void setCurrentIndicator(int position) {
        if (adapter == null) {
            return;
        }
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            ImageView indicator = (ImageView) indicatorLayout.getChildAt(i);
            if (indicator != null) {
                if (i == position) {
                    indicator.setImageResource(indicatorSelectedResource);
                } else {
                    indicator.setImageResource(indicatorUnSelectedResource);
                }
            }
        }
    }

    /**
     * 设置位置
     * 需要注意的是在setAdapter之后设置位置才行
     *
     * @param position
     */
    public void setPosition(int position) {
        if (adapter == null) {
            new Exception("The setPosition() method should be after the setAdapter() method.");
            return;
        }
        if (pager == null) {
            new NullPointerException("BannerPager pager is null,you can't set position.");
            return;
        }
        if (pager != null) {
            pager.setCurrentItem(isLoop() ? position + 1 : position);
        }
    }



    /**
     * 播放控制器
     */
    private class PlayHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pager.setCurrentItem(pager.getCurrentItem() + 1);
            play();
        }
    }

    /**
     * 播放跳转
     */
    public void play() {
        if (handler != null && !handler.hasMessages(1)) {
            handler.sendEmptyMessageDelayed(1, duration);
        }
    }

    /**
     * 停止跳转
     */
    public void stop() {
        if (handler != null) {
            handler.removeMessages(1);
        }
    }

    /**
     * 销毁 - 防止内容泄露
     */
    public void destroy() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    /**
     * 设置页面转变动画 - 同ViewPager
     *
     * @param reverseDrawingOrder
     * @param transformer
     */
    public void setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer) {
        pager.setPageTransformer(reverseDrawingOrder, transformer);
    }

    /**
     * 设置页面转变动画 - 同ViewPager
     *
     * @param reverseDrawingOrder
     * @param transformer
     * @param pageLayerType
     */
    public void setPageTransformer(boolean reverseDrawingOrder, ViewPager.PageTransformer transformer, int pageLayerType) {
        pager.setPageTransformer(reverseDrawingOrder, transformer, pageLayerType);
    }

    /**
     * 设置选中的指示器的图片
     *
     * @param indicatorSelectedResource
     */
    public void setSelectedIndicatorResource(int indicatorSelectedResource) {
        this.indicatorSelectedResource = indicatorSelectedResource;
    }

    /**
     * 设置未选中的指示器的图片
     *
     * @param indicatorUnSelectedResource
     */
    public void setUnIndicatorSelectedResource(int indicatorUnSelectedResource) {
        this.indicatorUnSelectedResource = indicatorUnSelectedResource;
    }

    /**
     * 设置指示器布局的外间距
     *
     * @param indicatorLayoutMargin
     */
    public void setIndicatorLayoutMargin(float indicatorLayoutMargin) {
        this.indicatorLayoutMargin = dpToPx(indicatorLayoutMargin);
    }

    /**
     * 设置指示器布局的左边间距
     *
     * @param indicatorLayoutMarginLeft
     */
    public void setIndicatorLayoutMarginLeft(float indicatorLayoutMarginLeft) {
        this.indicatorLayoutMarginLeft = dpToPx(indicatorLayoutMarginLeft);
    }

    /**
     * 设置指示器布局的上边间距
     *
     * @param indicatorLayoutMarginTop
     */
    public void setIndicatorLayoutMarginTop(float indicatorLayoutMarginTop) {
        this.indicatorLayoutMarginTop = dpToPx(indicatorLayoutMarginTop);
    }

    /**
     * 设置指示器布局的右边间距
     *
     * @param indicatorLayoutMarginRight
     */
    public void setIndicatorLayoutMarginRight(float indicatorLayoutMarginRight) {
        this.indicatorLayoutMarginRight = dpToPx(indicatorLayoutMarginRight);
    }

    /**
     * 设置指示器布局的下边间距
     *
     * @param indicatorLayoutMarginBottom
     */
    public void setIndicatorLayoutMarginBottom(float indicatorLayoutMarginBottom) {
        this.indicatorLayoutMarginBottom = dpToPx(indicatorLayoutMarginBottom);
    }

    /**
     * 设置指示器之间的间距
     *
     * @param indicatorMargin
     */
    public void setIndicatorMargin(float indicatorMargin) {
        this.indicatorMargin = dpToPx(indicatorMargin);
    }

    /**
     * 设置指示器之间的间距
     *
     * @param indicatorMarginLeft
     */
    public void setIndicatorMarginLeft(float indicatorMarginLeft) {
        this.indicatorMarginLeft = dpToPx(indicatorMarginLeft);
    }

    /**
     * 设置指示器之间的上间距
     *
     * @param indicatorMarginTop
     */
    public void setIndicatorMarginTop(float indicatorMarginTop) {
        this.indicatorMarginTop = indicatorMarginTop;
    }

    /**
     * 设置指示器之间的右间距
     *
     * @param indicatorMarginRight
     */
    public void setIndicatorMarginRight(float indicatorMarginRight) {
        this.indicatorMarginRight = indicatorMarginRight;
    }

    /**
     * 设置指示器之间的下间距
     *
     * @param indicatorMarginBottom
     */
    public void setIndicatorMarginBottom(float indicatorMarginBottom) {
        this.indicatorMarginBottom = indicatorMarginBottom;
    }

    /**
     * 设置指示器的位置
     *
     * @param gravity
     */
    public void setIndicatorGravity(int gravity) {
        this.indicatorGravity = gravity;
    }

    /**
     * 是否自动播放
     *
     * @return
     */
    public boolean isAutoPlay() {
        return isAutoPlay;
    }

    /**
     * 设置自动播放
     *
     * @param autoPlay
     */
    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
    }

    /**
     * 获取Pager对象
     *
     * @return
     */
    public ViewPager getPager() {
        return pager;
    }

    /**
     * 获取数据适配器
     *
     * @return
     */
    public BannerAdapter getAdapter() {
        return adapter;
    }

    public boolean isLoop() {
        return isLoop;
    }

    /**
     * 设置轮播时间
     *
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public static float dpToPx(float dp) {
        return dp * getScreenDensity();
    }

    public static float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    public interface OnPageChangeListener {
        /**
         * This method will be invoked when the current page is scrolled, either as part
         * of a programmatically initiated smooth scroll or a user initiated touch scroll.
         *
         * @param position             Position index of the first page currently being displayed.
         *                             Page position+1 will be visible if positionOffset is nonzero.
         * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
         * @param positionOffsetPixels Value in pixels indicating the offset from position.
         */
        void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        /**
         * This method will be invoked when a new page becomes selected. Animation is not
         * necessarily complete.
         *
         * @param position Position index of the new selected page.
         */
        void onPageSelected(int position);

        /**
         * Called when the scroll state changes. Useful for discovering when the user
         * begins dragging, when the pager is automatically settling to the current page,
         * or when it is fully stopped/idle.
         *
         * @param state The new scroll state.
         * @see ViewPager#SCROLL_STATE_IDLE
         * @see ViewPager#SCROLL_STATE_DRAGGING
         * @see ViewPager#SCROLL_STATE_SETTLING
         */
        void onPageScrollStateChanged(int state);
    }

}
