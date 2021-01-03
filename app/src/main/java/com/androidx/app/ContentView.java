package com.androidx.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Author: Relin
 * Describe:内容视图
 * Date:2020/12/13 15:32
 */
public class ContentView {

    /**
     * 布局资源id
     */
    private int layoutId;
    /**
     * 上下文
     */
    private Context context;
    /**
     * 内容View
     */
    private View contentView;

    /**
     * 创建
     *
     * @param context  上下文
     * @param layoutId 内容View id
     */
    public ContentView(Context context, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
        onCreate(LayoutInflater.from(context).inflate(layoutId, null));
    }

    /**
     * 创建
     *
     * @param contentView 内容
     */
    public void onCreate(View contentView) {
        this.contentView = contentView;
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取布局id
     *
     * @return
     */
    public int getLayoutId() {
        return layoutId;
    }

    /**
     * 设置布局id
     *
     * @param layoutId
     */
    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
        onCreate(LayoutInflater.from(context).inflate(layoutId, null));
    }

    /**
     * 获取内容视图View
     *
     * @return
     */
    public View getContentView() {
        return contentView;
    }

    /**
     * 设置内容视图View
     *
     * @param contentView
     */
    public void setContentView(View contentView) {
        this.contentView = contentView;
    }
}
