package com.androidx.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidx.R;
import com.androidx.util.Log;


/**
 * Created by Relin
 * on 2018-09-26.
 */
public abstract class SwipeLayout extends FrameLayout {

    /**
     * 调试模式
     */
    public static boolean DEBUG = true;
    /**
     * 日志标识
     */
    public String TAG = SwipeLayout.class.getSimpleName();
    /**
     * 下拉刷新
     */
    private int REFRESH = 1;
    /**
     * 加载更多
     */
    private int LOADING = -1;
    /**
     * 头部视图
     */
    private View headerView;
    /**
     * 内容视图
     */
    private View contentView[];
    /**
     * 底部视图
     */
    private View footerView;
    /**
     * 头部高度
     */
    private float headerHeight = dpToPx(60);
    /**
     * 脚部高度
     */
    private float footerHeight = dpToPx(40);
    /**
     * 刷新是否可用
     */
    private boolean refreshable = true;
    /**
     * 加载是否可用
     */
    private boolean loadable = true;
    /**
     * 是否超限距离
     */
    private boolean isTransfinite = false;
    /**
     * 是否正在刷新
     */
    private boolean isRefreshing = false;
    /**
     * 刷新释放
     */
    private boolean isRefreshingRelease = true;
    /**
     * 是否正在加载
     */
    private boolean isLoading = false;
    /**
     * 加载释放
     */
    private boolean isLoadingRelease = true;
    /**
     * 按下的坐标
     */
    private float downX, downY;
    /**
     * 刷新移动距离
     */
    private float refreshMoveY = 0;
    /**
     * 加载移动距离
     */
    private float loadMoveY = 0;
    /**
     * 刷新停留距离
     */
    private float refreshRemainY = 0;
    /**
     * 加载停留距离
     */
    private float loadRemainY = 0;
    /**
     * 刷新监听
     */
    private OnSwipeRefreshListener refreshListener;
    /**
     * 加载监听
     */
    private OnSwipeLoadListener loadListener;
    /**
     * 缩放动画
     */
    private ValueAnimator scaleAnimator;
    /**
     * 移动动画
     */
    private ValueAnimator translateAnimator;
    /**
     * 缩放时间
     */
    private int scaleDuration = 500;
    /**
     * 移动时间
     */
    private int translateDuration = 200;
    /**
     * 延迟时间
     */
    private int delayDuration = 1000;
    /**
     * 内容类型-列表
     */
    private AbsListView absListView;
    /**
     * 内容类型-ScrollView
     */
    private ScrollView scrollView;
    /**
     * 内容类型-RecyclerView
     */
    private RecyclerView recyclerView;
    private View emptyView;
    private NestedScrollView nestedScrollView;


    public SwipeLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public SwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SwipeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始话头部和底部
     *
     * @param context 上下文对象
     * @param attrs   属性
     */
    private void init(Context context, AttributeSet attrs) {
        initAttrs(context, attrs);
        setHeaderView(onCreateHeader(context));
        setFooterView(onCreateFooter(context));
    }

    /**
     * 初始化属性
     *
     * @param context 上下文对象
     * @param attrs   属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout);
            headerHeight = typedArray.getDimension(R.styleable.SwipeLayout_headerHeight, headerHeight);
            footerHeight = typedArray.getDimension(R.styleable.SwipeLayout_footerHeight, footerHeight);
            refreshable = typedArray.getBoolean(R.styleable.SwipeLayout_refreshable, refreshable);
            loadable = typedArray.getBoolean(R.styleable.SwipeLayout_loadable, loadable);
            delayDuration = typedArray.getInt(R.styleable.SwipeLayout_delayDuration, delayDuration);
            scaleDuration = typedArray.getInt(R.styleable.SwipeLayout_scaleDuration, scaleDuration);
            translateDuration = typedArray.getInt(R.styleable.SwipeLayout_translateDuration, translateDuration);
            onAttributeSet(context, attrs);
            typedArray.recycle();
        }
    }

    /**
     * 属性设置
     *
     * @param context 上下文对象
     * @param attrs   属性
     */
    protected void onAttributeSet(Context context, AttributeSet attrs) {

    }

    /**
     * 创建头部
     *
     * @param context
     */
    protected abstract View onCreateHeader(Context context);

    /**
     * 创建脚部
     *
     * @param context
     */
    protected abstract View onCreateFooter(Context context);

    /**
     * 设置头部View
     *
     * @param headerView
     */
    protected void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    /**
     * 获取头部View
     *
     * @return
     */
    public View getHeaderView() {
        return headerView;
    }

    /**
     * 设置脚部View
     *
     * @param footerView
     */
    protected void setFooterView(View footerView) {
        this.footerView = footerView;
    }

    /**
     * 获取脚部View
     *
     * @return
     */
    public View getFooterView() {
        return footerView;
    }

    /**
     * 开启头部动画
     */
    protected abstract void onHeaderAnimationStart();

    /**
     * 停止头部动画
     */
    protected abstract void onHeaderAnimationStop();

    /**
     * 开启头部动画
     */
    protected abstract void onFooterAnimationStart();

    /**
     * 停止头部动画
     */
    protected abstract void onFooterAnimationStop();


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = new View[getChildCount()];
        checkViewGroup(this, true);
        if (absListView != null && absListView.getVisibility() == VISIBLE) {
            absListView.setOnScrollListener(new AbsListViewOnScrollListener());
        }
        if (recyclerView != null) {
            recyclerView.addOnScrollListener(new RecyclerViewOnScrollListener());
        }
        //头部
        if (headerView != null) {
            LayoutParams headerParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) headerHeight);
            headerView.setLayoutParams(headerParams);
            headerView.setBackgroundColor(Color.TRANSPARENT);
            addView(headerView, headerParams);
        }
        //脚部
        if (footerView != null) {
            LayoutParams footerParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) footerHeight);
            footerView.setLayoutParams(footerParams);
            footerView.setBackgroundColor(Color.TRANSPARENT);
            footerParams.gravity = Gravity.BOTTOM;
            addView(footerView);
        }
    }

    /**
     * 是否滑动到顶部
     */
    private boolean isAbsListViewScrollTop = true;
    private boolean isRecyclerViewScrollTop = true;

    /**
     * 是否滑动到底部
     */
    private boolean isAbsListViewScrollBottom;
    private boolean isRecyclerViewScrollBottom;

    /**
     * 列表滑动事件
     */
    private class AbsListViewOnScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (emptyView != null) {
                emptyView.setVisibility(view.getCount() > 0 ? GONE : VISIBLE);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem == 0) {
                View first_view = view.getChildAt(0);
                if (first_view != null && first_view.getTop() == 0) {
                    isAbsListViewScrollTop = true;
                } else {
                    isAbsListViewScrollTop = false;
                }
            }
            if (firstVisibleItem + visibleItemCount == totalItemCount) {
                View last_view = view.getChildAt(view.getChildCount() - 1);
                if (last_view != null && last_view.getBottom() == view.getHeight()) {
                    isAbsListViewScrollBottom = true;
                } else {
                    isAbsListViewScrollBottom = false;
                }
            }
        }
    }

    private class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {


        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //检查向上滚动为负，检查向下滚动为正。
            isRecyclerViewScrollTop = !recyclerView.canScrollVertically(-1);
            isRecyclerViewScrollBottom = !recyclerView.canScrollVertically(1);
        }
    }

    /**
     * 检查ViewGroup是否有对应的滑动控件，处理多层嵌套时获取不到滑动控件
     *
     * @param parent ViewGroup
     * @param isInti 是否初始化
     */
    private void checkViewGroup(ViewGroup parent, boolean isInti) {
        if (parent != null && (scrollView == null || absListView == null)) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                if (parent.getChildAt(i) instanceof ScrollView) {
                    scrollView = (ScrollView) parent.getChildAt(i);
                }
                if (scrollView == null && parent.getChildAt(i) instanceof AbsListView) {
                    absListView = (AbsListView) parent.getChildAt(i);
                }
                if (parent.getChildAt(i) instanceof RecyclerView) {
                    recyclerView = (RecyclerView) parent.getChildAt(i);
                }
                if (parent.getChildAt(i) instanceof NestedScrollView) {
                    nestedScrollView = (NestedScrollView) parent.getChildAt(i);
                }
                if (isInti && i < contentView.length) {
                    contentView[i] = parent.getChildAt(i);
                }
                if (parent.getChildAt(i) instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) parent.getChildAt(i);
                    if (group != null) {
                        checkViewGroup(group, false);
                    }
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //头部
        if (headerView != null) {
            MarginLayoutParams headerParams = (MarginLayoutParams) headerView.getLayoutParams();
            left = getPaddingLeft() + headerParams.leftMargin;
            top = getPaddingTop() + headerParams.topMargin - headerView.getMeasuredHeight();
            top += (int) refreshRemainY + (int) refreshMoveY;
            right = left + headerView.getMeasuredWidth();
            bottom = top + headerView.getMeasuredHeight();
            headerView.layout(left, top, right, bottom);
        }
        //内容区域
        for (int i = 0; i < contentView.length; i++) {
            View child = contentView[i];
            MarginLayoutParams childParams = (MarginLayoutParams) child.getLayoutParams();
            left = getPaddingLeft() + childParams.leftMargin;
            top = getPaddingTop() + childParams.topMargin;
            //对对齐方式的控件处理
            if (child.getLayoutParams() instanceof LayoutParams) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                if (params.gravity == Gravity.RIGHT) {
                    left = getMeasuredWidth() - getPaddingLeft() - childParams.leftMargin - child.getMeasuredWidth();
                }
                if (params.gravity == Gravity.BOTTOM) {
                    top = getMeasuredHeight() - getPaddingTop() - childParams.topMargin - child.getMeasuredHeight();
                }
                if (params.gravity == Gravity.CENTER) {
                    left = getMeasuredWidth() / 2 - getPaddingLeft() - childParams.leftMargin - child.getMeasuredWidth() / 2;
                    top = getMeasuredHeight() / 2 - getPaddingTop() - childParams.topMargin - child.getMeasuredHeight() / 2;
                }
                if (params.gravity == Gravity.CENTER_VERTICAL) {
                    top = getMeasuredHeight() / 2 - getPaddingTop() - childParams.topMargin - child.getMeasuredHeight() / 2;
                }
                if (params.gravity == Gravity.CENTER_HORIZONTAL) {
                    left = getMeasuredWidth() / 2 - getPaddingLeft() - childParams.leftMargin - child.getMeasuredWidth() / 2;
                }
                if (params.gravity == Gravity.RIGHT + Gravity.CENTER_VERTICAL) {
                    left = getMeasuredWidth() - getPaddingLeft() - childParams.leftMargin - child.getMeasuredWidth();
                    top = getMeasuredHeight() / 2 - getPaddingTop() - childParams.topMargin - child.getMeasuredHeight() / 2;
                }
                if (params.gravity == Gravity.BOTTOM + Gravity.RIGHT) {
                    left = getMeasuredWidth() - getPaddingLeft() - childParams.leftMargin - child.getMeasuredWidth();
                    top = getMeasuredHeight() - getPaddingTop() - childParams.topMargin - child.getMeasuredHeight();
                }
            }
            top += (int) loadMoveY + (int) loadRemainY;
            right = left + child.getMeasuredWidth();
            bottom = top + child.getMeasuredHeight();
            child.layout(left, top, right, bottom);
        }
        //脚部
        if (footerView != null) {
            MarginLayoutParams footerParams = (MarginLayoutParams) footerView.getLayoutParams();
            left = getPaddingLeft() + footerParams.leftMargin;
            top = getMeasuredHeight() + getPaddingTop() + footerParams.topMargin;
            top += (int) loadRemainY + (int) loadMoveY;
            right = left + footerView.getMeasuredWidth();
            bottom = top + footerView.getMeasuredHeight();
            footerView.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX() - downX;
                float moveY = event.getY() - downY;
                if (Math.abs(moveY) <= Math.abs(moveX)||Math.abs(moveY)<10||Math.abs(moveX)<10) {
                    return super.onInterceptTouchEvent(event);
                }
                if (moveY > 0 && refreshable) {
                    return isContentViewRefreshEnable();
                }
                if (moveY < 0 && loadable) {
                    return isContentViewLoadEnable();
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (isRefreshingRelease) {
                    onHeaderAnimationStart();
                    if (refreshListener != null && refreshMoveY > 0 && refreshable && isTransfinite) {
                        refreshListener.onSwipeRefresh();
                        isRefreshingRelease = true;
                    }
                    if (isTransfinite) {
                        isRefreshing = true;
                        refreshRemainY = headerHeight;
                        refreshMoveY = 0;
                        requestLayout();
                    } else {
                        createTranslateAnimator(refreshMoveY, 0, REFRESH, 0).start();
                    }
                    return true;
                }
                if (isLoadingRelease) {
                    onFooterAnimationStart();
                    if (loadListener != null && loadMoveY < 0 && loadable && isTransfinite) {
                        loadListener.onSwipeLoad();
                        isLoadingRelease = true;
                    }
                    if (isTransfinite) {
                        isLoading = true;
                        loadRemainY = loadable ? -footerView.getMeasuredHeight() : 0;
                        loadMoveY = 0;
                        requestLayout();
                    } else {
                        createTranslateAnimator(loadMoveY, 0, LOADING, 0).start();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isLoading || isRefreshing) {
                    isRefreshingRelease = false;
                    isLoadingRelease = false;
                    return false;
                }
                float moveY = event.getY() - downY;
                if (Math.abs(moveY) < 50) {
                    break;
                }
                moveY *= 0.45F;
                if (Math.abs(moveY) < headerHeight / 5) {
                    break;
                }
                //下滑
                if (moveY > 0 && refreshable && !isLoading) {
                    isTransfinite = Math.abs(moveY) >= headerHeight;
                    if (headerView != null) {
                        headerView.setScaleX(1);
                        headerView.setScaleY(1);
                    }
                    onHeaderAnimationStop();
                    refreshMoveY = moveY;
                    isRefreshingRelease = true;
                    isLoadingRelease = false;
                    isAbsListViewScrollBottom = false;
                }
                //上滑
                if (moveY < 0 && loadable && !isRefreshing) {
                    isTransfinite = Math.abs(moveY) >= footerHeight;
                    if (footerView != null) {
                        footerView.setScaleX(1);
                        footerView.setScaleY(1);
                    }
                    onFooterAnimationStop();
                    loadMoveY = moveY;
                    isRefreshingRelease = false;
                    isLoadingRelease = true;
                }
                if (isRefreshingRelease || isLoadingRelease) {
                    requestLayout();
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (scaleAnimator != null) {
            scaleAnimator.removeAllUpdateListeners();
            scaleAnimator.cancel();
            scaleAnimator = null;
        }
    }

    public float dpToPx(float dp) {
        return dp * getScreenDensity();
    }

    public float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * 下拉刷新
     */
    public interface OnSwipeRefreshListener {
        /**
         * 下拉刷新
         */
        void onSwipeRefresh();
    }

    /**
     * 设置刷新监听
     *
     * @param refreshListener
     */
    public void setOnSwipeRefreshListener(OnSwipeRefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    /**
     * 上拉加载
     */
    public interface OnSwipeLoadListener {
        /**
         * 上拉加载
         */
        void onSwipeLoad();
    }

    /**
     * 设置加载监听
     *
     * @param loadListener
     */
    public void setOnSwipeLoadListener(OnSwipeLoadListener loadListener) {
        this.loadListener = loadListener;
    }

    /**
     * 是否正在刷新
     *
     * @return
     */
    public boolean isRefreshing() {
        return isRefreshing;
    }

    /**
     * 设置正在刷新
     *
     * @param isRefreshing 是否开始刷新
     */
    public void setRefreshing(boolean isRefreshing) {
        if (!isRefreshing) {
            if (!isRefreshing()) {
                return;
            }
            createScaleAnimator(headerView, REFRESH).start();
        } else {
            if (!refreshable) {
                return;
            }
            refreshRemainY = headerHeight;
            onHeaderAnimationStart();
            if (refreshListener != null) {
                refreshListener.onSwipeRefresh();
            }
        }
        this.isRefreshing = isRefreshing;
    }


    /**
     * 是否正在加载
     *
     * @return
     */
    public boolean isLoading() {
        return isLoading;
    }

    /**
     * 设置正在加载
     *
     * @param isLoading 是否开始加载
     */
    public void setLoading(boolean isLoading) {
        if (!isLoading) {
            if (!isLoading()) {
                return;
            }
            createTranslateAnimator(0, footerHeight, LOADING, delayDuration).start();
        } else {
            if (!loadable) {
                return;
            }
            onFooterAnimationStart();
            loadRemainY = -footerHeight;
            if (loadListener != null) {
                loadListener.onSwipeLoad();
            }
        }
        this.isLoading = isLoading;
    }

    /**
     * 创建位移动画
     *
     * @param startValue 开始值
     * @param endValue   结束值
     * @param type       类型{@link #REFRESH} or {@link #LOADING}
     * @param startDelay 开始延迟时间
     * @return
     */
    private ValueAnimator createTranslateAnimator(final float startValue, final float endValue, final int type, long startDelay) {
        if (translateAnimator != null && translateAnimator.isStarted() && translateAnimator.isRunning()) {
            translateAnimator.removeAllUpdateListeners();
            translateAnimator = null;
        }
        translateAnimator = ValueAnimator.ofFloat(startValue, endValue);
        translateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isTransfinite = false;
                if (type == REFRESH) {
                    isRefreshing = false;
                    refreshRemainY = 0;
                    refreshMoveY = 0;
                    isRefreshingRelease = true;
                    onHeaderAnimationStop();
                }
                if (type == LOADING) {
                    isLoading = false;
                    loadRemainY = 0;
                    loadMoveY = 0;
                    isLoadingRelease = true;
                    onFooterAnimationStop();
                }
                requestLayout();
            }
        });
        translateAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (type == REFRESH) {
                    refreshMoveY = value;
                }
                if (type == LOADING) {
                    loadMoveY = value;
                }
                requestLayout();
            }
        });
        translateAnimator.setDuration(translateDuration);
        translateAnimator.setStartDelay(startDelay);
        return translateAnimator;
    }

    /**
     * 创建缩放动画师
     *
     * @param view 控件
     * @param type 刷新 {@link #REFRESH} , 加载 {@link #LOADING}
     * @return
     */
    private synchronized ValueAnimator createScaleAnimator(final View view, final int type) {
        if (scaleAnimator != null && scaleAnimator.isStarted() && scaleAnimator.isRunning()) {
            scaleAnimator.removeAllUpdateListeners();
            scaleAnimator = null;
        }
        scaleAnimator = ValueAnimator.ofFloat(1, 0);
        scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                view.setScaleX(value);
                view.setScaleY(value);
            }
        });
        scaleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isTransfinite = false;
                if (type == REFRESH) {
                    isRefreshing = false;
                    refreshRemainY = 0;
                    refreshMoveY = 0;
                    onHeaderAnimationStop();
                    isRefreshingRelease = true;
                }
                if (type == LOADING) {
                    isLoading = false;
                    loadRemainY = 0;
                    loadMoveY = 0;
                    isLoadingRelease = true;
                    onFooterAnimationStop();
                }
                requestLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleAnimator.setStartDelay(refreshable ? delayDuration : 0);
        scaleAnimator.setDuration(scaleDuration);
        return scaleAnimator;
    }

    /**
     * 内容去是否可以刷新
     *
     * @return
     */
    private boolean isContentViewRefreshEnable() {
        if (scrollView != null && scrollView.getScrollY() > 0) {
            return false;
        }
        if (nestedScrollView != null && nestedScrollView.getScrollY() > 0) {
            return false;
        }
        if (absListView != null) {
            if (absListView.getAdapter().getCount() == 0) {
                return true;
            }
            return isAbsListViewScrollTop;
        }
        if (recyclerView != null) {
            if (recyclerView.getChildCount() == 0) {
                return true;
            }
            return isRecyclerViewScrollTop;
        }
        return true;
    }

    /**
     * 内容区域是否可以加载
     *
     * @return
     */
    private boolean isContentViewLoadEnable() {
        if (scrollView != null) {
            if ((scrollView.getScrollY() + scrollView.getHeight()) >= scrollView.getChildAt(0).getMeasuredHeight()) {
                return true;
            }
            return false;
        }
        if (nestedScrollView != null) {
            if ((nestedScrollView.getScrollY() + nestedScrollView.getHeight()) >= nestedScrollView.getChildAt(0).getMeasuredHeight()) {
                return true;
            }
            return false;
        }
        if (absListView != null) {
            if (absListView.getAdapter().getCount() == 0) {
                return false;
            }
            int lastPosition = absListView.getLastVisiblePosition();
            int count = absListView.getAdapter().getCount();
            if (lastPosition != count - 1) {
                return false;
            }
            return isAbsListViewScrollBottom && (isRefreshingRelease || isLoadingRelease);
        }
        if (recyclerView != null) {
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (manager.findLastCompletelyVisibleItemPosition() != recyclerView.getAdapter().getItemCount() - 1) {
                    isRecyclerViewScrollBottom = false;
                }
            }
            return isRecyclerViewScrollBottom && (isRefreshingRelease || isLoadingRelease);
        }
        return true;
    }

    /**
     * 设置头部高度
     *
     * @param headerHeight
     */
    public void setHeaderHeight(float headerHeight) {
        this.headerHeight = headerHeight;
        requestLayout();
    }

    /**
     * 设置脚部高度
     *
     * @param footerHeight
     */
    public void setFooterHeight(float footerHeight) {
        this.footerHeight = footerHeight;
        requestLayout();
    }

    /**
     * 只是是否可刷新
     *
     * @param refreshable
     */
    public void setRefreshable(boolean refreshable) {
        this.refreshable = refreshable;
    }

    /**
     * 设置是否加载更多
     *
     * @param loadable
     */
    public void setLoadable(boolean loadable) {
        this.loadable = loadable;
    }

    /**
     * 设置缩放持续时间
     *
     * @param scaleDuration
     */
    public void setScaleDuration(int scaleDuration) {
        this.scaleDuration = scaleDuration;
    }

    /**
     * 设置延迟持续时间
     *
     * @param delayDuration
     */
    public void setDelayDuration(int delayDuration) {
        this.delayDuration = delayDuration;
    }

}
