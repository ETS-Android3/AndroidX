package com.androidx.util;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/**
 * Author: Relin
 * Description:拖拽助手
 * Date:2020/12/26 17:51
 */
public class DragHelper {

    private Context context;
    private View view;
    private int width;
    private int height;
    private int maxWidth;
    private int maxHeight;
    private float downX;
    private float downY;
    private boolean drag = false;
    private int l = maxWidth - width * 2;
    private int t = maxHeight - width * 2;
    private int r = maxWidth - width;
    private int b = maxHeight - width;

    public DragHelper(View view) {
        this.view = view;
        context = view.getContext();
    }

    /**
     * 设置是否拖拽
     *
     * @param drag
     */
    public void setDrag(boolean drag) {
        this.drag = drag;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        int resourceId = view.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return view.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 获取导航栏高度
     *
     * @return
     */
    public int getNavigationBarHeight() {
        int rid = view.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid != 0) {
            int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        } else
            return 0;
    }

    /**
     * 测量
     *
     * @param widthMeasureSpec  宽度类型
     * @param heightMeasureSpec 高度类型
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = view.getMeasuredWidth();
        height = view.getMeasuredHeight();
        maxWidth = Screen.width();
        maxHeight = Screen.height() - getStatusBarHeight();
    }

    /**
     * 触摸事件
     * @param event 事件
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (view.isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    drag = false;
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    final float moveX = event.getX() - downX;
                    final float moveY = event.getY() - downY;
                    int l, r, t, b;
                    if (Math.abs(moveX) > 3 || Math.abs(moveY) > 3) {
                        l = (int) (view.getLeft() + moveX);
                        r = l + width;
                        t = (int) (view.getTop() + moveY);
                        b = t + height;
                        if (l < 0) {
                            l = 0;
                            r = l + width;
                        } else if (r > maxWidth) {
                            r = maxWidth;
                            l = r - width;
                        }
                        if (t < 0) {
                            t = 0;
                            b = t + height;
                        } else if (b > maxHeight) {
                            b = maxHeight;
                            t = b - height;
                        }
                        this.l = l;
                        this.t = t;
                        this.r = r;
                        this.b = b;
                        view.layout(l, t, r, b);
                        drag = true;
                    } else {
                        drag = false;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return true;
        }
        return false;
    }

}
