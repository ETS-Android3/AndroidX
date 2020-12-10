package com.android.view;

/**
 * Created by Ice on 2016/11/29.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.R;
import com.android.utils.Log;
import com.android.utils.Screen;


public class FlowListView extends ViewGroup {

    //数据适配器
    private BaseAdapter baseAdapter;
    //竖向间距
    private int verticalSpacing = (int) Screen.dpToPx(10);
    //横向间距
    private int horizontalSpacing = (int) Screen.dpToPx(10);
    //数据监察者
    private AdapterDataSetObserver adapterDataSerObserver;
    private int requiredWidth = 0;
    private int requiredHeight = 0;

    public FlowListView(Context context) {
        super(context);
    }

    public FlowListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public FlowListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    /**
     * 初始化Attrs.xml中的数据
     *
     * @param context
     * @param attrs
     */
    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlowListView);
        horizontalSpacing = array.getDimensionPixelSize(R.styleable.FlowListView_horizontalSpacing, horizontalSpacing);
        verticalSpacing = array.getDimensionPixelSize(R.styleable.FlowListView_verticalSpacing, verticalSpacing);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int requiredWidthHeight[] = measureRequiredWidthHeight(widthMeasureSpec, heightMeasureSpec);
        int wh[] = measureRequiredWidthHeight(widthMeasureSpec,heightMeasureSpec,requiredWidthHeight[0],requiredWidthHeight[1]);
        setMeasuredDimension(wh[0], wh[1]);
    }

    private int[] measureRequiredWidthHeight(int widthMeasureSpec, int heightMeasureSpec, int requiredWidth, int requiredHeight) {
        int measureSpecWidth = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureSpecHeight = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = measureSpecWidth;
        int measureHeight = measureSpecHeight;
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            measureWidth = requiredWidth;
            measureHeight = requiredHeight;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            measureWidth = requiredWidth;
            measureHeight = measureSpecHeight;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            measureWidth = measureSpecWidth;
            measureHeight = requiredHeight;
        }
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
        int requiredHeight = 0;
        int rowWidth = 0;
        int rowPosition = 0;
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
                rowPosition++;
                rowWidth = 0;
            }
            requiredHeight = itemHeight * (1 + rowPosition);
        }
        return new int[]{measureWidth, requiredHeight};
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int flowWidth = getWidth();
        int childLeft = 0;
        int childTop = 0;
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
     * 像ListView、GridView一样使用FlowLayout
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


    //Item 点击时间回调函数

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(BaseAdapter adapter, View view, int position);
    }

}


