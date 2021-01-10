package com.androidx.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.androidx.R;

/**
 * Author: Relin
 * Description:计时按钮<br/>
 * Date:2021/01/08 12:26
 */
public class CircleTimerView extends View {

    /**
     * 宽度
     */
    private float width;
    /**
     * 高度
     */
    private float height;
    /**
     * 内部半径
     */
    private float outsideRadius = 20F;
    /**
     * 内部颜色
     */
    private int outsideColor = Color.parseColor("#575757");
    /**
     * 外部半径
     */
    private float insideRadius = 20F;
    /**
     * 外部颜色
     */
    private int insideColor = Color.parseColor("#FFFFFF");
    /**
     * 线条宽度
     */
    private float strokeWidth = 20;
    /**
     * 线条颜色
     */
    private int strokeColor = Color.parseColor("#1EBFB4");
    /**
     * 开始角度
     */
    private float sweepAngle = 0f;
    /**
     * 持续时间
     */
    private int duration = 15 * 1000;
    /**
     * 时间
     */
    private int millisInFuture = 0;
    /**
     * 动画值
     */
    private ValueAnimator animator;
    /**
     * 监听
     */
    private OnTimerChangeListener listener;
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 是否按钮
     */
    private boolean button = true;
    /**
     * 是否开始
     */
    private boolean start = false;
    /**
     * 按钮时间
     */
    private long touchDownTime = 0;


    public CircleTimerView(Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public CircleTimerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public CircleTimerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    /**
     * 初始化参数
     *
     * @param context 上下文
     * @param attrs   参数
     */
    private void initAttributeSet(Context context, AttributeSet attrs) {
        paint = new Paint();
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleTimerView);
            outsideRadius = typedArray.getDimension(R.styleable.CircleTimerView_outsideRadius, outsideRadius);
            outsideColor = typedArray.getColor(R.styleable.CircleTimerView_outsideColor, outsideColor);
            insideRadius = typedArray.getDimension(R.styleable.CircleTimerView_insideRadius, insideRadius);
            insideColor = typedArray.getColor(R.styleable.CircleTimerView_insideColor, insideColor);
            strokeWidth = typedArray.getDimension(R.styleable.CircleTimerView_strokeWidth, strokeWidth);
            strokeColor = typedArray.getColor(R.styleable.CircleTimerView_strokeColor, strokeColor);
            duration = typedArray.getInt(R.styleable.CircleTimerView_duration, duration);
            millisInFuture = typedArray.getInt(R.styleable.CircleTimerView_millisInFuture, millisInFuture);
            button = typedArray.getBoolean(R.styleable.CircleTimerView_button, button);
            typedArray.recycle();
        }
        setMillisInFuture(millisInFuture);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        outsideRadius = (width >= height ? height : width) / 2;
        insideRadius = outsideRadius - strokeWidth;
        drawOutsideCircle(canvas, paint, outsideRadius, outsideColor);
        drawInsideCircle(canvas, paint, insideRadius, insideColor);
        drawStroke(canvas, paint, strokeWidth, strokeColor, sweepAngle);
    }

    /**
     * 绘制外部圆圈
     *
     * @param canvas        画布
     * @param paint         画笔
     * @param outsideRadius 外部圆半径
     * @param outsideColor  外部圆颜色
     */
    protected void drawOutsideCircle(Canvas canvas, Paint paint, float outsideRadius, int outsideColor) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(outsideColor);
        canvas.drawCircle(width / 2, height / 2, outsideRadius, paint);
    }

    /**
     * 获知内部圆圈
     *
     * @param canvas       画布
     * @param paint        画笔
     * @param insideRadius 内部圆半径
     * @param insideColor  内部圆颜色
     */
    protected void drawInsideCircle(Canvas canvas, Paint paint, float insideRadius, int insideColor) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(insideColor);
        canvas.drawCircle(width / 2, height / 2, insideRadius, paint);
    }

    /**
     * 绘制线条
     *
     * @param canvas      画布
     * @param paint       画笔
     * @param strokeWidth 线条宽度
     * @param strokeColor 线条颜色
     * @param sweepAngle  扫过角度
     */
    protected void drawStroke(Canvas canvas, Paint paint, float strokeWidth, int strokeColor, float sweepAngle) {
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(strokeColor);
        paint.setStrokeWidth(strokeWidth);
        RectF rectF = new RectF(width / 2 - insideRadius - strokeWidth / 2, height / 2 - insideRadius - strokeWidth / 2, width / 2 + insideRadius + strokeWidth / 2, height / 2 + insideRadius + strokeWidth / 2);
        canvas.drawArc(rectF, -90, sweepAngle, false, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (System.currentTimeMillis() - touchDownTime < 500) {
                    return false;
                }
                touchDownTime = System.currentTimeMillis();
                return true;
            case MotionEvent.ACTION_UP:
                start = !start;
                if (start) {
                    start();
                } else {
                    stop();
                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
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
     * 设置未来时间
     *
     * @param millisInFuture
     */
    public void setMillisInFuture(int millisInFuture) {
        this.millisInFuture = millisInFuture;
        sweepAngle = 360F * millisInFuture / duration;
        invalidate();
    }

    /**
     * 开始进度动画
     */
    public void start() {
        if (duration <= 0) {
            return;
        }
        initAnimator(duration);
        if (listener != null) {
            listener.onTimerStart();
        }
        animator.start();
    }

    /**
     * 停止
     */
    public void stop() {
        if (animator != null) {
            animator.cancel();
        }
        if (duration <= 0) {
            if (listener != null) {
                listener.onTimerFinish();
            }
        }
    }

    /**
     * 初始化Animator
     *
     * @param duration 持续时间
     */
    protected void initAnimator(final long duration) {
        if (animator != null && animator.isStarted()) {
            animator.cancel();
            animator = null;
        }
        animator = ValueAnimator.ofFloat(0f, 360);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                sweepAngle = (float) valueAnimator.getAnimatedValue();
                invalidate();
                if (listener != null) {
                    listener.onTimerChanged(duration, (long) (duration * sweepAngle / 360F));
                }
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                invalidate();
                if (listener != null) {
                    listener.onTimerFinish();
                }
                start = false;
            }
        });
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnTimerChangeListener(OnTimerChangeListener listener) {
        this.listener = listener;
    }

    public interface OnTimerChangeListener {

        /**
         * 计时开始
         */
        void onTimerStart();

        /**
         * 计时改变
         *
         * @param duration
         * @param millisInFuture
         */
        void onTimerChanged(long duration, long millisInFuture);

        /**
         * 计时结束
         */
        void onTimerFinish();

    }

    /**
     * 获取外部半径
     *
     * @return
     */
    public float getOutsideRadius() {
        return outsideRadius;
    }

    /**
     * 设置外部半径
     *
     * @param outsideRadius
     */
    public void setOutsideRadius(float outsideRadius) {
        this.outsideRadius = outsideRadius;
        invalidate();
    }

    /**
     * 获取外部颜色
     *
     * @return
     */
    public int getOutsideColor() {
        return outsideColor;
    }

    /**
     * 设置外部颜色
     *
     * @param outsideColor
     */
    public void setOutsideColor(int outsideColor) {
        this.outsideColor = outsideColor;
        invalidate();
    }

    /**
     * 获取内部半径
     *
     * @return
     */
    public float getInsideRadius() {
        return insideRadius;
    }

    /**
     * 设置内部半径
     *
     * @param insideRadius
     */
    public void setInsideRadius(float insideRadius) {
        this.insideRadius = insideRadius;
        invalidate();
    }

    /**
     * 获取内部颜色
     *
     * @return
     */
    public int getInsideColor() {
        return insideColor;
    }

    /**
     * 设置内部颜色
     *
     * @param insideColor
     */
    public void setInsideColor(int insideColor) {
        this.insideColor = insideColor;
        invalidate();
    }

    /**
     * 获取线条宽度
     *
     * @return
     */
    public float getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * 设置线条宽度
     *
     * @param strokeWidth
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    /**
     * 获取线条颜色
     *
     * @return
     */
    public int getStrokeColor() {
        return strokeColor;
    }

    /**
     * 设置线条颜色
     *
     * @param strokeColor
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    /**
     * 获取角度
     *
     * @return
     */
    public float getSweepAngle() {
        return sweepAngle;
    }

    /**
     * 设置角度
     *
     * @param sweepAngle
     */
    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    /**
     * 获取时间
     *
     * @return
     */
    public int getDuration() {
        return duration;
    }

    /**
     * 获取显示时间
     *
     * @return
     */
    public int getMillisInFuture() {
        return millisInFuture;
    }

    /**
     * 获取动画值对象
     *
     * @return
     */
    public ValueAnimator getAnimator() {
        return animator;
    }

    /**
     * 设置动画值对象
     *
     * @param animator
     */
    public void setAnimator(ValueAnimator animator) {
        this.animator = animator;
    }

    /**
     * 获取画笔
     *
     * @return
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * 设置画笔
     *
     * @param paint
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    /**
     * 是否是按钮
     *
     * @return
     */
    public boolean isButton() {
        return button;
    }

    /**
     * 设置按钮状态
     *
     * @param button
     */
    public void setButton(boolean button) {
        this.button = button;
    }

    /**
     * 计时器状态
     *
     * @return
     */
    public boolean isStart() {
        return start;
    }

    /**
     * 设置计时器状态
     *
     * @param start
     */
    public void setStart(boolean start) {
        this.start = start;
    }
}
