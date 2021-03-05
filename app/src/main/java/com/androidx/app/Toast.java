package com.androidx.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

import com.androidx.R;
import com.androidx.view.StatusView;

/**
 * Author: Relin
 * Describe:核心Toast
 * Date:2020/12/13 15:31
 */
public class Toast {

    /**
     * 上下文
     */
    private Context context;
    /**
     * 类型
     */
    private int type;
    /**
     * 状态
     */
    private int status;
    /**
     * 对齐方式
     */
    private int gravity = Gravity.BOTTOM;
    /**
     * 内容视图
     */
    private View contentView;
    /**
     * 提示布局
     */
    private LinearLayout layout;
    /**
     * 提示状态View
     */
    private StatusView statusView;
    /**
     * 提示文字View
     */
    private TextView textView;
    /**
     * 偏移X
     */
    private int xOffset = 0;
    /**
     * 偏移Y
     */
    private int yOffset = 200;
    /**
     * 水平间距
     */
    private float horizontalMargin = 0;
    /**
     * 垂直间距
     */
    private float verticalMargin = 0;


    /**
     * 类型
     */
    public final static class Type {
        /**
         * 普通模式
         */
        public final static int NORMAL = 0;
        /**
         * 状态模式
         */
        public final static int STATUS = 1;
    }


    /**
     * 状态
     */
    public final static class Status {
        /**
         * 成功
         */
        public final static int SUCCESS = StatusView.SUCCESS;
        /**
         * 警告
         */
        public final static int WARNING = StatusView.WARNING;
        /**
         * 网络
         */
        public final static int WIRELESS = StatusView.WIRELESS;
    }


    /**
     * 提示
     *
     * @param context 上下文
     */
    public Toast(Context context) {
        onCreate(context, Type.NORMAL);
    }

    /**
     * 提示
     *
     * @param context 上下文
     * @param type    类型 , 普通：{@link Type#NORMAL} ；状态 {@link Type#STATUS}
     */
    public Toast(Context context, int type) {
        onCreate(context, type);
    }

    /**
     * 创建View
     *
     * @param context 上下文
     * @param type    类型 , 普通：{@link Type#NORMAL} ；状态 {@link Type#STATUS}
     * @return
     */
    protected View onCreate(Context context, int type) {
        this.context = context;
        this.type = type;
        contentView = onCreateView(context, type);
        return contentView;
    }

    /**
     * 创建对应位置View
     *
     * @param context 上下文
     * @param type    类型 , 普通：{@link Type#NORMAL} ；状态 {@link Type#STATUS}
     * @return
     */
    protected View onCreateView(Context context, int type) {
        if (type == Type.NORMAL) {
            contentView = LayoutInflater.from(context).inflate(R.layout.android_toast_normal, null);
            textView = contentView.findViewById(R.id.android_toast_text);
        }
        if (type == Type.STATUS) {
            contentView = LayoutInflater.from(context).inflate(R.layout.android_toast_status, null);
            layout = contentView.findViewById(R.id.android_toast_layout);
            statusView = contentView.findViewById(R.id.android_toast_view);
            textView = contentView.findViewById(R.id.android_toast_text);
        }
        return contentView;
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
     * 设置上下文
     *
     * @param context 上下文
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * 获取类型
     *
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * 获取状态类型
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置状态类型
     *
     * @param status 状态值<br/>
     *               成功：{@link Status#SUCCESS}<br/>
     *               警告：{@link Status#WARNING}<br/>
     *               网络 {@link Status#WIRELESS}
     */
    public void setStatus(int status) {
        this.status = status;
        if (statusView != null) {
            statusView.setStatus(status);
        }
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
     * 设置内容View
     *
     * @param contentView
     */
    public void setContentView(View contentView) {
        this.contentView = contentView;
    }

    /**
     * @return
     */
    public LinearLayout getLayout() {
        return layout;
    }

    /**
     * 获取中间显示的Toast,信息View
     *
     * @return
     */
    public TextView getTextView() {
        return textView;
    }

    /**
     * 获取状态View
     *
     * @return
     */
    public StatusView getStatusView() {
        return statusView;
    }

    /**
     * 设置显示位置
     *
     * @param gravity
     */
    public void setGravity(int gravity) {
        this.gravity = gravity;
        if (gravity == Gravity.CENTER) {
            xOffset = 0;
            yOffset = 0;
            verticalMargin = 0;
        }
        if (gravity == Gravity.BOTTOM) {
            xOffset = 0;
            yOffset = 200;
            verticalMargin = 0;
        }
    }

    /**
     * 获取显示位置
     *
     * @return
     */
    public int getGravity() {
        return gravity;
    }

    /**
     * 设置偏移X
     *
     * @param xOffset
     */
    public void setOffsetX(int xOffset) {
        this.xOffset = xOffset;
    }

    /**
     * 获取偏移X
     *
     * @return
     */
    public int getOffsetX() {
        return xOffset;
    }

    /**
     * 设置偏移Y
     *
     * @param yOffset
     */
    public void setOffsetY(int yOffset) {
        this.yOffset = yOffset;
    }

    /**
     * 获取偏移Y
     *
     * @return
     */
    public int getOffsetY() {
        return yOffset;
    }

    /**
     * 设置水平间距
     *
     * @param horizontalMargin
     */
    public void setHorizontalMargin(float horizontalMargin) {
        this.horizontalMargin = horizontalMargin;
    }

    /**
     * 获取水平间距
     *
     * @return
     */
    public float getHorizontalMargin() {
        return horizontalMargin;
    }

    /**
     * 设置垂直间距
     *
     * @param verticalMargin
     */
    public void setVerticalMargin(float verticalMargin) {
        this.verticalMargin = verticalMargin;
    }

    /**
     * 获取垂直间距
     *
     * @return
     */
    public float getVerticalMargin() {
        return verticalMargin;
    }

    /**
     * 设置提示状态颜色，针对status = {@link Type#STATUS}
     *
     * @param color
     */
    public void setStatusColor(int color) {
        if (type == Type.STATUS && statusView != null) {
            statusView.setColor(color);
        }
    }

    /**
     * 设置提示文字颜色
     *
     * @param color
     */
    public void setTextColor(@ColorInt int color) {
        if (textView == null) {
            return;
        }
        textView.setTextColor(color);
    }

    /**
     * 设置背景资源id
     *
     * @param resId 资源id
     */
    public void setBackgroundResource(@DrawableRes int resId) {
        if (textView != null) {
            textView.setBackgroundResource(resId);
        }
        if (layout != null) {
            layout.setBackgroundResource(resId);
        }
    }

    /**
     * 设置背景
     *
     * @param background 背景
     */
    public void setBackground(Drawable background) {
        if (textView != null) {
            textView.setBackground(background);
        }
        if (layout != null) {
            layout.setBackground(background);
        }
    }

    /**
     * 设置背景颜色
     *
     * @param color 颜色
     */
    public void setBackgroundColor(@ColorInt int color) {
        if (textView != null) {
            textView.setBackgroundColor(color);
        }
        if (layout != null) {
            layout.setBackgroundColor(color);
        }
    }

    /**
     * 设置信息
     * @param message 信息
     */
    public void setMessage(String message){
        if (textView != null) {
            textView.setText(message);
        }
    }

    /**
     * 显示信息
     */
    public void show() {
        android.widget.Toast toast = new android.widget.Toast(context);
        toast.setView(contentView);
        toast.setMargin(horizontalMargin, verticalMargin);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.show();
    }

}
