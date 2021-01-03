package com.androidx.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.androidx.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Relin
 * Description:状态提示<br/>
 * 成功：{@link StatusView#SUCCESS}<br/>
 * 警告：{@link StatusView#WARNING}<br/>
 * 网络：{@link StatusView#WIRELESS}
 * Date:2020/12/14 22:01
 */
public class StatusView extends View {

    /**
     * 成功
     */
    public static final int SUCCESS = 1;
    /**
     * 警告
     */
    public static final int WARNING = -1;
    /**
     * 网络
     */
    public static final int WIRELESS = 0;

    /**
     * 显示类型
     */
    private int status;

    /**
     * 中心点
     */
    private float centerX, centerY;
    /**
     * 宽高
     */
    private float width, height;
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 半径
     */
    private float radius = dpToPx(16);
    /**
     * 颜色
     */
    private int color;
    /**
     * 圆 - 线宽度
     */
    private float circleStrokeWidth = dpToPx(1.5f);

    /**
     * 警告 - 上方圆圈半径
     */
    private float warningTopRadius;
    /**
     * 警告 - 下方圆圈半径
     */
    private float warningBottomRadius;
    /**
     * 警告 - 圆点半径
     */
    private float warningDotRadius;

    /**
     * 网络 - 中心Y
     */
    private float wirelessCircleY;
    /**
     * 网络 - 中心X
     */
    private float wirelessCircleX;
    /**
     * 网络 - 个数
     */
    private int wirelessCount = 4;
    /**
     * 网络 - 线宽
     */
    private float wirelessStrokeWidth = dpToPx(15);
    /**
     * 网络 - 开始半径
     */
    private float wirelessStartRadius = dpToPx(5);
    /**
     * 网络 - 间距
     */
    private float wirelessPadding = dpToPx(5);
    /**
     * 网络 - 开始角度
     */
    private float wirelessStartAngle = -135;
    /**
     * 网络 - 角度
     */
    private float wirelessSweepAngle = Math.abs(wirelessStartAngle + 90) * 2;

    /**
     * 坐标点
     */
    private List<PathCoordinates> points;

    /**
     * 是否使用动画
     */
    private boolean isAnimator = false;
    /**
     * 动画持续时间
     */
    private int duration = 500;
    /**
     * 动画开始角度
     */
    private float animationSweepAngle = 0;
    /**
     * 外部圆动画
     */
    private ValueAnimator outCircleAnimator;
    /**
     * 路径动画
     */
    private ValueAnimator pathAnimator;


    public StatusView(Context context) {
        super(context);
        onStyledAttributes(context, null, 0, 0);
    }

    public StatusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onStyledAttributes(context, attrs, 0, 0);
    }

    public StatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onStyledAttributes(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onStyledAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 初始化xml属性
     *
     * @param context      上下文
     * @param attrs        属性
     * @param defStyleAttr 默认属性
     * @param defStyleRes  默认样式
     */
    protected void onStyledAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusView, defStyleAttr, defStyleRes);
        status = typedArray.getInt(R.styleable.StatusView_status, WIRELESS);
        radius = typedArray.getDimension(R.styleable.StatusView_radius, radius);
        isAnimator = typedArray.getBoolean(R.styleable.StatusView_isAnimator, isAnimator);
        color = typedArray.getColor(R.styleable.StatusView_android_color, context.getResources().getColor(R.color.colorAccent));
        circleStrokeWidth = typedArray.getDimension(R.styleable.StatusView_circleStrokeWidth, circleStrokeWidth);
        warningTopRadius = typedArray.getDimension(R.styleable.StatusView_warningTopRadius, warningTopRadius);
        warningBottomRadius = typedArray.getDimension(R.styleable.StatusView_warningBottomRadius, warningBottomRadius);
        warningDotRadius = typedArray.getDimension(R.styleable.StatusView_warningDotRadius, warningDotRadius);
        wirelessStrokeWidth = typedArray.getDimension(R.styleable.StatusView_wirelessStrokeWidth, wirelessStrokeWidth);
        wirelessCount = typedArray.getInt(R.styleable.StatusView_wirelessCount, wirelessCount);
        wirelessStartRadius = typedArray.getDimension(R.styleable.StatusView_wirelessStartAngle, wirelessStartRadius);
        wirelessPadding = typedArray.getDimension(R.styleable.StatusView_wirelessPadding, wirelessPadding);
        wirelessStartAngle = typedArray.getDimension(R.styleable.StatusView_wirelessStartAngle, wirelessStartAngle);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = widthSpecSize;
        int height = heightSpecSize;
        int needHeight = (int) (radius * 2);
        switch (status) {
            case WARNING:
            case SUCCESS:
                needHeight = (int) ((radius + circleStrokeWidth * 2 + getPaddingTop() + getPaddingBottom()) * 2);
                break;
            case WIRELESS:
                needHeight = (int) ((wirelessStartRadius + wirelessStrokeWidth + wirelessPadding) * wirelessCount);
                break;
        }
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            width = status == WIRELESS ? (int) (needHeight + wirelessStrokeWidth * 2) : needHeight;
            height = needHeight;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            width = needHeight;
            height = heightSpecSize;
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
            height = needHeight;
        }
        setMeasuredDimension(width, height);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        centerY = height / 2;
        centerX = width / 2;
        wirelessCircleX = width / 2;
        wirelessCircleY = height - getPaddingBottom();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        switch (status) {
            case SUCCESS:
                drawSuccessView(canvas, isAnimator);
                break;
            case WARNING:
                drawWarningView(canvas);
                break;
            case WIRELESS:
                drawWirelessView(canvas);
                break;
        }
    }

    /**
     * 创建画笔
     *
     * @param color 颜色
     * @return
     */
    private Paint createPaint(int color) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(circleStrokeWidth);
        return paint;
    }

    /**
     * 绘制外边缘圆圈
     *
     * @param canvas   画布
     * @param paint    画笔
     * @param animator 动画值
     */
    private void drawOutsideCircle(Canvas canvas, Paint paint, boolean animator) {
        if (animator) {
            drawOutsideCircle(canvas, paint, animationSweepAngle);
            if (outCircleAnimator != null) {
                return;
            }
            outCircleAnimator = ValueAnimator.ofFloat(360);
            outCircleAnimator.setDuration(duration);
            outCircleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    animationSweepAngle = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            outCircleAnimator.start();
        } else {
            drawOutsideCircle(canvas, paint, 360);
        }
    }


    /**
     * 绘制外边缘圆圈
     *
     * @param canvas     画布
     * @param paint      画笔
     * @param sweepAngle 弧度
     */
    private void drawOutsideCircle(Canvas canvas, Paint paint, float sweepAngle) {
        RectF outsideRectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(outsideRectF, -270, sweepAngle, false, paint);
    }

    private class PathCoordinates {
        float x;
        float y;

        public PathCoordinates(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private Path path = new Path();
    private float[] pos = new float[2];
    private int pathCount = 0;

    /**
     * 创建打钩路径
     *
     * @return
     */
    private Path createSuccessPath() {
        float part = radius * 2 / 4;
        if (points == null) {
            points = new ArrayList<>();
            points.add(new PathCoordinates(centerX - part - (part / 4), centerY + part / 4));
            points.add(new PathCoordinates(centerX - part, centerY));
            points.add(new PathCoordinates(centerX - part / 3, centerY + 2 * part / 3));
            points.add(new PathCoordinates(centerX + part + 1 * part / 4, centerY - 4 * part / 5));
        }
        Path path = new Path();
        for (int i = 0; i < points.size(); i++) {
            PathCoordinates coordinates = points.get(i);
            if (i == 0) {
                path.moveTo(coordinates.x, coordinates.y);
            } else {
                path.lineTo(coordinates.x, coordinates.y);
            }
        }
        return path;
    }

    /**
     * 绘制成功视图
     *
     * @param canvas
     */
    private void drawSuccessView(Canvas canvas, boolean animator) {
        paint = createPaint(color);
        //外圆
        drawOutsideCircle(canvas, paint, animator);
        //内钩
        if (animator) {
            canvas.drawPath(path, paint);
            if (pathAnimator != null) {
                return;
            }
            pathAnimator = createPathAnimator(createSuccessPath());
            pathAnimator.setStartDelay(duration);
            pathAnimator.start();
        } else {
            canvas.drawPath(createSuccessPath(), paint);
        }
    }


    /**
     * 绘制错误视图
     *
     * @param canvas
     */
    private void drawWarningView(Canvas canvas) {
        paint = createPaint(color);
        //外圆
        drawOutsideCircle(canvas, paint, false);
        final float part = radius * 2 / 4;
        warningTopRadius = part / 5;
        warningBottomRadius = part / 10;
        warningDotRadius = part / 6;
        float warningFlagHeight = 2 * part / 3;
        //====上部分=====
        Path path = new Path();
        paint.setStyle(Paint.Style.FILL);
        //左边线起点
        path.moveTo(centerX - warningTopRadius, centerY - 3 * part / 4);
        //左边线终点
        path.lineTo(centerX - warningBottomRadius, centerY + warningFlagHeight);
        //下边弧线
        RectF bottomOval = new RectF(centerX - warningBottomRadius, centerY + warningFlagHeight - warningBottomRadius,
                centerX + warningBottomRadius, centerY + warningFlagHeight + warningBottomRadius);
        path.arcTo(bottomOval, -180, -180, false);
        //右边线终点
        path.lineTo(centerX + warningTopRadius, centerY - 3 * part / 4);
        RectF upOval = new RectF(centerX - warningTopRadius, centerY - part, centerX + warningTopRadius, centerY - part / 2);
        //上边弧线
        path.arcTo(upOval, -180, 180, false);
        path.close();

        canvas.drawPath(path, paint);
        //=====下部分====
        canvas.drawCircle(centerX, centerY + 4 * part / 5 + warningDotRadius * 2, warningDotRadius, paint);
    }

    private void drawWirelessView(Canvas canvas) {
        for (int i = 1; i < wirelessCount; i++) {
            drawWirelessView(canvas, color, i);
        }
        drawWirelessCircle(canvas, color, wirelessCircleX, wirelessCircleY - wirelessStrokeWidth, wirelessStrokeWidth / 2);
    }

    /**
     * 绘制上部分
     *
     * @param canvas        画布
     * @param wirelessColor 颜色
     * @param acrIndex      绘制下标[第几个扇形]
     */
    private void drawWirelessView(Canvas canvas, int wirelessColor, int acrIndex) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(wirelessColor);
        paint.setStrokeWidth(wirelessStrokeWidth);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        //绘制扇形
        float radius = (wirelessStartRadius + wirelessStrokeWidth + wirelessPadding) * acrIndex;
        RectF rectF = new RectF(wirelessCircleX - radius, wirelessCircleY - radius - wirelessStrokeWidth, wirelessCircleX + radius, wirelessCircleY + radius - wirelessStrokeWidth);
        canvas.drawArc(rectF, wirelessStartAngle, wirelessSweepAngle, false, paint);
    }

    /**
     * 绘制圆
     *
     * @param canvas          画布
     * @param color           颜色
     * @param wirelessCircleX 圆中心X
     * @param wirelessCircleY 圆中心Y
     * @param wirelessRadius  圆半径
     */
    public void drawWirelessCircle(Canvas canvas, int color, float wirelessCircleX, float wirelessCircleY, float wirelessRadius) {
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(wirelessStrokeWidth);
        paint.setAntiAlias(true);
        canvas.drawCircle(wirelessCircleX, wirelessCircleY, wirelessRadius, paint);
    }

    /**
     * dip转px
     *
     * @param dp
     * @return
     */
    public float dpToPx(float dp) {
        return dp * getScreenDensity();
    }

    /**
     * 获取屏幕密度
     *
     * @return
     */
    public float getScreenDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * 创建路径动画
     *
     * @param path 路径
     * @return
     */
    private ValueAnimator createPathAnimator(final Path path) {
        final PathMeasure pathMeasure = new PathMeasure(path, false);
        ValueAnimator pathAnimator = ValueAnimator.ofFloat(pathMeasure.getLength());
        pathAnimator.setDuration(duration);
        pathAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pathCount++;
                float distance = (float) animation.getAnimatedValue();
                pathMeasure.getPosTan(distance, pos, null);
                if (pathCount == 1) {
                    StatusView.this.path.moveTo(pos[0], pos[1]);
                } else {
                    StatusView.this.path.lineTo(pos[0], pos[1]);
                }
                postInvalidate();
            }
        });
        outCircleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (pathCount == 3) {
                    outCircleAnimator = null;
                    pathCount = 0;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return pathAnimator;
    }

    /**
     * 获取显示类型
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置显示状态
     *
     * @param status 状态{@link StatusView#SUCCESS};{@link StatusView#WARNING};{@link StatusView#WIRELESS}
     */
    public void setStatus(int status) {
        this.status = status;
        invalidate();
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
     * 获取颜色
     *
     * @return
     */
    public int getColor() {
        return color;
    }

    /**
     * 设置颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
        invalidate();
    }

    /**
     * 获取圆线宽
     *
     * @return
     */
    public float getCircleStrokeWidth() {
        return circleStrokeWidth;
    }

    /**
     * 获取圆 - 线宽度
     *
     * @param circleStrokeWidth
     */
    public void setCircleStrokeWidth(float circleStrokeWidth) {
        this.circleStrokeWidth = circleStrokeWidth;
        invalidate();
    }

    /**
     * 获取警告 - 上方圆圈半径
     *
     * @return
     */
    public float getWarningTopRadius() {
        return warningTopRadius;
    }

    /**
     * 设置警告 - 上方圆圈半径
     *
     * @param warningTopRadius
     */
    public void setWarningTopRadius(float warningTopRadius) {
        this.warningTopRadius = warningTopRadius;
        invalidate();
    }

    /**
     * 获取警告 - 下方圆圈半径
     *
     * @return
     */
    public float getWarningBottomRadius() {
        return warningBottomRadius;
    }

    /**
     * 设置警告 - 下方圆圈半径
     *
     * @param warningBottomRadius
     */
    public void setWarningBottomRadius(float warningBottomRadius) {
        this.warningBottomRadius = warningBottomRadius;
        invalidate();
    }

    /**
     * 获取警告 - 圆点半径
     *
     * @return
     */
    public float getWarningDotRadius() {
        return warningDotRadius;
    }

    /**
     * 设置警告 - 圆点半径
     *
     * @param warningDotRadius
     */
    public void setWarningDotRadius(float warningDotRadius) {
        this.warningDotRadius = warningDotRadius;
        invalidate();
    }

    /**
     * 获取网络 - 中心Y
     *
     * @return
     */
    public float getWirelessCircleY() {
        return wirelessCircleY;
    }

    /**
     * 网络 - 中心X
     *
     * @param wirelessCircleY
     */
    public void setWirelessCircleY(float wirelessCircleY) {
        this.wirelessCircleY = wirelessCircleY;
        invalidate();
    }

    /**
     * 获取网络 - 中心X
     *
     * @return
     */
    public float getWirelessCircleX() {
        return wirelessCircleX;
    }

    /**
     * 设置网络 - 中心X
     *
     * @param wirelessCircleX
     */
    public void setWirelessCircleX(float wirelessCircleX) {
        this.wirelessCircleX = wirelessCircleX;
        invalidate();
    }

    /**
     * 获取网络 - 个数
     *
     * @return
     */
    public int getWirelessCount() {
        return wirelessCount;
    }

    /**
     * 设置网络 - 个数
     *
     * @param wirelessCount
     */
    public void setWirelessCount(int wirelessCount) {
        this.wirelessCount = wirelessCount;
        invalidate();
    }

    /**
     * 获取网络 - 线宽
     *
     * @return
     */
    public float getWirelessStrokeWidth() {
        return wirelessStrokeWidth;
    }

    /**
     * 设置网络 - 线宽
     *
     * @param wirelessStrokeWidth
     */
    public void setWirelessStrokeWidth(float wirelessStrokeWidth) {
        this.wirelessStrokeWidth = wirelessStrokeWidth;
        invalidate();
    }

    /**
     * 获取网络 - 开始半径
     *
     * @return
     */
    public float getWirelessStartRadius() {
        return wirelessStartRadius;
    }

    /**
     * 设置网络 - 开始半径
     *
     * @param wirelessStartRadius
     */
    public void setWirelessStartRadius(float wirelessStartRadius) {
        this.wirelessStartRadius = wirelessStartRadius;
        invalidate();
    }

    /**
     * 获取网络 - 间距
     *
     * @return
     */
    public float getWirelessPadding() {
        return wirelessPadding;
    }

    /**
     * 设置网络 - 间距
     *
     * @param wirelessPadding
     */
    public void setWirelessPadding(float wirelessPadding) {
        this.wirelessPadding = wirelessPadding;
        invalidate();
    }

    /**
     * 获取网络 - 开始角度
     *
     * @return
     */
    public float getWirelessStartAngle() {
        return wirelessStartAngle;
    }

    /**
     * 设置网络 - 开始角度
     *
     * @param wirelessStartAngle
     */
    public void setWirelessStartAngle(float wirelessStartAngle) {
        this.wirelessStartAngle = wirelessStartAngle;
        invalidate();
    }

    /**
     * 获取网络 - 角度
     *
     * @return
     */
    public float getWirelessSweepAngle() {
        return wirelessSweepAngle;
    }

    /**
     * 设置网络 - 角度
     *
     * @param wirelessSweepAngle
     */
    public void setWirelessSweepAngle(float wirelessSweepAngle) {
        this.wirelessSweepAngle = wirelessSweepAngle;
        invalidate();
    }

    /**
     * 是否使用动画
     *
     * @return
     */
    public boolean isAnimator() {
        return isAnimator;
    }

    /**
     * 设置是否使用动画
     *
     * @param isAnimator
     */
    public void setAnimator(boolean isAnimator) {
        this.isAnimator = isAnimator;
        invalidate();
    }

    /**
     * 动画持续时间
     *
     * @return
     */
    public int getDuration() {
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
     * 获取动画开始角度
     *
     * @return
     */
    public float getAnimationSweepAngle() {
        return animationSweepAngle;
    }

    /**
     * 设置动画开始角度
     *
     * @param animationSweepAngle
     */
    public void setAnimationSweepAngle(float animationSweepAngle) {
        this.animationSweepAngle = animationSweepAngle;
        invalidate();
    }

    /**
     * 获取外部圆动画
     *
     * @return
     */
    public ValueAnimator getOutCircleAnimator() {
        return outCircleAnimator;
    }

    /**
     * 设置外部圆动画
     *
     * @param outCircleAnimator
     */
    public void setOutCircleAnimator(ValueAnimator outCircleAnimator) {
        this.outCircleAnimator = outCircleAnimator;
        invalidate();
    }

    /**
     * 获取路径动画
     *
     * @return
     */
    public ValueAnimator getPathAnimator() {
        return pathAnimator;
    }

    /**
     * 设置路径动画
     *
     * @param pathAnimator
     */
    public void setPathAnimator(ValueAnimator pathAnimator) {
        this.pathAnimator = pathAnimator;
        invalidate();
    }

    /**
     * 获取路径基数
     *
     * @return
     */
    public int getPathCount() {
        return pathCount;
    }

    /**
     * 设置路径计数
     *
     * @param pathCount
     */
    public void setPathCount(int pathCount) {
        this.pathCount = pathCount;
        invalidate();
    }
}
