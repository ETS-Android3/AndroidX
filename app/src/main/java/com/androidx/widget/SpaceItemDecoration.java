package com.androidx.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.androidx.util.Log;


/**
 * Author: Relin
 * Describe:RecycleView Item 间距类
 * Date:2016/5/18.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 间隔
     */
    private int space;

    /**
     * 方向
     */
    private int orientation;

    /**
     * 构造函数
     *
     * @param orientation 方向 LinearLayoutManager.HORIZONTAL or LinearLayoutManager.VERTICAL
     * @param space
     */
    public SpaceItemDecoration(int orientation, int space) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalArgumentException("请传入正确的参数");
        }
        this.orientation = orientation;
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        Log.i("RRL", "->isPreLayout=" + state.isPreLayout() + ",isMeasuring=" + state.isMeasuring());
        int itemCount = parent.getAdapter().getItemCount();
        int itemPosition = parent.getChildLayoutPosition(view);
        int lastItemIndex = itemCount == 0 ? 0 : (itemCount - 1);
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager == null) {
            throw new IllegalArgumentException("SpaceItemDecoration need to set after setLayoutManager()");
        }
        if (manager.getClass() == LinearLayoutManager.class) {
            if (orientation == LinearLayoutManager.VERTICAL) {
                if (itemPosition != lastItemIndex) {
                    outRect.set(0, 0, 0, space);
                }
            }
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                if (itemPosition != lastItemIndex) {
                    outRect.set(0, 0, space, 0);
                }
            }
        }
        if (manager.getClass() == GridLayoutManager.class) {
            int spanCount = ((GridLayoutManager) manager).getSpanCount();
            if (orientation == LinearLayoutManager.VERTICAL) {
                if (itemPosition > spanCount - 1) {
                    outRect.set(0, space, 0, 0);
                }
            }
            if (orientation == LinearLayoutManager.HORIZONTAL) {
                if (itemPosition % spanCount != (spanCount - 1)) {
                    outRect.set(0, 0, space, 0);
                }
            }
        }
        if (manager.getClass() == StaggeredGridLayoutManager.class) {
            int top = 0;
            int left = space / 2;
            int right = space / 2;
            int bottom = space;
            if (itemPosition == 0) {
                top = space;
            }
            outRect.set(left, top, right, bottom);
        }
    }
}
