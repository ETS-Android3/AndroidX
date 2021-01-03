package com.androidx.util;

/**
 * Created by Relin on 2017/7/18.
 * 全屏沉寖式-解决键盘遮挡输入框
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import static android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN;

/**
 * 软键盘
 */
public class SoftKeyboard {

    private Activity activity;
    private View childOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private SoftKeyboard(Activity activity) {
        this.activity = activity;
        init(activity);
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param input
     */
    public void hide(Context context, EditText input) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    /**
     * 显示键盘
     *
     * @param context
     * @param input
     */
    public void show(Context context, EditText input) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, 0);
    }

    /**
     * Activity的根视图
     */
    private View rootView;
    /**
     * 纪录根视图的显示高度
     */
    private int rootViewVisibleHeight;

    private OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener;

    /**
     * 设置软键盘监听
     * @param onSoftKeyBoardChangeListener
     */
    public void setOnSoftKeyBoardChangeListener(OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
        this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener;
    }

    public interface OnSoftKeyBoardChangeListener {

        void onSoftKeyBoardShow(int height);

        void onSoftKeyBoardHide(int height);
    }

    private void init(Activity activity){
        FrameLayout content = activity.findViewById(android.R.id.content);
        childOfContent = content.getChildAt(0);
        childOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                globalLayout();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) childOfContent.getLayoutParams();
        addOnGlobalLayoutListener();
    }

    private void globalLayout() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = childOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                frameLayoutParams.height = usableHeightSansKeyboard;
            }
            childOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        childOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - (isFullScreen() ? 0 : StatusBar.height(activity)));
    }

    private boolean isFullScreen() {
        int val = activity.getWindow().getAttributes().flags;
        if (val != FLAG_FULLSCREEN) {
            return true;
        } else {
            return false;
        }
    }

    private void addOnGlobalLayoutListener() {
        rootView = activity.getWindow().getDecorView();
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                rootView.getWindowVisibleDisplayFrame(rect);
                int visibleHeight = rect.height();
                if (rootViewVisibleHeight == 0) {
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }
                if (rootViewVisibleHeight == visibleHeight) {
                    return;
                }
                if (rootViewVisibleHeight - visibleHeight > 200) {
                    if (onSoftKeyBoardChangeListener != null) {
                        onSoftKeyBoardChangeListener.onSoftKeyBoardShow(rootViewVisibleHeight - visibleHeight);
                    }
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }
                if (visibleHeight - rootViewVisibleHeight > 200) {
                    if (onSoftKeyBoardChangeListener != null) {
                        onSoftKeyBoardChangeListener.onSoftKeyBoardHide(visibleHeight - rootViewVisibleHeight);
                    }
                    rootViewVisibleHeight = visibleHeight;
                    return;
                }

            }
        });
    }

}
