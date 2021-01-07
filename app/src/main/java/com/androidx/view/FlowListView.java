package com.androidx.view;

/**
 * Author: Relin
 * Description:流式布局列表
 * Date:2020/12/26 17:51
 */

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.androidx.R;
import com.androidx.util.Log;
import com.androidx.util.Screen;

public class FlowListView extends ViewGroup {

    public final static String TAG = FlowListView.class.getSimpleName();

    /**
     * 数据适配器
     */
    private BaseAdapter baseAdapter;
    /**
     * 竖向间距
     */
    private int verticalSpacing = (int) (8* Resources.getSystem().getDisplayMetrics().density);
    /**
     * 横向间距
     */
    private int horizontalSpacing = (int) (8* Resources.getSystem().getDisplayMetrics().density);
    /**
     * 数据监察者
     */
    private AdapterDataSetObserver adapterDataSerObserver;

    public FlowListView(Context context) {
        super(context);
        onStyledAttributes(context, null);
    }

    public FlowListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onStyledAttributes(context, attrs);
    }

    public FlowListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onStyledAttributes(context, attrs);
    }

    /**
     * 初始化Attrs.xml中的数据
     *
     * @param context 上下文
     * @param attrs   属性
     */
    protected void onStyledAttributes(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlowListView);
            horizontalSpacing = array.getDimensionPixelSize(R.styleable.FlowListView_android_horizontalSpacing, horizontalSpacing);
            verticalSpacing = array.getDimensionPixelSize(R.styleable.FlowListView_android_verticalSpacing, verticalSpacing);
            array.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int requiredWidthHeight[] = measureRequiredWidthHeight(widthMeasureSpec, heightMeasureSpec);
        int wh[] = measureRequiredWidthHeight(widthMeasureSpec, heightMeasureSpec, requiredWidthHeight[0], requiredWidthHeight[1]);
        setMeasuredDimension(wh[0], wh[1]);
    }

    /**
     * 测量需要的宽高
     *
     * @param widthMeasureSpec  宽度类型
     * @param heightMeasureSpec 高度类型
     * @param requiredWidth     需要宽度
     * @param requiredHeight    需要高度
     * @return
     */
    protected int[] measureRequiredWidthHeight(int widthMeasureSpec, int heightMeasureSpec, int requiredWidth, int requiredHeight) {
        int measureSpecWidth = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureSpecHeight = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = measureSpecWidth;
        int measureHeight = measureSpecHeight;
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            measureWidth = requiredWidth;
            measureHeight = requiredHeight;
        } else if (widthSpecMode == MeasureSpec.AT_MOST || widthSpecMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = requiredWidth;
            measureHeight = measureSpecHeight;
        } else if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            measureWidth = measureSpecWidth;
            measureHeight = requiredHeight;
        }
        Log.i(TAG, "->measureRequiredWidthHeight measureWidth=" + measureWidth + ",measureHeight=" + measureHeight);
        return new int[]{measureWidth, measureHeight};
    }

    /**
     * 测试需要的宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     * @return
     */
    private int[] measureRequiredWidthHeight(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("RRL", "->measureRequiredWidthHeight");
        int requiredHeight = 0, rowWidth = 0, rowPosition = 0;
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            int itemWidth = childWidth + mlp.leftMargin + mlp.rightMargin + horizontalSpacing;
            int itemHeight = childHeight + mlp.topMargin + mlp.bottomMargin + verticalSpacing;
            rowWidth += itemWidth;
            if (rowWidth > measureWidth) {
                rowWidth = itemWidth;
                rowPosition++;
            }
            requiredHeight = itemHeight * (1 + rowPosition);
        }
        if (getChildCount() > 0) {
            requiredHeight -= verticalSpacing;
        }
        Log.i(TAG, "->measureWidth=" + measureWidth + ",requiredHeight=" + requiredHeight);
        return new int[]{measureWidth, requiredHeight};
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "->onLayout");
        int flowWidth = getWidth();
        int childLeft = 0;
        int childTop = 0;
        int rowPosition = 0;
        //遍历子控件，记录每个子view的位置
        for (int i = 0, childCount = getChildCount(); i < childCount; i++) {
            View childView = getChildAt(i);
            //跳过View.GONE的子View
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            //获取到测量的宽和高
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            //因为子View可能设置margin，这里要加上margin的距离
            MarginLayoutParams mlp = (MarginLayoutParams) childView.getLayoutParams();
            mlp.rightMargin = horizontalSpacing;
            mlp.bottomMargin = verticalSpacing;
            if (childLeft + mlp.leftMargin + childWidth + mlp.rightMargin > flowWidth) {
                rowPosition++;
                //换行处理
                childTop += (mlp.topMargin + childHeight + mlp.bottomMargin);
                childLeft = 0;
            }
            //布局
            int left = childLeft + mlp.leftMargin;
            int top = childTop + mlp.topMargin;
            int right = childLeft + mlp.leftMargin + childWidth;
            int bottom = childTop + mlp.topMargin + childHeight;
            childView.layout(left, top, right, bottom);
            childLeft += (mlp.leftMargin + childWidth + mlp.rightMargin);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 重新加载刷新数据
     */
    private void notifyDataRefresh() {
        removeAllViews();
        for (int i = 0; i < baseAdapter.getCount(); i++) {
            final View childView = baseAdapter.getView(i, null, this);
            addView(childView, childView.getLayoutParams());
            if (onItemClickListener != null) {//Item点击事件
                final int position = i;
                childView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(baseAdapter, childView, position);
                    }
                });
            }
        }
        requestLayout();
    }

    /**
     * Adapter数据监察者
     */
    public class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataRefresh();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }

    private boolean isRegisterDataSetObserver;

    /**
     * 设置诗句适配器
     *
     * @param adapter
     */
    public void setAdapter(BaseAdapter adapter) {
        if (!isRegisterDataSetObserver && adapter != null) {
            adapterDataSerObserver = new AdapterDataSetObserver();
            adapter.registerDataSetObserver(adapterDataSerObserver);
            isRegisterDataSetObserver = true;
        }
        this.baseAdapter = adapter;
    }


    /**
     * Item点击事件
     */
    private OnItemClickListener onItemClickListener;

    /**
     * 设置Item点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        /**
         * Item点击
         *
         * @param adapter  数据适配器
         * @param view     视图
         * @param position 位置
         */
        void onItemClick(BaseAdapter adapter, View view, int position);
    }

}


