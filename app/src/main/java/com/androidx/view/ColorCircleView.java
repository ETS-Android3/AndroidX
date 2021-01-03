package com.androidx.view;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.androidx.R;

/**
 * Author: Relin
 * Description:颜色圆圈控件
 * Date:2020/12/14 22:01
 */
public class ColorCircleView extends View {

    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 中心点
     */
    private float centerX, centerY;
    /**
     * 背景半径
     */
    private float circleRadius = dpToPx(18);
    /**
     * 背景颜色
     */
    private int circleColor = Color.WHITE;
    /**
     * 背景阴影半径
     */
    private float shadowRadius = dpToPx(5);
    /**
     * 背景阴影颜色
     */
    private int shadowColor = Color.parseColor("#5F000000");
    /**
     * 扇形半径
     */
    private float arcRadius = dpToPx(9);
    /**
     * 扇形组合颜色
     */
    private int arcSchemeColors[] = {
            Color.parseColor("#20A5F7"),
            Color.parseColor("#EA4335"),
            Color.parseColor("#34A853"),
            Color.parseColor("#FBBC05")};
    /**
     * 扇形初始颜色
     */
    private int arcColor = arcSchemeColors[0];
    /**
     * 扇形线宽度
     */
    private float arcStrokeWidth = dpToPx(2.5F);
    /**
     * 弧度缺口大小
     */
    private float arcGapAngle = 90;
    /**
     * 扇形终点弧度
     */
    private float arcSweepAngle = 360;
    /**
     * 扇形起始弧度
     */
    private float arcStartAngle = 0;
    /**
     * 动画重复次数 - 记录作用
     */
    private int repeatCount = 0;
    /**
     * 动画对象
     */
    private ValueAnimator animator;
    /**
     * 是否开始
     */
    private boolean isStart;
    /**
     * 单次动画时间
     */
    private int duration = 600;
    /**
     * 动画集
     */
    private AnimatorSet set;


    public ColorCircleView(Context context) {
        super(context);
        initAttrs(context, null);
    }

    public ColorCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public ColorCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    /**
     * 初始化Attrs属性值
     *
     * @param context
     * @param attrs   属性值
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorCircleView);
            circleRadius = typedArray.getDimension(R.styleable.ColorCircleView_circleRadius, circleRadius);
            circleColor = typedArray.getColor(R.styleable.ColorCircleView_circleColor, circleColor);
            shadowRadius = typedArray.getDimension(R.styleable.ColorCircleView_shadowRadius, shadowRadius);
            shadowColor = typedArray.getColor(R.styleable.ColorCircleView_shadowColor, shadowColor);
            arcRadius = typedArray.getDimension(R.styleable.ColorCircleView_arcRadius, arcRadius);
            attrsSchemeColors(typedArray);
            arcStrokeWidth = typedArray.getDimension(R.styleable.ColorCircleView_arcStrokeWidth, arcStrokeWidth);
            arcGapAngle = typedArray.getFloat(R.styleable.ColorCircleView_arcGapAngle, arcGapAngle);
            duration = typedArray.getInt(R.styleable.ColorCircleView_duration, duration);
            typedArray.recycle();
        }
    }

    /**
     * Attrs组合颜色值
     *
     * @param typedArray
     */
    private void attrsSchemeColors(TypedArray typedArray) {
        String schemeColors = typedArray.getString(R.styleable.ColorCircleView_arcSchemeColors);
        if (!TextUtils.isEmpty(schemeColors) && schemeColors.contains(",")) {
            String split[] = schemeColors.split(",");
            arcSchemeColors = new int[split.length];
            for (int i = 0; i < split.length; i++) {
                arcSchemeColors[i] = Color.parseColor(split[i]);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = widthSpecSize;
        int height = heightSpecSize;
        int needWidth = (int) (arcRadius * 2 + shadowRadius * 2 + getPaddingLeft() + getPaddingRight());
        int needHeight = (int) (arcRadius * 2 + shadowRadius * 2 + getPaddingTop() + getPaddingBottom());
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            width = needWidth;
            height = needHeight;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {//wrap-content
            width = needWidth;
            height = heightSpecSize;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
            height = needHeight;
        }
        setMeasuredDimension(width, height);
        centerY = getMeasuredHeight() / 2;
        centerX = getMeasuredWidth() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLoadingBackground(canvas);
        drawLoadingArc(canvas, arcColor, arcStartAngle, arcSweepAngle);
        startSweepAnimation();
    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawLoadingBackground(Canvas canvas) {
        paint = createPaint(circleColor, Paint.Style.FILL, 0);
        paint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
        canvas.drawCircle(centerX, centerY, circleRadius, paint);
    }

    /**
     * 绘制弧线
     *
     * @param canvas     画布
     * @param arcColor   弧线颜色
     * @param startAngle 开始弧度
     * @param sweepAngle 扫描弧度
     */
    private void drawLoadingArc(Canvas canvas, int arcColor, float startAngle, float sweepAngle) {
        paint = createPaint(arcColor, Paint.Style.STROKE, arcStrokeWidth);
        RectF oval = new RectF(centerX - arcRadius, centerY - arcRadius, centerX + arcRadius, centerY + arcRadius);
        canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
    }

    /**
     * 开启动画
     */
    private void startSweepAnimation() {
        if (set == null && isStart) {
            ValueAnimator start = createSweepAngleAnimator(0, 360, false);
            ValueAnimator startAngle = createStartAngleAnimator(0, 360);
            ValueAnimator sweepAngle = createSweepAngleAnimator(360, 0, true);
            set = new AnimatorSet();
            set.play(startAngle).with(sweepAngle).after(start);
            set.setDuration(duration);
            int poasition = repeatCount % arcSchemeColors.length;
            arcColor = arcSchemeColors[poasition];
            if (onSchemeColorChangeListener != null) {
                onSchemeColorChangeListener.onSchemeColor(arcSchemeColors, poasition);
            }
            set.start();
            repeatCount++;
        }
        if (set != null && !set.isRunning()) {
            set = null;
            arcGapAngle += 90;
            if (arcSweepAngle == 360) {
                arcGapAngle = 90;
            }
        }
    }

    /**
     * 创建扇形扫过角度动画
     *
     * @param start     开始角度
     * @param end       结束角度
     * @param isRecycle 回收
     * @return
     */
    private ValueAnimator createSweepAngleAnimator(float start, float end, final boolean isRecycle) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                arcSweepAngle = isRecycle ? animatedValue + arcGapAngle : animatedValue;
                arcSweepAngle = animatedValue;
                postInvalidate();
            }
        });
        animator.setDuration(duration);
        return animator;
    }

    /**
     * 创建扇形开始角度动画
     *
     * @param start 开始角度
     * @param end   结束角度
     * @return
     */
    private ValueAnimator createStartAngleAnimator(float start, float end) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                arcStartAngle = animatedValue + arcGapAngle;
                postInvalidate();
            }
        });
        animator.setDuration(duration);
        return animator;
    }

    /**
     * 创建画笔
     *
     * @param color       颜色
     * @param style       样式
     * @param strokeWidth 宽度
     * @return
     */
    private Paint createPaint(int color, Paint.Style style, float strokeWidth) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(style);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        return paint;
    }

    public float dpToPx(float dp) {
        return dp * getScreenDensity();
    }

    public float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    /**
     * 开启动画
     */
    public void start() {
        this.isStart = true;
        postInvalidate();
    }

    /**
     * 取消动画
     */
    public void cancel() {
        this.isStart = false;
        if (animator != null) {
            animator.removeAllUpdateListeners();
            animator.cancel();
            animator = null;
        }
        postInvalidate();
    }

    /**
     * 获取圆圈半径
     *
     * @return
     */
    public float getCircleRadius() {
        return circleRadius;
    }

    /**
     * 设置圆圈半径
     *
     * @param circleRadius
     */
    public void setCircleRadius(float circleRadius) {
        this.circleRadius = circleRadius;
        invalidate();
    }

    /**
     * 获取圆圈颜色
     *
     * @return
     */
    public int getCircleColor() {
        return circleColor;
    }

    /**
     * 设置圆圈颜色
     *
     * @param circleColor
     */
    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;
        invalidate();
    }

    /**
     * 获取阴影颜色
     *
     * @return
     */
    public float getShadowRadius() {
        return shadowRadius;
    }

    /**
     * 设置阴影颜色
     *
     * @param shadowRadius
     */
    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
        invalidate();
    }

    /**
     * 获取阴影颜色
     *
     * @return
     */
    public int getShadowColor() {
        return shadowColor;
    }

    /**
     * 设置阴影颜色
     *
     * @param shadowColor
     */
    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        invalidate();
    }

    /**
     * 扇形半径
     *
     * @return
     */
    public float getArcRadius() {
        return arcRadius;
    }

    /**
     * 设置扇形半径
     *
     * @param arcRadius
     */
    public void setArcRadius(float arcRadius) {
        this.arcRadius = arcRadius;
        invalidate();
    }

    /**
     * 获取颜色集合
     *
     * @return
     */
    public int[] getArcSchemeColors() {
        return arcSchemeColors;
    }

    /**
     * 设置颜色集合
     *
     * @param arcSchemeColors
     */
    public void setArcSchemeColors(int[] arcSchemeColors) {
        this.arcSchemeColors = arcSchemeColors;
    }

    /**
     * 获取扇形颜色
     *
     * @return
     */
    public int getArcColor() {
        return arcColor;
    }

    /**
     * 设置扇形颜色
     *
     * @param arcColor
     */
    public void setArcColor(int arcColor) {
        this.arcColor = arcColor;
        invalidate();
    }

    /**
     * 获取扇形线宽度
     *
     * @return
     */
    public float getArcStrokeWidth() {
        return arcStrokeWidth;
    }

    /**
     * 设置扇形线宽度
     *
     * @param arcStrokeWidth
     */
    public void setArcStrokeWidth(float arcStrokeWidth) {
        this.arcStrokeWidth = arcStrokeWidth;
        invalidate();
    }

    /**
     * 获取扇形间距角度
     *
     * @return
     */
    public float getArcGapAngle() {
        return arcGapAngle;
    }

    /**
     * 设置扇形间距角度
     *
     * @param arcGapAngle
     */
    public void setArcGapAngle(float arcGapAngle) {
        this.arcGapAngle = arcGapAngle;
        invalidate();
    }

    /**
     * 获取扇形扫过角度
     *
     * @return
     */
    public float getArcSweepAngle() {
        return arcSweepAngle;
    }

    /**
     * 设置扇形扫过角度
     *
     * @param arcSweepAngle
     */
    public void setArcSweepAngle(float arcSweepAngle) {
        this.arcSweepAngle = arcSweepAngle;
        invalidate();
    }

    /**
     * 获取扇形开始角度
     *
     * @return
     */
    public float getArcStartAngle() {
        return arcStartAngle;
    }

    /**
     * 设置扇形开始角度
     *
     * @param arcStartAngle
     */
    public void setArcStartAngle(float arcStartAngle) {
        this.arcStartAngle = arcStartAngle;
        invalidate();
    }

    /**
     * 获取持续时间
     *
     * @return
     */
    public int getDuration() {
        return duration;
    }

    /**
     * 设置持续时间
     *
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
        invalidate();
    }

    /**
     * 颜色监听
     */
    private OnSchemeColorChangeListener onSchemeColorChangeListener;

    /**
     * 设置颜色变化监听
     *
     * @param onSchemeColorChangeListener
     */
    public void setOnSchemeColorChangeListener(OnSchemeColorChangeListener onSchemeColorChangeListener) {
        this.onSchemeColorChangeListener = onSchemeColorChangeListener;
    }

    /**
     * 颜色变化监听
     */
    public interface OnSchemeColorChangeListener {

        /**
         * 颜色值变化
         *
         * @param colors   颜色集合
         * @param position 变化位置
         */
        void onSchemeColor(int colors[], int position);

    }

}
