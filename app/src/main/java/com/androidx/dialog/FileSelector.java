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

    public final Builder builder;
    public final Context context;
    public final List<String> menu;
    public final int menuTextColor;
    public final int menuTextSize;
    public final int menuBackgroundColor;
    public final int menuBackground;
    public final int menuDividerHeight;
    public final int menuDividerColor;
    public final String cancelText;
    public final int cancelTextColor;
    public final int cancelTextSize;
    public final OnMenuItemClickListener onMenuItemClickListener;
    public final OnDocumentSelectListener onDocumentSelectListener;
    private CoreDialog dialog;
    private DocumentDialogAdapter adapter;
    private DocumentSelector.Builder selectorBuilder;
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
        public int getItemLayoutResId() {
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

        private Context context;
        private AppCompatActivity activity;
        private Fragment fragment;
        private DocumentSelector.Builder selector;

        private List<String> menu;
        private int menuTextColor = Color.parseColor("#333333");
        private int menuTextSize = 14;
        private int menuBackgroundColor = 0;
        private int menuBackground = 0;
        private int menuDividerHeight = 1;
        private int menuDividerColor = Color.parseColor("#F8F8F8");
        private String cancelText = "取消";
        private int cancelTextColor = Color.parseColor("#333333");
        private int cancelTextSize = 14;
        private OnMenuItemClickListener menuItemClickListener;
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
