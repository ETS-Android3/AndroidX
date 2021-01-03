package com.androidx.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.androidx.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: Relin
 * Description:数据加载
 * Date:2020/12/14 22:01
 */
public class LoadingView extends View {

    /**
     * 日志标识
     */
    public static final String TAG = LoadingView.class.getSimpleName();

    /**
     * 垂直方向
     */
    public final static int VERTICAL = 1;
    /**
     * 水平方向
     */
    public final static int HORIZONTAL = 0;

    /**
     * 线条画笔
     */
    private Paint streakPaint;
    /**
     * 线条颜色
     */
    private int streakColor;
    /**
     * 文字颜色
     */
    private int textColor;
    /**
     * 宽度
     */
    private int width;
    /**
     * 高度
     */
    private int height;
    /**
     * 中心点
     */
    private float centerX, centerY;
    /**
     * 等边边长
     */
    private float sideLength = 0;
    /**
     * 半径
     */
    private float radius = 0;

    /**
     * 线段宽度
     */
    private float streakWidth = 6;
    /**
     * 线段长度
     */
    private float streakLength = 4;
    /**
     * 线段间距
     */
    private float streakSpace = 20;

    /**
     * 透明度位置
     */
    private int alphaPosition = 0;
    /**
     * 旋转的单位角度
     */
    private float unitAngle = 90f / 4f;
    /**
     * 开始角度
     */
    private int startAlpha = 50;
    /**
     * 结束角度
     */
    private int endAlpha = 255;
    /**
     * 持续时间
     */
    private long duration = 350;

    /**
     * 文字画笔
     */
    private Paint textPaint;
    /**
     * 文本
     */
    private String text = "测试中...";
    /**
     * 文本显示
     */
    private int textVisibility = View.GONE;
    /**
     * 文字大小
     */
    private float textSize = dpToPx(12);
    /**
     * 方向
     */
    private int orientation = VERTICAL;
    /**
     * 透明度值
     */
    private List<Integer> alphas;
    /**
     * 动画值
     */
    private ValueAnimator animator;

    private int widthSpecMode;
    private int heightSpecMode;
    private int widthSpecSize;
    private int heightSpecSize;
    private boolean isStart;

    public LoadingView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0);
        radius = typedArray.getDimension(R.styleable.LoadingView_android_radius, radius);
        orientation = typedArray.getInteger(R.styleable.LoadingView_android_orientation, orientation);
        textSize = typedArray.getDimension(R.styleable.LoadingView_android_textSize, textSize);
        CharSequence charSequence = typedArray.getText(R.styleable.LoadingView_android_text);
        text = charSequence == null || charSequence.length() == 0 ? "" : charSequence.toString();
        textVisibility = text == null || text.length() == 0 ? View.GONE : View.VISIBLE;
        textVisibility = typedArray.getInt(R.styleable.LoadingView_textVisibility, textVisibility);
        textColor = context.getResources().getColor(R.color.colorLoadingText);
        textColor = typedArray.getColor(R.styleable.LoadingView_android_textColor, textColor);
        streakColor = context.getResources().getColor(R.color.colorLoadingStreak);
        streakColor = typedArray.getColor(R.styleable.LoadingView_streakColor, streakColor);
        startAlpha = typedArray.getInt(R.styleable.LoadingView_startAlpha, startAlpha);
        endAlpha = typedArray.getInt(R.styleable.LoadingView_endAlpha, endAlpha);
        duration = typedArray.getInteger(R.styleable.LoadingView_duration, (int) duration);
        unitAngle = typedArray.getFloat(R.styleable.LoadingView_unitAngle, unitAngle);
        streakWidth = typedArray.getDimension(R.styleable.LoadingView_streakWidth, streakWidth);
        streakSpace = typedArray.getDimension(R.styleable.LoadingView_streakSpace, streakSpace);
        typedArray.recycle();
        //初始化透明度数据
        alphas = buildList(startAlpha, endAlpha, (int) (360 / unitAngle - 1));
        int alphaSize = alphas.size();
        initAnimator(alphaSize - 1);
        textSize = textSize == 0 ? dpToPx(13) : textSize;
        //初始化线条画笔
        streakPaint = new Paint();
        streakPaint.setStyle(Paint.Style.STROKE);
        streakPaint.setStrokeCap(Paint.Cap.ROUND);
        streakPaint.setColor(streakColor);
        streakPaint.setAntiAlias(true);
        streakPaint.setStrokeWidth(streakWidth);
        //初始化文字画笔
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
    }

    /**
     * 初始化动画值
     *
     * @param value 透明值数据Size
     */
    protected void initAnimator(int value) {
        animator = ValueAnimator.ofInt(value - 1);
        animator.setDuration(duration);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alphaPosition = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isStart = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                isStart = false;
            }
        });
    }

    /**
     * 转换文字大小
     *
     * @param size 大小
     * @return
     */
    public float dpToPx(int size) {
        return size * Resources.getSystem().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        width = widthSpecSize;
        height = heightSpecSize;
        int sideSpecSize = widthSpecSize >= heightSpecSize ? heightSpecSize : widthSpecSize;
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            sideSpecSize = (int) dpToPx(65);
            width = widthSpecSize;
            height = sideSpecSize;
        }
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode != MeasureSpec.AT_MOST) {
            width = sideSpecSize;
            height = heightSpecSize;
        }
        if (widthSpecMode != MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
            height = sideSpecSize;
        }
        initCenterSideStreakParams();
        //文字宽高
        int textWH[] = measureText(textPaint, text);

        int requireWH[] = getRequireWH(textWH, radius, streakSpace, orientation);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            width = requireWH[0] >= width ? width : requireWH[0];
            initCenterSideStreakParams();
        }
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode != MeasureSpec.AT_MOST) {
            width = requireWH[0] >= width ? width : requireWH[0];
            initCenterSideStreakParams();
        }
        if (widthSpecMode != MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {

        }
        //获取中心点
        if (textVisibility == GONE) {
            centerY = height / 2;
        }
        if (textVisibility == VISIBLE) {
            if (orientation == HORIZONTAL) {
                centerX = radius;
            }
            if (orientation == VERTICAL) {
                centerY -= textWH[1];
            }
        }
        centerX += getPaddingLeft() - getPaddingRight();
        centerY += getPaddingTop() - getPaddingBottom();
        //设置宽高
        setMeasuredDimension(width, height);
    }

    /**
     * 初始化中心点/边长/线段参数
     */
    private void initCenterSideStreakParams() {
        centerX = width / 2;
        centerY = height / 2;
        sideLength = width >= height ? height : width;
        radius = sideLength / 2.0f;
        streakLength = streakLength == 0 ? radius : streakLength;
        streakSpace = streakSpace == 0 ? (radius - streakLength) / 2.0f : streakSpace;
    }

    /**
     * 获取需求宽高
     *
     * @return
     */
    private int[] getRequireWH(int[] textWH, float radius, float streakSpace, int orientation) {
        int requireWidth = (int) (radius + textWH[0] + streakSpace * (orientation == HORIZONTAL ? 4.0f : 1.0f));
        int requireHeight = (int) (radius + textWH[1] + streakSpace * 3.0f);
        return new int[]{requireWidth, requireHeight};
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLoading(canvas);
        drawText(canvas);
    }

    /**
     * 绘制Loading
     *
     * @param canvas 画布
     */
    protected void drawLoading(Canvas canvas) {
        int index = -1;
        for (float angle = 0; angle < 360; angle += unitAngle) {
            double radians;
            index++;
            setStreakAlpha(index);
            float startX, startY, endX, endY;
            radians = Math.toRadians(angle - 2 * Math.PI);
            startX = (float) (Math.sin(radians) * streakSpace);
            startY = (float) (Math.cos(radians) * streakSpace);
            endX = (float) (Math.sin(radians) * (streakLength + streakSpace));
            endY = (float) (Math.cos(radians) * (streakLength + streakSpace));
            canvas.drawLine(centerX + startX, centerY - startY, centerX + endX, centerY - endY, streakPaint);
        }
    }

    /**
     * 绘制文本
     *
     * @param canvas 画布
     */
    protected void drawText(Canvas canvas) {
        if (isTextEmpty() || textVisibility == GONE) {
            return;
        }
        int wh[] = measureText(textPaint, text);
        if (orientation == HORIZONTAL) {
            canvas.drawText(text, centerX + streakSpace + streakLength + streakSpace, centerY + wh[1] / 2, textPaint);
        }
        if (orientation == VERTICAL) {
            canvas.drawText(text, centerX - wh[0] / 2, centerY + streakSpace + streakLength + streakSpace + wh[1], textPaint);
        }
    }

    /**
     * 设置方向
     *
     * @param orientation
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        invalidate();
    }

    /**
     * 是否文字为空
     *
     * @return
     */
    public boolean isTextEmpty() {
        return text == null || text.length() == 0;
    }

    /**
     * 测量文本
     *
     * @param paint
     * @param content
     * @return
     */
    protected int[] measureText(Paint paint, String content) {
        if (content == null || content.length() == 0) {
            return new int[]{0, 0};
        }
        Rect rect = new Rect();
        paint.getTextBounds(content, 0, content.length(), rect);
        return new int[]{rect.width(), rect.height()};
    }


    /**
     * 构建列表
     *
     * @param start 开始值
     * @param end   结束值
     * @param count 需要的个数
     * @return
     */
    protected List<Integer> buildList(int start, int end, int count) {
        List<Integer> alphas = new ArrayList<>();
        int value = (end - start) / count;
        for (int i = start; i < end; i += value) {
            alphas.add(i);
        }
        return alphas;
    }

    /**
     * 重组列表
     *
     * @param list     透明值列表
     * @param position 位置
     * @return
     */
    protected List<Integer> regroupList(List<Integer> list, int position) {
        List<Integer> groups = new ArrayList<>();
        if (position > 0) {
            groups.addAll(list.subList(position, list.size() - 1));
            List<Integer> remaining = list.subList(0, position);
            groups.addAll(remaining);
        } else {
            groups.addAll(list);
        }
        Collections.reverse(groups);
        return groups;
    }

    /**
     * 设置透明度
     *
     * @param index
     */
    private void setStreakAlpha(int index) {
        List<Integer> list = regroupList(alphas, alphaPosition);
        if (index < list.size()) {
            streakPaint.setAlpha(list.get(index));
        }
    }

    /**
     * 开始
     */
    public void start() {
        if (animator != null && !isStart) {
            animator.start();
        }
    }

    /**
     * 取消
     */
    public void cancel() {
        if (animator != null) {
            animator.cancel();
        }
        isStart = false;
    }

    /**
     * 获取线段画笔
     *
     * @return
     */
    public Paint getStreakPaint() {
        return streakPaint;
    }

    /**
     * 设置线段画笔
     *
     * @param streakPaint
     */
    public void setStreakPaint(Paint streakPaint) {
        this.streakPaint = streakPaint;
        invalidate();
    }

    /**
     * 获取线段颜色
     *
     * @return
     */
    public int getStreakColor() {
        return streakColor;
    }

    /**
     * 设置线段颜色
     *
     * @param streakColor
     */
    public void setStreakColor(int streakColor) {
        this.streakColor = streakColor;
        invalidate();
    }

    /**
     * 获取文字颜色
     *
     * @return
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * 设置文字颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    /**
     * 设置宽度
     *
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * 设置高度
     *
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
        invalidate();
    }

    /**
     * 获取中心点x
     *
     * @return
     */
    public float getCenterX() {
        return centerX;
    }

    /**
     * 设置中心x
     *
     * @param centerX
     */
    public void setCenterX(float centerX) {
        this.centerX = centerX;
        invalidate();
    }

    /**
     * 获取中心点Y
     *
     * @return
     */
    public float getCenterY() {
        return centerY;
    }

    /**
     * 设置中心点Y
     *
     * @param centerY
     */
    public void setCenterY(float centerY) {
        this.centerY = centerY;
        invalidate();
    }

    /**
     * 获取边长，按正方形算
     *
     * @return
     */
    public float getSideLength() {
        return sideLength;
    }

    /**
     * 设置边长
     *
     * @param sideLength
     */
    public void setSideLength(float sideLength) {
        this.sideLength = sideLength;
        invalidate();
    }

    /**
     * 获取半径
     *
     * @return
     */
    public float getRadius() {
        return radius;
    }

    /**
     * 设置半径
     *
     * @param radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
    }

    /**
     * 获取线段宽度
     *
     * @return
     */
    public float getStreakWidth() {
        return streakWidth;
    }

    /**
     * 设置线段宽度
     *
     * @param streakWidth
     */
    public void setStreakWidth(float streakWidth) {
        this.streakWidth = streakWidth;
        invalidate();
    }

    /**
     * 获取线段长度
     *
     * @return
     */
    public float getStreakLength() {
        return streakLength;
    }

    /**
     * 设置线段长度
     *
     * @param streakLength
     */
    public void setStreakLength(float streakLength) {
        this.streakLength = streakLength;
        invalidate();
    }

    /**
     * 获取线段间隔
     *
     * @return
     */
    public float getStreakSpace() {
        return streakSpace;
    }

    /**
     * 设置线段间隔
     *
     * @param streakSpace
     */
    public void setStreakSpace(float streakSpace) {
        this.streakSpace = streakSpace;
        invalidate();
    }

    /**
     * 获取透明度位置
     *
     * @return
     */
    public int getAlphaPosition() {
        return alphaPosition;
    }

    /**
     * 设置透明度位置
     *
     * @param alphaPosition
     */
    public void setAlphaPosition(int alphaPosition) {
        this.alphaPosition = alphaPosition;
        invalidate();
    }

    /**
     * 获取单位角度
     *
     * @return
     */
    public float getUnitAngle() {
        return unitAngle;
    }

    /**
     * 设置单位角度
     *
     * @param unitAngle
     */
    public void setUnitAngle(float unitAngle) {
        this.unitAngle = unitAngle;
        invalidate();
    }

    /**
     * 获取开始得透明度
     *
     * @return
     */
    public int getStartAlpha() {
        return startAlpha;
    }

    /**
     * 设置开始透明度
     *
     * @param startAlpha
     */
    public void setStartAlpha(int startAlpha) {
        this.startAlpha = startAlpha;
        invalidate();
    }

    /**
     * 获取结束透明度
     *
     * @return
     */
    public int getEndAlpha() {
        return endAlpha;
    }

    /**
     * 设置结束透明度
     *
     * @param endAlpha
     */
    public void setEndAlpha(int endAlpha) {
        this.endAlpha = endAlpha;
        invalidate();
    }

    /**
     * 获取动画持续时间
     *
     * @return
     */
    public long getDuration() {
        return duration;
    }

    /**
     * 设置动画持续时间
     *
     * @param duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
        invalidate();
    }

    /**
     * 获取文字画笔
     *
     * @return
     */
    public Paint getTextPaint() {
        return textPaint;
    }

    /**
     * 设置文字画笔
     *
     * @param textPaint
     */
    public void setTextPaint(Paint textPaint) {
        this.textPaint = textPaint;
        invalidate();
    }

    /**
     * 获取文字
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * 设置文字
     *
     * @param text
     */
    public void setText(String text) {
        this.text = text;
        textVisibility = isTextEmpty() ? GONE : VISIBLE;
        invalidate();
    }

    /**
     * 获取文字大小
     *
     * @return
     */
    public float getTextSize() {
        return textSize;
    }

    /**
     * 设置文字大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = dpToPx(textSize);
        invalidate();
    }

    /**
     * 获取方向
     *
     * @return
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * 获取透明度数据
     *
     * @return
     */
    public List<Integer> getAlphas() {
        return alphas;
    }

    /**
     * 设置透明度数据
     *
     * @param alphas
     */
    public void setAlphas(List<Integer> alphas) {
        this.alphas = alphas;
        invalidate();
    }

    /**
     * 获取动画值
     *
     * @return
     */
    public ValueAnimator getAnimator() {
        return animator;
    }

    /**
     * 设置动画值
     *
     * @param animator
     */
    public void setAnimator(ValueAnimator animator) {
        this.animator = animator;
        invalidate();
    }
}
