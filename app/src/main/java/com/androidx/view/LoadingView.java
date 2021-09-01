package com.androidx.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
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
public class LoadingView extends View implements ValueAnimator.AnimatorUpdateListener {

    /**
     * 线条画笔
     */
    private Paint streakPaint;
    /**
     * 线条颜色
     */
    private int streakColor;
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
    private float streakWidth = 8;
    /**
     * 线段长度
     */
    private float streakLength = 4;
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
    private int duration = 350;
    /**
     * 透明度值
     */
    private List<Integer> alphas;
    /**
     * 动画值
     */
    private ValueAnimator animator;
    /**
     * 是否开始
     */
    private boolean loading;

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
        streakColor = context.getResources().getColor(R.color.colorLoadingStreak);
        streakColor = typedArray.getColor(R.styleable.LoadingView_streakColor, streakColor);
        startAlpha = typedArray.getInt(R.styleable.LoadingView_startAlpha, startAlpha);
        endAlpha = typedArray.getInt(R.styleable.LoadingView_endAlpha, endAlpha);
        duration = typedArray.getInteger(R.styleable.LoadingView_duration, duration);
        unitAngle = typedArray.getFloat(R.styleable.LoadingView_unitAngle, unitAngle);
        streakWidth = typedArray.getDimension(R.styleable.LoadingView_streakWidth, streakWidth);
        typedArray.recycle();
        //初始化透明度数据
        alphas = buildList(startAlpha, endAlpha, (int) (360 / unitAngle - 1));
        int alphaSize = alphas.size();
        initAnimator(alphaSize - 1);
        start();
        //初始化线条画笔
        streakPaint = new Paint();
        streakPaint.setAntiAlias(true);
        streakPaint.setStyle(Paint.Style.FILL);
        streakPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerX = getMeasuredWidth() / 2F;
        centerY = getMeasuredHeight() / 2F;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        float diameter;
        if (height >= width) {
            diameter = width - getPaddingLeft() - getPaddingRight();
        } else {
            diameter = height - getPaddingTop() - getPaddingBottom();
        }
        radius = diameter * 0.90F / 2F;
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
        animator.addUpdateListener(this);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                loading = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                loading = false;
            }
        });
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        alphaPosition = (int) animation.getAnimatedValue();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLoading(canvas);
    }

    /**
     * 绘制Loading
     *
     * @param canvas 画布
     */
    protected void drawLoading(Canvas canvas) {
        streakPaint.setColor(streakColor);
        streakPaint.setStrokeWidth(streakWidth);
        int index = -1;
        for (float angle = 0; angle < 360; angle += unitAngle) {
            index++;
            setStreakAlpha(index);
            double radians = Math.toRadians(angle - 2 * Math.PI);
            float startX = (float) (Math.sin(radians) * (radius - streakLength));
            float startY = (float) (Math.cos(radians) * (radius - streakLength));
            float endX = (float) (Math.sin(radians) * radius);
            float endY = (float) (Math.cos(radians) * radius);
            canvas.drawLine(centerX + startX, centerY + startY, centerX + endX, centerY + endY, streakPaint);
        }
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
     * 是否正在加载
     *
     * @return
     */
    public boolean isLoading() {
        return loading;
    }

    /**
     * 开始
     */
    public void start() {
        if (animator != null && !isLoading()) {
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
        loading = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancel();
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
    public void setDuration(int duration) {
        this.duration = duration;
        invalidate();
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
