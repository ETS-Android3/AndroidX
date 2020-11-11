package com.android.app.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.R;
import com.android.utils.ListUtils;
import com.android.view.LoopView;

import java.util.ArrayList;

public class ItemSelector {


    /**
     * 上下文
     */
    public final Context context;

    /**
     * 显示对象
     */
    private Dialog dialog;

    /**
     * 标题颜色
     */
    public final int titleBarBackgroundColor;

    /**
     * 标题字体颜色
     */
    public final int titleBarCancelTextColor;
    /**
     * 标题字体颜色
     */
    public final int titleBarTitleTextColor;

    /**
     * 标题字体颜色
     */
    public final int titleBarConfirmTextColor;

    /**
     * 取消字体大小
     */
    public final int titleBarCancelTextSize;
    /**
     * 标题字体大小
     */
    public final int titleBarTitleTextSize;
    /**
     * 确认字体大小
     */
    public final int titleBarConfirmTextSize;

    /**
     * 分割线颜色
     */
    public final int dividerColor;

    /**
     * 选择颜色
     */
    public final int selectedColor;

    /**
     * 未选中颜色
     */
    public final int unselectedColor;

    /**
     * 字体大小
     */
    public final int textSize;

    /**
     * 背景师范半透明
     */
    public final boolean translucent;
    /**
     * 日期选中回调函数
     */
    public final OnItemSelectListener listener;

    public final String cancelText;

    public final String confirmText;

    public final String titleText;

    public final ArrayList<String> list;

    public final String[] items;

    public final boolean loop;


    public ItemSelector(Builder builder) {
        this.context = builder.context;
        this.translucent = builder.translucent;
        this.titleBarBackgroundColor = builder.titleBarBackgroundColor;
        this.titleBarCancelTextColor = builder.titleBarCancelTextColor;
        this.titleBarTitleTextColor = builder.titleBarTitleTextColor;
        this.titleBarConfirmTextColor = builder.titleBarConfirmTextColor;
        this.cancelText = builder.cancelText;
        this.confirmText = builder.confirmText;
        this.titleText = builder.titleText;
        this.titleBarCancelTextSize = builder.titleBarCancelTextSize;
        this.titleBarTitleTextSize = builder.titleBarTitleTextSize;
        this.titleBarConfirmTextSize = builder.titleBarConfirmTextSize;
        this.dividerColor = builder.dividerColor;
        this.selectedColor = builder.selectedColor;
        this.unselectedColor = builder.unselectedColor;
        this.textSize = builder.textSize;
        this.listener = builder.listener;
        this.items = builder.items;
        this.loop = builder.loop;
        if (items != null) {
            builder.list = new ArrayList<>();
            for (int i = 0; i < items.length; i++) {
                builder.list.add(items[i]);
            }
        }
        this.list = builder.list;
        createDialog(context, listener);
        show();
    }

    public static class Builder {

        private Context context;

        private boolean translucent = true;

        private int titleBarBackgroundColor = Color.parseColor("#F2F2F2");

        private int titleBarCancelTextColor = Color.parseColor("#454545");

        private int titleBarTitleTextColor = Color.parseColor("#454545");

        private int titleBarConfirmTextColor = Color.parseColor("#0EB692");

        private int titleBarCancelTextSize = 16;

        private int titleBarTitleTextSize = 16;

        private int titleBarConfirmTextSize = 16;

        private int dividerColor = Color.parseColor("#CDCDCD");

        private int selectedColor = Color.parseColor("#0EB692");

        private int unselectedColor = Color.parseColor("#AEAEAE");

        private int textSize = 16;

        private OnItemSelectListener listener;

        private ArrayList<String> list;

        private String[] items;

        private boolean loop = false;

        private String cancelText;

        private String confirmText;

        private String titleText;

        public Builder(Context context) {
            this.context = context;
        }

        public boolean isTranslucent() {
            return translucent;
        }

        public Builder translucent(boolean translucent) {
            this.translucent = translucent;
            return this;
        }

        public Context context() {
            return context;
        }

        public int titleBarBackgroundColor() {
            return titleBarBackgroundColor;
        }

        public Builder titleBarBackgroundColor(int titleBarBackgroundColor) {
            this.titleBarBackgroundColor = titleBarBackgroundColor;
            return this;
        }

        public int titleBarCancelTextColor() {
            return titleBarCancelTextColor;
        }

        public Builder titleBarCancelTextColor(int titleBarCancelTextColor) {
            this.titleBarCancelTextColor = titleBarCancelTextColor;
            return this;
        }

        public Builder titleBarTitleTextColor(int titleBarTitleTextColor) {
            this.titleBarTitleTextColor = titleBarTitleTextColor;
            return this;
        }

        public int titleBarTitleTextColor() {
            return titleBarTitleTextColor;
        }

        public int titleBarConfirmTextColor() {
            return titleBarConfirmTextColor;
        }

        public Builder titleBarConfirmTextColor(int titleBarConfirmTextColor) {
            this.titleBarConfirmTextColor = titleBarConfirmTextColor;
            return this;
        }

        public int titleBarCancelTextSize() {
            return titleBarCancelTextSize;
        }

        public Builder titleBarCancelTextSize(int titleBarCancelTextSize) {
            this.titleBarCancelTextSize = titleBarCancelTextSize;
            return this;
        }

        public int titleBarTitleTextSize() {
            return titleBarTitleTextSize;
        }

        public Builder titleBarTitleTextSize(int titleBarTitleTextSize) {
            this.titleBarTitleTextSize = titleBarTitleTextSize;
            return this;
        }

        public int titleBarConfirmTextSize() {
            return titleBarConfirmTextSize;
        }

        public Builder titleBarConfirmTextSize(int titleBarConfirmTextSize) {
            this.titleBarConfirmTextSize = titleBarConfirmTextSize;
            return this;
        }

        public int dividerColor() {
            return dividerColor;
        }

        public Builder dividerColor(int dividerColor) {
            this.dividerColor = dividerColor;
            return this;
        }

        public int selectedColor() {
            return selectedColor;
        }

        public Builder selectedColor(int selectedColor) {
            this.selectedColor = selectedColor;
            return this;
        }

        public int unselectedColor() {
            return unselectedColor;
        }

        public Builder unselectedColor(int unselectedColor) {
            this.unselectedColor = unselectedColor;
            return this;
        }

        public int textSize() {
            return textSize;
        }

        public Builder textSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public OnItemSelectListener listener() {
            return listener;
        }

        public Builder listener(OnItemSelectListener listener) {
            this.listener = listener;
            return this;
        }

        public ArrayList<String> list() {
            return list;
        }

        public Builder list(ArrayList<String> list) {
            this.list = list;
            return this;
        }

        public String[] items() {
            return items;
        }

        public Builder items(String[] items) {
            this.items = items;
            return this;
        }

        public boolean isLoop() {
            return loop;
        }

        public Builder loop(boolean loop) {
            this.loop = loop;
            return this;
        }

        public String cancelText() {
            return cancelText;
        }

        public Builder cancelText(String cancelText) {
            this.cancelText = cancelText;
            return this;
        }

        public String confirmText() {
            return confirmText;
        }

        public Builder confirmText(String confirmText) {
            this.confirmText = confirmText;
            return this;
        }

        public String titleText() {
            return titleText;
        }

        public Builder titleText(String titleText) {
            this.titleText = titleText;
            return this;
        }

        public ItemSelector build() {
            return new ItemSelector(this);
        }
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


    /**
     * 显示日期选择器
     *
     * @param context  上下文
     * @param listener 选择监听
     * @return
     */
    private void createDialog(Context context, final OnItemSelectListener listener) {
        dialog = new Dialog.Builder(context)
                .width(LinearLayout.LayoutParams.MATCH_PARENT)
                .height(LinearLayout.LayoutParams.WRAP_CONTENT)
                .layoutResId(R.layout.android_item_selector)
                .animResId(R.style.android_anim_bottom)
                .themeResId(translucent ? R.style.Android_Theme_Dialog_Translucent_Background : R.style.Android_Theme_Dialog_Transparent_Background)
                .gravity(Gravity.BOTTOM)
                .build();
        LinearLayout ll_bar = dialog.contentView.findViewById(R.id.ll_bar);
        TextView tv_cancel = dialog.contentView.findViewById(R.id.tv_cancel);
        TextView tv_title = dialog.contentView.findViewById(R.id.tv_title);
        TextView tv_complete = dialog.contentView.findViewById(R.id.tv_complete);
        final LoopView lv_loop = dialog.contentView.findViewById(R.id.lv_loop);

        ll_bar.setBackgroundColor(titleBarBackgroundColor);
        tv_cancel.setTextColor(titleBarCancelTextColor);
        tv_complete.setTextColor(titleBarConfirmTextColor);
        tv_cancel.setTextSize(titleBarCancelTextSize);
        tv_title.setTextSize(titleBarTitleTextSize);
        tv_complete.setTextSize(titleBarConfirmTextSize);

        lv_loop.setDividerColor(dividerColor);
        lv_loop.setCenterTextColor(selectedColor);
        lv_loop.setOuterTextColor(unselectedColor);
        lv_loop.setTextSize(textSize);
        if (loop) {
            lv_loop.setLoop();
        } else {
            lv_loop.setNotLoop();
        }
        if (list != null) {
            lv_loop.setItems(list);
        }
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog.dialog == null) {
                    return;
                }
                dialog.dialog.dismiss();
            }
        });
        tv_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && ListUtils.getSize(list) != 0) {
                    listener.onItemSelect(list.get(lv_loop.getSelectedItem()), lv_loop.getSelectedItem());
                }
                dialog.dialog.dismiss();
            }
        });
    }

}
