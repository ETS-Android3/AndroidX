package com.androidx.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidx.R;
import com.androidx.util.Log;
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
    /**
     * 脚部文字
     */
    private TextView footerText;


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
        headerLoadingWidth = array.getDimensionPixelOffset(R.styleable.SwipeRequestLayout_footerLoadingWidth, 100);
        headerLoadingHeight = array.getDimensionPixelOffset(R.styleable.SwipeRequestLayout_headerLoadingHeight, 100);
        footerLoadingWidth = array.getDimensionPixelOffset(R.styleable.SwipeRequestLayout_footerLoadingWidth, 40);
        footerLoadingHeight = array.getDimensionPixelOffset(R.styleable.SwipeRequestLayout_footerLoadingHeight, 40);
        footerLoadingText = array.getString(R.styleable.SwipeRequestLayout_footerLoadingText);
        footerLoadingText = footerLoadingText == null ? context.getResources().getString(R.string.swipe_layout_load_more) : footerLoadingText;
        footerLoadingTextSize = array.getDimensionPixelSize(R.styleable.SwipeRequestLayout_footerLoadingTextSize, 12);
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
        header.setPadding(20, 20, 20, 20);
        headerParent.addView(header);
        return headerParent;
    }

    @Override
    protected View onCreateFooter(Context context) {
        //脚部父级控件
        LinearLayout footerParent = new LinearLayout(getContext());
        footerParent.setOrientation(LinearLayout.HORIZONTAL);
        footerParent.setGravity(Gravity.CENTER);
        //脚部控件
        footer = new LoadingView(context);
        LayoutParams footerParams = new LayoutParams(footerLoadingWidth, footerLoadingHeight);
        footer.setLayoutParams(footerParams);
        footer.setStreakWidth(dpToPx(1));
        footer.setUnitAngle(15);
        footer.setStreakLength(dpToPx(2));
        footer.setBackgroundResource(R.drawable.android_swipe_footer_loading_background);
        footer.setStreakColor(getResources().getColor(R.color.colorSwipeFooterLoadingStreak));
        footerParent.addView(footer);
        MarginLayoutParams marginParams = (MarginLayoutParams) footer.getLayoutParams();
        marginParams.rightMargin = (int) dpToPx(5);
        //文字
        footerText = new TextView(getContext());
        footerText.setTextColor(getResources().getColor(R.color.colorSwipeFooterLoadingStreak));
        footerText.setText(footerLoadingText);
        footerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, footerLoadingTextSize);
        footerParent.addView(footerText);
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
        if (footer != null&&footer.getVisibility()==VISIBLE) {
            footer.start();
        }
    }

    @Override
    protected void onAdapterChange(boolean change) {
        Log.i(TAG,"->onAdapterChange change="+change);
        if (change){
            footer.setVisibility(VISIBLE);
            footerText.setText(getContext().getResources().getString(R.string.swipe_layout_load_more));
        }else{
            footer.setVisibility(GONE);
            footerText.setText(getContext().getResources().getString(R.string.swipe_layout_load_more_disable));
        }
    }

    @Override
    protected void onFooterAnimationStop() {
        if (footer != null) {
            footer.cancel();
        }
    }

}
