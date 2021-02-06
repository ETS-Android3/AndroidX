package com.androidx.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.androidx.R;
import com.androidx.content.DocumentSelector;
import com.androidx.content.OnDocumentSelectListener;
import com.androidx.widget.BasisAdapter;
import com.androidx.widget.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 文档对话框
 */
public class FileSelector implements View.OnClickListener, BasisAdapter.OnItemClickListener<String> {

    /**
     * 构建者
     */
    public final Builder builder;
    /**
     * 上下文对象
     */
    public final Context context;
    /**
     * 菜单
     */
    public final List<String> menu;
    /**
     * 菜单文字颜色
     */
    public final int menuTextColor;
    /**
     * 菜单文字大小
     */
    public final int menuTextSize;
    /**
     * 菜单背景颜色
     */
    public final int menuBackgroundColor;
    /**
     * 菜单背景
     */
    public final int menuBackground;
    /**
     * 菜单分割线高度
     */
    public final int menuDividerHeight;
    /**
     * 菜单分割线颜色
     */
    public final int menuDividerColor;
    /**
     * 取消文字
     */
    public final String cancelText;
    /**
     * 取消文字颜色
     */
    public final int cancelTextColor;
    /**
     * 取消文字大小
     */
    public final int cancelTextSize;
    /**
     * 菜单点击事件
     */
    public final OnMenuItemClickListener onMenuItemClickListener;
    /**
     * 文档选择监听
     */
    public final OnDocumentSelectListener onDocumentSelectListener;
    /**
     * Dialog对象
     */
    private CoreDialog dialog;
    /**
     * 适配器
     */
    private DocumentDialogAdapter adapter;
    /**
     * 选择器构建者
     */
    private DocumentSelector.Builder selectorBuilder;
    /**
     * 选择器
     */
    private DocumentSelector selector;

    public FileSelector(Builder builder) {
        this.builder = builder;
        this.context = builder.context;
        this.menu = builder.menu();
        this.menuTextColor = builder.menuTextColor();
        this.menuTextSize = builder.menuTextSize();
        this.menuBackgroundColor = builder.menuBackgroundColor();
        this.menuBackground = builder.menuBackground();
        this.menuDividerHeight = builder.menuDividerHeight();
        this.menuDividerColor = builder.menuDividerColor();
        this.cancelText = builder.cancelText();
        this.cancelTextColor = builder.cancelTextColor();
        this.cancelTextSize = builder.cancelTextSize();
        this.onMenuItemClickListener = builder.menuItemClickListener();
        this.onDocumentSelectListener = builder.documentSelectListener();
        onCreate(builder);
    }

    protected void onCreate(Builder builder) {
        if (builder.selector() == null) {
            if (builder.activity() != null) {
                selectorBuilder = new DocumentSelector.Builder(builder.activity());
            }
            if (builder.fragment() != null) {
                selectorBuilder = new DocumentSelector.Builder(builder.fragment());
            }
        } else {
            selectorBuilder = builder.selector();
        }
        selectorBuilder.listener(onDocumentSelectListener);
        CoreDialog.Builder coreBuilder = new CoreDialog.Builder(builder.context());
        coreBuilder.themeResId(CoreDialog.THEME_TRANSLUCENT);
        coreBuilder.width(LinearLayout.LayoutParams.MATCH_PARENT);
        coreBuilder.height(LinearLayout.LayoutParams.WRAP_CONTENT);
        coreBuilder.layoutResId(R.layout.android_dialog_document_selector);
        coreBuilder.animResId(CoreDialog.ANIM_BOTTOM);
        coreBuilder.gravity(Gravity.BOTTOM);
        coreBuilder.cancelable(true);
        coreBuilder.canceledOnTouchOutside(true);
        dialog = coreBuilder.build();
        adapter = new DocumentDialogAdapter(builder.context());
        ListView listView = dialog.contentView.findViewById(R.id.android_lv_menu);
        if (builder.menuBackgroundColor != 0) {
            listView.setBackgroundColor(builder.menuBackgroundColor());
        }
        if (builder.menuBackground != 0) {
            listView.setBackgroundResource(builder.menuBackground());
        }
        adapter.setItems(builder.menu());
        listView.setAdapter(adapter);
        TextView cancelView = dialog.contentView.findViewById(R.id.tv_cancel);
        cancelView.setText(builder.cancelText());
        cancelView.setTextColor(builder.cancelTextColor());
        cancelView.setTextSize(builder.cancelTextSize());
        cancelView.setOnClickListener(this);
        adapter.setOnItemClickListener(this);
        dialog.show();
    }

    /**
     * 处理结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (selector != null) {
            selector.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            onCancel();
        }
    }

    @Override
    public void onItemClick(BasisAdapter<String> adapter, View view, String item, int position) {
        if (item.equals(context.getResources().getString(R.string.document_selector_take_photo))) {
            onTakePicture();
        }
        if (item.equals(context.getResources().getString(R.string.document_selector_photo_album))) {
            onPhotoAlbum();
        }
        if (onMenuItemClickListener != null) {
            onMenuItemClickListener.OnMenuItemClickListener(this, item, position);
        }
    }

    /**
     * 拍照
     */
    protected void onTakePicture() {
        dismiss();
        selectorBuilder.mode(DocumentSelector.MODE_IMAGE_CAPTURE);
        selector = selectorBuilder.build();
    }

    /**
     * 相册
     */
    protected void onPhotoAlbum() {
        dismiss();
        selectorBuilder.mode(DocumentSelector.MODE_PICK_IMAGE);
        selector = selectorBuilder.build();
    }

    /**
     * 取消
     */
    protected void onCancel() {
        dismiss();
    }

    private class DocumentDialogAdapter extends BasisAdapter<String> {

        public DocumentDialogAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemLayoutResId(int viewType) {
            return R.layout.android_item_document_selector;
        }

        @Override
        public void onItemBindViewHolder(ViewHolder holder, String item, int position) {
            TextView textView = holder.find(TextView.class, R.id.android_tv_name);
            textView.setText(item);
            textView.setTextColor(menuTextColor);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, menuTextSize);
            holder.addItemClick(R.id.android_tv_name);
        }
    }

    public interface OnMenuItemClickListener {

        void OnMenuItemClickListener(FileSelector documentDialog, String item, int position);

    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public static class Builder {

        /**
         * 上下文对象
         */
        private Context context;
        /**
         * Activity
         */
        private AppCompatActivity activity;
        /**
         * Fragment
         */
        private Fragment fragment;
        /**
         * 选择器
         */
        private DocumentSelector.Builder selector;
        /**
         * 菜单
         */
        private List<String> menu;
        /**
         * 菜单文字颜色
         */
        private int menuTextColor = Color.parseColor("#333333");
        /**
         * 菜单文字大小
         */
        private int menuTextSize = 14;
        /**
         * 菜单背景颜色
         */
        private int menuBackgroundColor = 0;
        /**
         * 菜单背景
         */
        private int menuBackground = 0;
        /**
         * 菜单分割线高度
         */
        private int menuDividerHeight = 1;
        /**
         * 菜单分割线颜色
         */
        private int menuDividerColor = Color.parseColor("#F8F8F8");
        /**
         * 取消文字
         */
        private String cancelText = "取消";
        /**
         * 取消文字颜色
         */
        private int cancelTextColor = Color.parseColor("#333333");
        /**
         * 取消文字大小
         */
        private int cancelTextSize = 14;
        /**
         * 菜单点击监听
         */
        private OnMenuItemClickListener menuItemClickListener;
        /**
         * 文档选择监听
         */
        private OnDocumentSelectListener documentSelectListener;

        public Builder(AppCompatActivity activity) {
            this.activity = activity;
            this.context = activity;
        }

        public Builder(Fragment fragment) {
            this.fragment = fragment;
            this.context = fragment.getContext();
        }

        public Context context() {
            return context;
        }

        public AppCompatActivity activity() {
            return activity;
        }

        public Fragment fragment() {
            return fragment;
        }

        public Builder selector(DocumentSelector.Builder selector) {
            this.selector = selector;
            return this;
        }

        public DocumentSelector.Builder selector() {
            return selector;
        }

        public List<String> menu() {
            if (menu == null) {
                menu = new ArrayList<>();
                menu.add(context.getResources().getString(R.string.document_selector_take_photo));
                menu.add(context.getResources().getString(R.string.document_selector_photo_album));
            }
            return menu;
        }

        public Builder menu(List<String> menu) {
            this.menu = menu;
            return this;
        }

        public int menuTextColor() {
            return menuTextColor;
        }

        public Builder menuTextColor(int menuTextColor) {
            this.menuTextColor = menuTextColor;
            return this;
        }

        public int menuTextSize() {
            return menuTextSize;
        }

        public Builder menuTextSize(int menuTextSize) {
            this.menuTextSize = menuTextSize;
            return this;
        }

        public int menuBackgroundColor() {
            return menuBackgroundColor;
        }

        public Builder menuBackgroundColor(int menuBackgroundColor) {
            this.menuBackgroundColor = menuBackgroundColor;
            return this;
        }

        public int menuBackground() {
            return menuBackground;
        }

        public Builder menuBackground(int menuBackground) {
            this.menuBackground = menuBackground;
            return this;
        }

        public int menuDividerHeight() {
            return menuDividerHeight;
        }

        public Builder menuDividerHeight(int menuDividerHeight) {
            this.menuDividerHeight = menuDividerHeight;
            return this;
        }

        public int menuDividerColor() {
            return menuDividerColor;
        }

        public Builder menuDividerColor(int menuDividerColor) {
            this.menuDividerColor = menuDividerColor;
            return this;
        }

        public String cancelText() {
            if (cancelText == null) {
                cancelText = context.getResources().getString(R.string.document_selector_cancel);
            }
            return cancelText;
        }

        public void cancelText(String cancelText) {
            this.cancelText = cancelText;
        }

        public int cancelTextColor() {
            return cancelTextColor;
        }

        public Builder cancelTextColor(int cancelTextColor) {
            this.cancelTextColor = cancelTextColor;
            return this;
        }

        public int cancelTextSize() {
            return cancelTextSize;
        }

        public Builder cancelTextSize(int cancelTextSize) {
            this.cancelTextSize = cancelTextSize;
            return this;
        }

        public OnMenuItemClickListener menuItemClickListener() {
            return menuItemClickListener;
        }

        public Builder menuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
            this.menuItemClickListener = menuItemClickListener;
            return this;
        }

        public Builder documentSelectListener(OnDocumentSelectListener documentSelectListener) {
            this.documentSelectListener = documentSelectListener;
            return this;
        }

        public OnDocumentSelectListener documentSelectListener() {
            return documentSelectListener;
        }

        public FileSelector build() {
            return new FileSelector(this);
        }

    }

}
