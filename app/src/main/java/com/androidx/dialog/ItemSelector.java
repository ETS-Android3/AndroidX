package com.androidx.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidx.R;
import com.androidx.util.Screen;
import com.androidx.util.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Relin
 * Describe:Item列表
 * Date:2020/5/25 13:55
 */
public class ItemSelector {

    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 图标
     */
    public final int ico;
    /**
     * 标题
     */
    public final String title;
    /**
     * 标题文字大小
     */
    public final int titleSize;
    /**
     * 标题文字颜色
     */
    public final int titleColor;
    /**
     * 标题文字颜色
     */
    public final int titleBackgroundColor;
    /**
     * 标题文字大小
     */
    public final int itemSize;
    /**
     * 标题文字颜色
     */
    public final int itemColor;
    /**
     * 标题文字颜色
     */
    public final int itemBackgroundColor;
    /**
     * 菜单文字
     */
    public final String menu;
    /**
     * 菜单文字大小
     */
    public final int menuSize;
    /**
     * 菜单文字颜色
     */
    public final int menuColor;
    /**
     * 菜单文字颜色
     */
    public final int menuBackgroundColor;
    /**
     * 是否能取消选择
     */
    public final boolean cancelable;
    /**
     * 默认位置
     */
    public final int position;
    /**
     * 数据源
     */
    public final List<SelectorBody> bodies;
    /**
     * 字符数组
     */
    public final String[] array;
    /**
     * 点击事件
     */
    public final OnItemDialogClickListener listener;
    /**
     * 对话框
     */
    private CoreDialog dialog;

    /**
     * 构建
     *
     * @param builder
     */
    public ItemSelector(Builder builder) {
        this.context = builder.context;
        this.ico = builder.ico;
        this.title = builder.title;
        this.titleSize = builder.titleSize;
        this.titleColor = builder.titleColor;
        this.titleBackgroundColor = builder.titleBackgroundColor;
        this.itemSize = builder.itemSize;
        this.itemColor = builder.itemColor;
        this.itemBackgroundColor = builder.itemBackgroundColor;
        this.menu = builder.menu;
        this.menuSize = builder.menuSize;
        this.menuColor = builder.menuColor;
        this.menuBackgroundColor = builder.menuBackgroundColor;
        this.cancelable = builder.cancelable;
        this.position = builder.position;
        this.array = builder.array;
        this.listener = builder.listener;
        if (array != null && array.length > 0) {
            List<SelectorBody> list = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                SelectorBody body = new SelectorBody();
                body.setPosition(i);
                body.setCheck(false);
                body.setName(array[i]);
                list.add(body);
            }
            builder.bodies(list);
        }
        this.bodies = builder.bodies;
        onCreate(builder);
    }

    /**
     * 构建
     *
     * @param builder 构建者
     */
    private void onCreate(Builder builder) {
        CoreDialog.Builder coreBuilder = new CoreDialog.Builder(context);
        coreBuilder.gravity(Gravity.CENTER);
        coreBuilder.width((int) (Screen.width() * 0.75f));
        coreBuilder.height(LinearLayout.LayoutParams.WRAP_CONTENT);
        coreBuilder.themeResId(CoreDialog.THEME_TRANSLUCENT);//半透明背景
        coreBuilder.layoutResId(R.layout.android_dialog_items);
        coreBuilder.canceledOnTouchOutside(cancelable);
        coreBuilder.cancelable(cancelable);
        coreBuilder.animResId(CoreDialog.ANIM_SCALE);//底部进入动画
        dialog = coreBuilder.build();
        //找到控件
        FrameLayout android_fl_title = dialog.contentView.findViewById(R.id.android_fl_title);
        ImageView android_iv_icon = dialog.contentView.findViewById(R.id.android_iv_icon);
        TextView android_tv_title = dialog.contentView.findViewById(R.id.android_tv_title);
        TextView android_tv_menu = dialog.contentView.findViewById(R.id.android_tv_menu);
        ListView android_lv_content = dialog.contentView.findViewById(R.id.android_lv_content);
        //标题
        if (ico != 0) {
            android_iv_icon.setImageResource(ico);
        }
        if (titleBackgroundColor != 0) {
            android_fl_title.setBackgroundColor(titleBackgroundColor);
        }
        android_tv_title.setText(title);
        if (titleSize != 0) {
            android_tv_title.setTextSize(titleSize);
        }
        if (titleColor != 0) {
            android_tv_title.setTextColor(titleColor);
        }
        //菜单
        android_tv_menu.setText(menu);
        if (menuSize != 0) {
            android_tv_menu.setTextSize(menuSize);
        }
        if (menuColor != 0) {
            android_tv_menu.setTextColor(menuColor);
        }
        //列表
        for (int i = 0; i < Size.of(bodies); i++) {
            bodies.get(i).setCheck(i == position ? true : false);
        }
        ItemDialogAdapter adapter = new ItemDialogAdapter(context, dialog, bodies, listener);
        android_lv_content.setAdapter(adapter);
    }

    /**
     * 显示
     */
    public void show() {
        dialog.show();
    }

    /**
     * 隐藏
     */
    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * 是否显示
     *
     * @return
     */
    public boolean isShowing() {
        if (dialog == null) {
            return false;
        }
        return dialog.isShowing();
    }

    private class ItemDialogAdapter extends BaseAdapter {

        private Context context;
        private CoreDialog dialog;
        private List<SelectorBody> items;
        private OnItemDialogClickListener onItemDialogClickListener;

        public ItemDialogAdapter(Context context, CoreDialog dialog, List<SelectorBody> items, OnItemDialogClickListener onItemDialogClickListener) {
            this.context = context;
            this.dialog = dialog;
            this.items = items;
            this.onItemDialogClickListener = onItemDialogClickListener;
        }

        @Override
        public int getCount() {
            return Size.of(items);
        }

        @Override
        public SelectorBody getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.android_dialog_item, null);
                holder.android_tv_name = convertView.findViewById(R.id.android_tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.android_tv_name.setText(getItem(position).getName());
            boolean isCheck = getItem(position).isCheck();
            holder.android_tv_name.setTextColor(isCheck ? context.getResources().getColor(R.color.colorItemDialogItemCheck) : context.getResources().getColor(R.color.colorItemDialogItemUnCheck));
            holder.android_tv_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < getCount(); i++) {
                        items.get(i).setCheck(i == position ? true : false);
                    }
                    notifyDataSetChanged();
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    if (onItemDialogClickListener != null) {
                        onItemDialogClickListener.onItemDialogClick(dialog, items, position);
                    }
                }
            });
            return convertView;
        }

        public class ViewHolder {
            private TextView android_tv_name;
        }

    }

    public interface OnItemDialogClickListener {

        void onItemDialogClick(CoreDialog dialog, List<SelectorBody> bodies, int position);

    }

    public static class Builder {

        /**
         * 上下文对象
         */
        private Context context;
        /**
         * 图标
         */
        private int ico = 0;
        /**
         * 标题
         */
        private String title;
        /**
         * 标题文字大小
         */
        private int titleSize = 14;
        /**
         * 标题文字颜色
         */
        private int titleColor;
        /**
         * 标题文字颜色
         */
        private int titleBackgroundColor;
        ;
        /**
         * 标题文字大小
         */
        private int itemSize;
        /**
         * 标题文字颜色
         */
        private int itemColor;
        /**
         * 标题文字颜色
         */
        private int itemBackgroundColor;
        /**
         * 菜单文字
         */
        private String menu;
        /**
         * 菜单文字大小
         */
        private int menuSize;
        /**
         * 菜单文字颜色
         */
        private int menuColor;
        /**
         * 菜单文字颜色
         */
        private int menuBackgroundColor;
        /**
         * 是否能取消选择
         */
        private boolean cancelable = true;
        /**
         * 默认位置
         */
        private int position = -1;
        /**
         * 数据
         */
        private List<SelectorBody> bodies;
        /**
         * 数据
         */
        private String[] array;
        /**
         * 点击事件
         */
        private OnItemDialogClickListener listener;

        public Builder(Context context) {
            this.context = context;
        }

        public String title() {
            return title;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public int ico() {
            return ico;
        }

        public Builder ico(int ico) {
            this.ico = ico;
            return this;
        }

        public int titleSize() {
            return titleSize;
        }

        public Builder titleSize(int titleSize) {
            this.titleSize = titleSize;
            return this;
        }

        public int titleColor() {
            return titleColor;
        }

        public Builder titleColor(int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        public int titleBackgroundColor() {
            return titleBackgroundColor;
        }

        public Builder titleBackgroundColor(int titleBackgroundColor) {
            this.titleBackgroundColor = titleBackgroundColor;
            return this;
        }

        public int itemSize() {
            return itemSize;
        }

        public Builder itemSize(int itemSize) {
            this.itemSize = itemSize;
            return this;
        }

        public int itemColor() {
            return itemColor;
        }

        public Builder itemColor(int itemColor) {
            this.itemColor = itemColor;
            return this;
        }

        public int itemBackgroundColor() {
            return itemBackgroundColor;
        }

        public Builder itemBackgroundColor(int itemBackgroundColor) {
            this.itemBackgroundColor = itemBackgroundColor;
            return this;
        }

        public boolean cancelable() {
            return cancelable;
        }

        public Builder cancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public String menu() {
            return menu;
        }

        public Builder menu(String menu) {
            this.menu = menu;
            return this;
        }

        public int menuSize() {
            return menuSize;
        }

        public Builder menuSize(int menuSize) {
            this.menuSize = menuSize;
            return this;
        }

        public int menuColor() {
            return menuColor;
        }

        public Builder menuColor(int menuColor) {
            this.menuColor = menuColor;
            return this;
        }

        public int menuBackgroundColor() {
            return menuBackgroundColor;
        }

        public Builder menuBackgroundColor(int menuBackgroundColor) {
            this.menuBackgroundColor = menuBackgroundColor;
            return this;
        }

        public int position() {
            return position;
        }

        public Builder position(int position) {
            this.position = position;
            return this;
        }

        public List<SelectorBody> bodies() {
            return bodies;
        }

        public Builder bodies(List<SelectorBody> bodies) {
            this.bodies = bodies;
            return this;
        }

        public String[] array() {
            return array;
        }

        public Builder array(String[] array) {
            this.array = array;
            return this;
        }

        public OnItemDialogClickListener listener() {
            return listener;
        }

        public Builder listener(OnItemDialogClickListener listener) {
            this.listener = listener;
            return this;
        }

        public ItemSelector build() {
            return new ItemSelector(this);
        }

    }

}
