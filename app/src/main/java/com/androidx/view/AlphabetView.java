package com.androidx.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.androidx.R;


/**
 * Author: Relin
 * Description:字母表<br/>
 * Date:2020/12/26 12:26
 */
public class AlphabetView extends View {

    /**
     * 画笔工具
     **/
    private Paint paint;
    /**
     * 控件的字母数据
     **/
    private String alphabets[] = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    /**
     * 控件的高度
     **/
    private int height;
    /**
     * 控件宽度
     **/
    private int width;
    /**
     * 单个字母高度
     **/
    private int unitFontHeight;
    /**
     * 字母的总数量
     */
    private int fontCount;
    /**
     * 选中字母的索引
     **/
    private int position = 0;
    /**
     * 字母颜色
     **/
    private int textColor = Color.parseColor("#333333");
    /**
     * 状态改变的字母颜色
     **/
    private int checkColor = Color.CYAN;
    /**
     * 字体的大小
     **/
    private int textSize = (int) (Resources.getSystem().getDisplayMetrics().density * 14);


    public AlphabetView(Context context) {
        super(context);
    }

    public AlphabetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onStyledAttributes(context, attrs);
    }

    public AlphabetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onStyledAttributes(context, attrs);
    }

    /**
     * 初始化属性
     *
     * @param context 上下文
     * @param attrs   属性
     */
    protected void onStyledAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AlphabetView);
        checkColor = array.getColor(R.styleable.AlphabetView_checkColor, checkColor);
        textColor = array.getColor(R.styleable.AlphabetView_android_textColor, textColor);
        textSize = array.getDimensionPixelSize(R.styleable.AlphabetView_android_textSize, textSize);
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        height = getHeight();
        width = getWidth();
        //总字数
        fontCount = alphabets.length;
        //一个字的高度
        unitFontHeight = height / (fontCount+1);
        for (int i = 0; i < fontCount; i++) {
            //设置画笔
            paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(textSize);
            //当选中对应的字母的时候，改变画笔的颜色
            if (i == position) {
                paint.setColor(checkColor);
            }
            //计算字体的显示位置
            float x = width / 2 - paint.measureText(alphabets[i]) / 2;
            float y = unitFontHeight * i + unitFontHeight;
            canvas.drawText(alphabets[i], x, y, paint);
            //重置画笔工具
            paint.reset();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                return true;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(true);
                return false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float y = event.getY();
        //计算点击字体的位置
        int index = (int) (y / height * (fontCount+1));
        //注意一定要这样判断，不判断也可以正常滑动，只是有些用户如果滑动过于频繁就崩溃
        if (0 <= index && index != -1 && index < fontCount) {
            float x = width / 2 - paint.measureText(alphabets[index]) / 2;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    getParent().requestDisallowInterceptTouchEvent(true);
                    position = index;
                    if (onAlphabetTouchListener != null) {
                        onAlphabetTouchListener.onAlphabetTouchDown(alphabets, index, alphabets[index]);
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    position = index;
                    if (onAlphabetTouchListener != null) {
                        onAlphabetTouchListener.onAlphabetTouchMove(alphabets, index, alphabets[index]);
                    }
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    getParent().requestDisallowInterceptTouchEvent(false);
                    if (onAlphabetTouchListener != null) {
                        onAlphabetTouchListener.onAlphabetTouchUp(alphabets, index, alphabets[index]);
                    }
                    break;
            }
        }
        return true;
    }

    /**
     * 获取默认颜色
     *
     * @return
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * 设置默认颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    /**
     * 获取选中颜色
     *
     * @return
     */
    public int getCheckColor() {
        return checkColor;
    }

    /**
     * 设置选中颜色
     *
     * @param checkColor
     */
    public void setCheckColor(int checkColor) {
        this.checkColor = checkColor;
        invalidate();
    }

    /**
     * 获取字体大小
     *
     * @return
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * 设置字体大小
     *
     * @param textSize 字体大小
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
        invalidate();
    }

    /**
     * 字母触摸监听
     */
    public OnAlphabetTouchListener onAlphabetTouchListener;

    /**
     * 设置字母触摸监听
     *
     * @param onAlphabetTouchListener
     */
    public void setOnAlphabetTouchListener(OnAlphabetTouchListener onAlphabetTouchListener) {
        this.onAlphabetTouchListener = onAlphabetTouchListener;
    }

    public interface OnAlphabetTouchListener {

        /**
         * 字母按下
         *
         * @param alphabets 字母列表
         * @param position  字母位置
         * @param text      文字
         */
        void onAlphabetTouchDown(String alphabets[], int position, String text);

        /**
         * 按下移动
         *
         * @param alphabets 字母列表
         * @param position  字母位置
         * @param text      文字
         */
        void onAlphabetTouchMove(String alphabets[], int position, String text);

        /**
         * 按下弹起
         *
         * @param alphabets 字母列表
         * @param position  字母位置
         * @param text      文字
         */
        void onAlphabetTouchUp(String alphabets[], int position, String text);

    }

}