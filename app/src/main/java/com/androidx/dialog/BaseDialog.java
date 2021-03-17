package com.androidx.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.androidx.annotation.ViewUtils;
import com.androidx.app.CoreActivity;
import com.androidx.net.OnHttpListener;
import com.androidx.net.ResponseBody;

/**
 * 基础对话框
 */
public abstract class BaseDialog implements OnHttpListener {

    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 对话框
     */
    private CoreDialog dialog;
    /**
     * 构建对象
     */
    private CoreDialog.Builder builder;

    public BaseDialog(Context context) {
        this.context = context;
        builder = new CoreDialog.Builder(context);
        builder.width(getWidth());
        builder.height(getHeight());
        builder.gravity(getGravity());
        builder.themeResId(getThemeResId());
        builder.cancelable(getCancelable());
        builder.canceledOnTouchOutside(getCanceledOnTouchOutside());
        builder.layoutResId(getLayoutResId());
        builder.animResId(getAnimResId());
        dialog = builder.build();
        onCreate(dialog);
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
     * 获取宽度
     *
     * @return
     */
    protected int getWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    /**
     * 获取高度
     *
     * @return
     */
    protected int getHeight() {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    /**
     * 获取动画资源
     *
     * @return
     */
    protected int getAnimResId() {
        return CoreDialog.ANIM_BOTTOM;
    }

    /**
     * 获取显示位置
     *
     * @return
     */
    protected int getGravity() {
        return Gravity.BOTTOM;
    }

    /**
     * 获取显示主题
     *
     * @return
     */
    protected int getThemeResId() {
        return CoreDialog.THEME_TRANSLUCENT;
    }

    /**
     * 获取是否可取消
     *
     * @return
     */
    protected boolean getCancelable() {
        return true;
    }

    /**
     * 获取点击外部可取消
     *
     * @return
     */
    protected boolean getCanceledOnTouchOutside() {
        return true;
    }

    /**
     * 获取布局文件
     *
     * @return
     */
    protected abstract int getLayoutResId();

    /**
     * 对话框创建
     *
     * @param dialog
     */
    protected void onCreate(CoreDialog dialog) {
        ViewUtils.inject(this, dialog.contentView);
    }

    /**
     * 获取对话框对象
     *
     * @return
     */
    public CoreDialog getDialog() {
        return dialog;
    }

    /**
     * 获取内容布局
     *
     * @return
     */
    public View getContentView() {
        return dialog.contentView;
    }

    /**
     * 显示
     */
    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    /**
     * 消失
     */
    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onHttpSucceed(ResponseBody responseBody) {
        addResponseBody(responseBody);
    }

    @Override
    public void onHttpFailure(ResponseBody responseBody) {
        addResponseBody(responseBody);
    }

    /**
     * 添加日志显示数据
     *
     * @param responseBody
     */
    private void addResponseBody(ResponseBody responseBody) {
        CoreActivity activity = (CoreActivity) getContext();
        if (activity != null) {
            responseBody.page(getClass().getCanonicalName());
            activity.getDebug().addResponseBody(responseBody);
        }
    }

}
