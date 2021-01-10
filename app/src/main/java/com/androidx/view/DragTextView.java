package com.androidx.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.androidx.util.DragHelper;

/**
 * Author: Relin
 * Description:拖拽文本
 * Date:2020/12/26 17:51
 */
public class DragTextView extends AppCompatTextView {

    private DragHelper helper;

    public DragTextView(Context context) {
        super(context);
        initHelper();
    }

    public DragTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initHelper();
    }

    public DragTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHelper();
    }

    private void initHelper() {
        helper = new DragHelper(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        helper.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return helper.onTouchEvent(event);
    }

}
