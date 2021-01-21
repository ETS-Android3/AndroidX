package com.androidx.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androidx.R;
import com.androidx.util.StatusBar;

/**
 * Author: Relin
 * Describe:核心标题栏
 * Date:2020/12/13 15:32
 */
public class NavigationBar implements View.OnClickListener {

    /**
     * 文本返回控件
     */
    public final static int BACK_TEXT = 1;
    /**
     * 图片返回控件
     */
    public final static int BACK_IMAGE = 2;
    /**
     * 文字标题
     */
    public final static int TITLE_TEXT = 3;
    /**
     * 文字菜单
     */
    public final static int MENU_TEXT = 4;
    /**
     * 图片菜单
     */
    public final static int MENU_IMAGE = 5;
    /**
     * 布局Id
     */
    private int layoutId;
    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 内容View
     */
    private View contentView;
    /**
     * ActionBar
     */
    private ActionBar actionBar;
    /**
     * Toolbar
     */
    private Toolbar toolbar;
    /**
     * 自定义的导航栏View
     */
    protected FrameLayout navigationView;
    /**
     * 图片返回控件
     */
    protected ImageView backImageView;
    /**
     * 文字返回控件
     */
    protected TextView backTextView;
    /**
     * 标题控件
     */
    protected TextView titleView;
    /**
     * 图片菜单控件
     */
    protected ImageView menuImageView;
    /**
     * 文字菜单图片
     */
    protected TextView menuTextView;
    /**
     * 是否是Activity
     */
    protected boolean isActivity;


    /**
     * 构建导航栏
     *
     * @param context    上下文
     * @param layoutId   布局id
     * @param isActivity 页面
     */
    public NavigationBar(Context context, int layoutId, boolean isActivity) {
        this.context = context;
        this.layoutId = layoutId;
        this.isActivity = isActivity;
        onLayoutInflater(context, layoutId);
    }

    /**
     * 布局参数
     *
     * @return
     */
    protected ActionBar.LayoutParams onActionBarLayoutParams() {
        return new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
    }

    /**
     * 创建导航栏
     *
     * @param context  上下文对象
     * @param layoutId 布局资源Id
     * @return
     */
    private void onLayoutInflater(Context context, int layoutId) {
        if (layoutId == 0) {
            return;
        }
        View contentView = LayoutInflater.from(context).inflate(layoutId, null, false);
        if (isActivity && context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            ActionBar.LayoutParams layoutParams = onActionBarLayoutParams();
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_HORIZONTAL;
            actionBar = activity.getSupportActionBar();
            if (actionBar!=null){
                //设置不显示底部阴影分割线
                actionBar.setElevation(0f);
                actionBar.setDisplayShowHomeEnabled(false);
                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setDisplayShowTitleEnabled(false);
                actionBar.setCustomView(contentView, layoutParams);
                toolbar = (Toolbar) contentView.getParent();
                toolbar.setContentInsetsAbsolute(0, 0);
            }
        }
        onCreate(contentView);
    }

    /**
     * 布局创建
     *
     * @param contentView 布局视图
     */
    protected void onCreate(View contentView) {
        this.contentView = contentView;
        navigationView = contentView.findViewById(R.id.navigation_bar);
        //查找View
        backImageView = contentView.findViewById(R.id.navigation_bar_back_icon);
        backTextView = contentView.findViewById(R.id.navigation_bar_back_text);
        titleView = contentView.findViewById(R.id.navigation_bar_title);
        menuImageView = contentView.findViewById(R.id.navigation_bar_menu_icon);
        menuTextView = contentView.findViewById(R.id.navigation_bar_menu_text);
        //设置点击事件
        backImageView.setOnClickListener(this);
        backTextView.setOnClickListener(this);
        titleView.setOnClickListener(this);
        menuImageView.setOnClickListener(this);
        menuTextView.setOnClickListener(this);
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
     * 设置上下文对象
     *
     * @param context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 获取自定义资源id
     *
     * @return
     */
    public int getLayoutId() {
        return layoutId;
    }

    /**
     * 设置自定义资源id
     *
     * @param layoutId
     */
    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
        onLayoutInflater(context, layoutId);
    }

    /**
     * 获取内容View
     *
     * @return
     */
    public View getContentView() {
        return contentView;
    }

    /**
     * 动作栏
     *
     * @return
     */
    public ActionBar getActionBar() {
        return actionBar;
    }

    /**
     * 工具栏
     *
     * @return
     */
    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * 隐藏
     */
    public void hide() {
        if (actionBar != null && actionBar.isShowing()) {
            actionBar.hide();
        }
        if (contentView != null) {
            ViewGroup parent = (ViewGroup) contentView.getParent();
            parent.setVisibility(View.GONE);
        }
    }

    /**
     * 是否显示
     *
     * @return
     */
    public boolean isShowing() {
        if (actionBar==null){
            return false;
        }
        return actionBar.isShowing();
    }

    /**
     * 显示
     */
    public void show() {
        if (actionBar != null && !actionBar.isShowing()) {
            actionBar.show();
        }
        if (contentView != null) {
            ViewGroup parent = (ViewGroup) contentView.getParent();
            parent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置背景颜色
     * @param color 颜色
     */
    public void setBackgroundColor(@ColorInt int color) {
        setBackgroundColor(color,true);
    }

    /**
     * 设置背景颜色
     *
     * @param color          颜色
     * @param applyStatusBar 状态栏是否应用
     */
    public void setBackgroundColor(@ColorInt int color, boolean applyStatusBar) {
        navigationView.setBackgroundColor(color);
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            setStatusBarTextColor(color == Color.WHITE || color == Color.TRANSPARENT);
            if (applyStatusBar) {
                StatusBar.setColor(activity, color);
            }
        }
    }

    /**
     * 设置状态栏文字颜色
     *
     * @param dark 是否黑色
     */
    public void setStatusBarTextColor(boolean dark) {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;
            StatusBar.setTextColor(activity, dark);
        }
    }

    /**
     * 设置背景颜色
     *
     * @param resId
     */
    public void setBackgroundResource(@ColorRes int resId) {
        int color = context.getResources().getColor(resId);
        setBackgroundColor(color);
    }

    /**
     * 设置背景颜色
     *
     * @param resId          颜色
     * @param applyStatusBar 状态栏是否应用
     */
    public void setBackgroundResource(@ColorRes int resId, boolean applyStatusBar) {
        int color = context.getResources().getColor(resId);
        setBackgroundColor(color,applyStatusBar);
    }

    /**
     * 设置背景颜色
     *
     * @param background
     */
    public void setBackgroundColor(Drawable background) {
        navigationView.setBackground(background);
    }

    /**
     * 设置返回资源
     *
     * @param resId
     */
    public void setBackResource(@DrawableRes int resId) {
        backImageView.setImageResource(resId);
    }

    /**
     * 设置返回图片
     *
     * @param drawable
     */
    public void setBackDrawable(Drawable drawable) {
        backImageView.setImageDrawable(drawable);
    }

    /**
     * 设置返回图片
     *
     * @param bm
     */
    public void setBackBitmap(Bitmap bm) {
        backImageView.setImageBitmap(bm);
    }

    /**
     * 设置返回内部间距
     *
     * @param padding
     */
    public void setBackPadding(int padding) {
        backImageView.setPadding(padding, padding, padding, padding);
    }

    /**
     * 设置返回内部间距
     *
     * @param left   左间距
     * @param top    上间距
     * @param right  右间距
     * @param bottom 底间距
     */
    public void setBackPadding(int left, int top, int right, int bottom) {
        backImageView.setPadding(left, top, right, bottom);
    }

    /**
     * 设置返回文字
     *
     * @param text
     */
    public void setBackText(CharSequence text) {
        backTextView.setText(text);
    }

    /**
     * 设置返回文字
     *
     * @param res
     */
    public void setBackText(@StringRes int res) {
        backTextView.setText(res);
    }

    /**
     * 设置返回颜色
     *
     * @param color
     */
    public void setBackTextColor(@ColorInt int color) {
        backTextView.setTextColor(color);
    }

    /**
     * 设置返回文字大小
     *
     * @param size
     */
    public void setBackTextSize(int size) {
        backTextView.setTextSize(size, TypedValue.COMPLEX_UNIT_SP);
    }

    /**
     * 设置返回文字大小
     *
     * @param size
     * @param unit
     */
    public void setBackTextSize(int size, int unit) {
        backTextView.setTextSize(size, unit);
    }

    /***
     * 设置返回文字内间距
     * @param padding
     */
    public void setBackTextPadding(int padding) {
        backTextView.setPadding(padding, padding, padding, padding);
    }

    /**
     * 设置返回文字内部间距
     *
     * @param left   左间距
     * @param top    上间距
     * @param right  右间距
     * @param bottom 底间距
     */
    public void setBackTextPadding(int left, int top, int right, int bottom) {
        backTextView.setPadding(left, top, right, bottom);
    }

    /**
     * 设置标题
     *
     * @param text
     */
    public void setTitle(CharSequence text) {
        titleView.setText(text);
    }

    /**
     * 设置标题
     *
     * @param res
     */
    public void setTitle(@StringRes int res) {
        titleView.setText(res);
    }

    /**
     * 设置标题文字
     *
     * @param size
     * @param unit
     */
    public void setTitleTextSize(int size, int unit) {
        titleView.setTextSize(size, unit);
    }

    /**
     * 设置标题文字大小
     *
     * @param size
     */
    public void setTitleTextSize(int size) {
        titleView.setTextSize(size, TypedValue.COMPLEX_UNIT_SP);
    }

    /**
     * 设置标题文字颜色
     *
     * @param color
     */
    public void setTitleTextColor(@ColorInt int color) {
        titleView.setTextColor(color);
    }

    /**
     * 设置标题文字间距
     *
     * @param padding
     */
    public void setTitleTextPadding(int padding) {
        titleView.setPadding(padding, padding, padding, padding);
    }

    /**
     * 设置标题文字间距
     *
     * @param left   左间距
     * @param top    上间距
     * @param right  右间距
     * @param bottom 底间距
     */
    public void setTitleTextPadding(int left, int top, int right, int bottom) {
        titleView.setPadding(left, top, right, bottom);
    }

    /**
     * 设置菜单资源
     *
     * @param resId
     */
    public void setMenuResource(@DrawableRes int resId) {
        menuImageView.setImageResource(resId);
    }

    /**
     * 设置菜单图片
     *
     * @param drawable
     */
    public void setMenuDrawable(Drawable drawable) {
        menuImageView.setImageDrawable(drawable);
    }

    /**
     * 设置菜单图片
     *
     * @param bm
     */
    public void setMenuBitmap(Bitmap bm) {
        menuImageView.setImageBitmap(bm);
    }

    /**
     * 设置菜单内间距
     *
     * @param padding
     */
    public void setMenuImagePadding(int padding) {
        menuImageView.setPadding(padding, padding, padding, padding);
    }

    /**
     * 设置菜单图片内间距
     *
     * @param left   左间距
     * @param top    上间距
     * @param right  右间距
     * @param bottom 底间距
     */
    public void setMenuImagePadding(int left, int top, int right, int bottom) {
        menuImageView.setPadding(left, top, right, bottom);
    }

    /**
     * 设置菜单文字
     *
     * @param text
     */
    public void setMenuText(CharSequence text) {
        menuTextView.setText(text);
    }

    /**
     * 设置菜单文字
     *
     * @param res
     */
    public void setMenuText(@StringRes int res) {
        menuTextView.setText(res);
    }

    /**
     * 设置菜单文字大小
     *
     * @param size
     * @param unit
     */
    public void setMenuTextSize(int size, int unit) {
        menuTextView.setTextSize(size, unit);
    }

    /**
     * 设置菜单文字大小
     *
     * @param size
     */
    public void setMenuTextSize(int size) {
        menuTextView.setTextSize(size, TypedValue.COMPLEX_UNIT_SP);
    }

    /**
     * 设置菜单文字颜色
     *
     * @param color
     */
    public void setMenuTextColor(@ColorInt int color) {
        menuTextView.setTextColor(color);
    }

    /**
     * 设置菜单文字内间距
     *
     * @param padding
     */
    public void setMenuTextPadding(int padding) {
        menuTextView.setPadding(padding, padding, padding, padding);
    }

    /**
     * 设置菜单文字内间距
     *
     * @param left   左间距
     * @param top    上间距
     * @param right  右间距
     * @param bottom 底间距
     */
    public void setMenuTextPadding(int left, int top, int right, int bottom) {
        menuTextView.setPadding(left, top, right, bottom);
    }

    @Override
    public void onClick(View v) {
        int operate = -1;
        if (v.getId() == R.id.navigation_bar_back_icon) {
            operate = BACK_IMAGE;
        }
        if (v.getId() == R.id.navigation_bar_back_text) {
            operate = BACK_TEXT;
        }
        if (v.getId() == R.id.navigation_bar_title) {
            operate = TITLE_TEXT;
        }
        if (v.getId() == R.id.navigation_bar_menu_icon) {
            operate = MENU_IMAGE;
        }
        if (v.getId() == R.id.navigation_bar_menu_text) {
            operate = MENU_TEXT;
        }
        if (onNavigationBarClickListener != null) {
            onNavigationBarClickListener.onNavigationBarClick(v, operate);
        }
    }

    /**
     * 导航栏点击事件
     */
    private OnNavigationBarClickListener onNavigationBarClickListener;

    /**
     * 设置导航栏点击事件
     *
     * @param onNavigationBarClickListener
     */
    public void setOnNavigationBarClickListener(OnNavigationBarClickListener onNavigationBarClickListener) {
        this.onNavigationBarClickListener = onNavigationBarClickListener;
    }

    public interface OnNavigationBarClickListener {

        void onNavigationBarClick(View v, int operate);

    }

}
