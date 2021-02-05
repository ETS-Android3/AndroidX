package com.androidx.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidx.R;
import com.androidx.view.LoadingView;

/**
 * Author: Relin
 * Describe:数据请求布局
 * Date:2020/12/21 20:24
 */
public class SwipeRequestLayout extends SwipeLayout {

    /**
     * 头部
     */
    private LoadingView header;
    /**
     * 脚部
     */
    private LoadingView footer;
    /**
     * 头部Loading宽度
     */
    private int headerLoadingWidth;
    /**
     * 头部Loading高度
     */
    private int headerLoadingHeight;
    /**
     * 脚部Loading宽度
     */
    private int footerLoadingWidth;
    /**
     * 脚部Loading高度
     */
    private int footerLoadingHeight;
    /**
     * 脚部Loading文字
     */
    private String footerLoadingText;
    /**
     * 脚部Loading文字大小
     */
    private int footerLoadingTextSize;


    public SwipeRequestLayout(@NonNull Context context) {
        super(context);
    }

    public SwipeRequestLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeRequestLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttributeSet(Context context, AttributeSet attrs) {
        super.onAttributeSet(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SwipeRequestLayout);
        headerLoadingWidth = array.getDimensionPixelOffset(R.styleable.SwipeRequestLayout_footerLoadingWidth, 120);
        headerLoadingHeight = array.getDimensionPixelOffset(R.styleable.SwipeRequestLayout_headerLoadingHeight, 120);
        footerLoadingWidth = array.getDimensionPixelOffset(R.styleable.SwipeRequestLayout_footerLoadingWidth, LayoutParams.WRAP_CONTENT);
        footerLoadingHeight = array.getDimensionPixelOffset(R.styleable.SwipeRequestLayout_footerLoadingHeight,  LayoutParams.WRAP_CONTENT);
        footerLoadingText = array.getString(R.styleable.SwipeRequestLayout_footerLoadingText);
        footerLoadingText = footerLoadingText == null ? context.getResources().getString(R.string.swipe_layout_load_more) : footerLoadingText;
        footerLoadingTextSize = array.getDimensionPixelSize(R.styleable.SwipeRequestLayout_footerLoadingTextSize,12);
        array.recycle();
    }

    @Override
    protected View onCreateHeader(Context context) {
        //头部父级
        LinearLayout headerParent = new LinearLayout(getContext());
        headerParent.setOrientation(LinearLayout.HORIZONTAL);
        headerParent.setGravity(Gravity.CENTER);
        //LoadingView
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(headerLoadingWidth, headerLoadingHeight);
        header = new LoadingView(context);
        header.setBackgroundResource(R.drawable.android_swipe_header_loading_background);
        header.setStreakColor(getResources().getColor(R.color.colorSwipeHeaderLoadingStreak));
        header.setLayoutParams(headerParams);
        headerParent.addView(header);
        return headerParent;
    }

    @Override
    protected View onCreateFooter(Context context) {
        //脚部父级控件
        LinearLayout footerParent = new LinearLayout(getContext());
        footerParent.setOrientation(LinearLayout.HORIZONTAL);
        footerParent.setGravity(Gravity.CENTER);
        //脚步控件
        footer = new LoadingView(context);
        LayoutParams footerParams = new LayoutParams(footerLoadingWidth, footerLoadingHeight);
        footer.setLayoutParams(footerParams);
        footer.setBackgroundResource(R.drawable.android_swipe_footer_loading_background);
        footer.setStreakColor(getResources().getColor(R.color.colorSwipeFooterLoadingStreak));
        footer.setTextColor(getResources().getColor(R.color.colorSwipeFooterLoadingText));
        footer.setText(footerLoadingText);
        footer.setTextSize(footerLoadingTextSize);
        footer.setOrientation(LoadingView.HORIZONTAL);
        footerParent.addView(footer);
        return footerParent;
    }

    @Override
    protected void onHeaderAnimationStart() {
        if (header != null) {
            header.start();
        }
    }

    @Override
    protected void onHeaderAnimationStop() {
        if (header != null) {
            header.cancel();
        }
    }

    @Override
    protected void onFooterAnimationStart() {
        if (footer != null) {
            footer.start();
        }
    }

    @Override
    protected void onFooterAnimationStop() {
        if (footer != null) {
            footer.cancel();
        }
    }

}
