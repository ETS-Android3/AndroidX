package com.androidx.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.androidx.R;

import java.util.ArrayList;

/**
 * 页面标签<br/>
 * 主要使用在ViewPager顶部菜单或多页面顶部菜单<br/>
 */
public class PagerTab extends android.view.View {

    public final String TAG = PagerTab.class.getSimpleName();
    /**
     * 文字未选中颜色
     */
    private int textUnCheckColor = android.graphics.Color.parseColor("#333333");
    /**
     * 文字选中颜色
     */
    private int textCheckedColor = android.graphics.Color.parseColor("#3D9D69");
    /**
     * 文字未选中大小
     */
    private int textUnCheckSize = (int) (14 * Resources.getSystem().getDisplayMetrics().density);
    /**
     * 文字选中大小
     */
    private int textCheckedSize = (int) (14 * Resources.getSystem().getDisplayMetrics().density);
    /**
     * 画笔
     */
    private Paint paint;
    /**
     * 标签左边内间距
     */
    private int tabPaddingLeft = (int) (20 * Resources.getSystem().getDisplayMetrics().density);
    /**
     * 标签右边内间距
     */
    private int tabPaddingRight = (int) (20 * Resources.getSystem().getDisplayMetrics().density);
    /**
     * 标签宽度
     */
    private int tabWidth = LayoutParams.WRAP_CONTENT;
    /**
     * 高度中心点
     */
    private float centerY;
    /**
     * 高度中心点
     */
    private float centerX;
    /**
     * 标签数据
     */
    private CharSequence[] items = new String[]{"item", "item", "item", "item", "item", "item", "item"};
    /**
     * 选中位置
     */
    private int position = 0;
    /**
     * 上一次选中位置
     */
    private int oldPosition = -1;
    /**
     * 标签布局方式
     */
    private int tabLayout = LayoutParams.WRAP_CONTENT;
    /**
     * 坐标
     */
    private java.util.List<Tab> tabList;
    /**
     * 页面标签点击监听
     */
    private OnTabClickListener onTabClickListener;
    /**
     * 点击坐标
     */
    private float downX, downY;
    /**
     * 横向滑动X
     */
    private float scrollX = 0;
    /**
     * 下划线资源id
     */
    private int underlineHeight = (int) (3 * Resources.getSystem().getDisplayMetrics().density);
    /**
     * 下划线颜色
     */
    private int underlineColor = android.graphics.Color.parseColor("#3D9D69");
    /**
     * 下划线圆角
     */
    private float underlineRadius = 3F * Resources.getSystem().getDisplayMetrics().density;
    /**
     * 下划线左内间距
     */
    private int underlinePaddingLeft = (int) (15 * Resources.getSystem().getDisplayMetrics().density);
    /**
     * 下划线右内间距
     */
    private int underlinePaddingRight = (int) (15 * Resources.getSystem().getDisplayMetrics().density);
    /**
     * 下划线持续动画时间
     */
    private int underlineDuration = 300;
    /**
     * 下划线x
     */
    private float underlineSX = 0;
    /**
     * 下划线Y
     */
    private float underlineEX = 0;
    /**
     * 下划线x
     */
    private float underlineOldSX = 0;
    /**
     * 下划线Y
     */
    private float underlineOldEX = 0;


    public PagerTab(Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public PagerTab(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public PagerTab(Context context, @androidx.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    /**
     * 初始化xml属性
     *
     * @param context
     * @param attrs
     */
    private void initAttributeSet(Context context, @androidx.annotation.Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PagerTab);
            position = array.getInt(R.styleable.PagerTab_position, 0);
            tabLayout = array.getInt(R.styleable.PagerTab_tabLayout, tabLayout);
            if (array.getTextArray(R.styleable.PagerTab_tabItems) != null) {
                items = array.getTextArray(R.styleable.PagerTab_tabItems);
            }
            tabWidth = array.getDimensionPixelOffset(R.styleable.PagerTab_tabWidth, tabWidth);
            tabPaddingLeft = array.getDimensionPixelOffset(R.styleable.PagerTab_tabPaddingLeft, tabPaddingLeft);
            tabPaddingRight = array.getDimensionPixelOffset(R.styleable.PagerTab_tabPaddingRight, tabPaddingRight);
            textUnCheckSize = array.getDimensionPixelOffset(R.styleable.PagerTab_tabTextSize, textUnCheckSize);
            textCheckedSize = array.getDimensionPixelOffset(R.styleable.PagerTab_tabTextSelectSize, textCheckedSize);
            ColorStateList colorStateList = array.getColorStateList(R.styleable.PagerTab_tabTextColor);
            if (colorStateList != null) {
                textUnCheckColor = colorStateList.getColorForState(new int[]{android.R.attr.state_empty}, textUnCheckColor);
                textCheckedColor = colorStateList.getColorForState(new int[]{android.R.attr.state_checked}, textCheckedColor);
            }
            underlineColor = array.getColor(R.styleable.PagerTab_underlineColor, underlineColor);
            underlineRadius = array.getDimension(R.styleable.PagerTab_underlineRadius, underlineRadius);
            underlineHeight = array.getDimensionPixelOffset(R.styleable.PagerTab_underlineHeight, underlineHeight);
            underlinePaddingLeft = array.getDimensionPixelOffset(R.styleable.PagerTab_underlinePaddingLeft, underlinePaddingLeft);
            underlinePaddingRight = array.getDimensionPixelOffset(R.styleable.PagerTab_underlinePaddingRight, underlinePaddingRight);
            underlineDuration = array.getInt(R.styleable.PagerTab_underlineDuration, underlineDuration);
            array.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerY = getMeasuredHeight() / 2;
        centerX = getMeasuredWidth() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTab(canvas, items);
        if (oldPosition == -1) {
            setPosition(position);
        }
    }

    /**
     * 绘制标签栏
     *
     * @param canvas 画布
     * @param items  标签栏
     */
    private void drawTab(Canvas canvas, CharSequence[] items) {
        if (items == null || items.length == 0) {
            return;
        }
        paint = new Paint();
        paint.setAntiAlias(true);
        float x, y;
        int size = items.length;
        if (tabLayout == LayoutParams.MATCH_PARENT) {
            tabWidth = getMeasuredWidth() / size;
        }
        tabList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            paint.setColor(position == i ? textCheckedColor : textUnCheckColor);
            paint.setTextSize(position == i ? textCheckedSize : textUnCheckSize);
            String text = items[i].toString();
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            if (tabLayout == LayoutParams.WRAP_CONTENT && tabWidth == LayoutParams.WRAP_CONTENT) {
                tabWidth = bounds.width() + tabPaddingLeft + tabPaddingRight;
            }
            x = tabWidth / 2 - bounds.width() / 2 + tabWidth * i;
            y = centerY + bounds.height() / 2;
            canvas.drawText(text, x + scrollX, y, paint);
            //记录坐标
            Tab tab = new Tab();
            tab.setBounds(bounds);
            tab.setTlX(x + bounds.width() / 2 - tabWidth / 2);
            tab.setTlY(y - getMeasuredHeight() / 2 - bounds.height());
            tab.setBrX(x + bounds.width() / 2 + tabWidth / 2);
            tab.setBrY(y + getMeasuredHeight() / 2 + bounds.height());
            tab.setPosition(i);
            tab.setText(text);
            tabList.add(tab);
        }
        //下划线
        drawUnderline(canvas, underlineSX + scrollX, underlineEX + scrollX);
    }

    /**
     * 绘制下划线
     *
     * @param canvas 画布
     * @param left   水平x
     * @param right  水平y
     */
    private void drawUnderline(Canvas canvas, float left, float right) {
        RectF rectF = new RectF(left,
                getMeasuredHeight() - underlineHeight,
                right,
                getMeasuredHeight());
        paint.setColor(underlineColor);
        canvas.drawRoundRect(rectF, underlineRadius, underlineRadius, paint);
    }

    /**
     * 滑动绘制下划线
     *
     * @param start 开始
     * @param end   结束
     * @param isX   是否水平
     */
    private void smoothDrawUnderline(float start, float end, final boolean isX) {
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(underlineDuration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (isX) {
                    underlineSX = value;
                } else {
                    underlineEX = value;
                }
                invalidate();
            }
        });
        animator.start();
    }

    /**
     * 设置当前位置
     *
     * @param position
     */
    private void setCurrentPosition(int position) {
        this.position = position;
    }

    /**
     * 设置旧位置
     *
     * @param position
     */
    private void setOldPosition(int position) {
        oldPosition = position;
    }

    /**
     * 获取之前的位置
     *
     * @return
     */
    public int getOldPosition() {
        return oldPosition;
    }

    /**
     * 设置选中位置
     *
     * @param position
     */
    public void setPosition(int position) {
        setOldPosition(getPosition());
        setCurrentPosition(position);
        //获取对应position的item对象
        Tab oldTab = getTab(getOldPosition());
        Tab tab = getTab(position);
        if (oldTab == null || tab == null) {
            return;
        }
        //获取对应x移动距离
        underlineOldSX = oldTab.getTlX() + underlinePaddingLeft;
        underlineOldEX = oldTab.getBrX() - underlinePaddingRight;
        underlineSX = tab.getTlX() + underlinePaddingLeft;
        underlineEX = tab.getBrX() - underlinePaddingRight;
        //动画滑动对应位置
        smoothDrawUnderline(underlineOldSX, underlineSX, true);
        smoothDrawUnderline(underlineOldEX, underlineEX, false);
        //华东对应位置
        smoothScrollToPosition(position);
    }

    /**
     * 动画滚动到对应位置
     *
     * @param x
     */
    public void smoothScrollTo(float x) {
        Tab tab = getLastTab();
        if (tab != null) {
            float beyond = getMeasuredWidth() - tab.getBrX();
            scrollX += x;
            scrollX = scrollX < beyond ? beyond : scrollX;
            scrollX = scrollX > 0 ? 0 : scrollX;
            invalidate();
        }
    }

    /**
     * 动画滚动到对应位置
     *
     * @param position
     */
    public void smoothScrollToPosition(int position) {
        Tab tab = getTab(position);
        scrollX = getMeasuredWidth() - tab.getBrX();
        scrollX = scrollX > 0 ? 0 : scrollX;
        invalidate();
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    public int getPosition() {
        return position;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                Tab downTab = getTab(downX, downY);
                Tab tab = getTab(event.getX(), event.getY());
                if (tab != null && downTab != null && tab.text.equals(downTab.text)) {
                    setPosition(tab.getPosition());
                    if (onTabClickListener != null) {
                        onTabClickListener.onTabClick(PagerTab.this, getPosition());
                    }
                }
                return true;
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                float distanceX = Math.abs(event.getX() - downX);
                float distanceY = Math.abs(event.getY() - downY);
                if (distanceX > distanceY) {
                    smoothScrollTo((event.getX() - downX) / 5.0f);
                }
                return true;
            default:
                super.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }


    /**
     * 设置页面标签点击监听
     *
     * @param onTabClickListener
     */
    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    public interface OnTabClickListener {

        /**
         * 页面标签点击
         *
         * @param pagerTab 页面标签
         * @param position 位置
         */
        void onTabClick(PagerTab pagerTab, int position);

    }

    /**
     * 标签布局
     */
    public class LayoutParams {
        /**
         * 平均分
         */
        public static final int MATCH_PARENT = -1;
        /**
         * 自适应
         */
        public static final int WRAP_CONTENT = -2;
    }

    /**
     * 获取Tab个数
     *
     * @return
     */
    public int getTabCount() {
        return tabList == null ? 0 : tabList.size();
    }

    /**
     * 找到标签坐标
     *
     * @param x 点击x
     * @param y 点击y
     * @return
     */
    public Tab getTab(float x, float y) {
        int size = tabList == null ? 0 : tabList.size();
        for (int i = 0; i < size; i++) {
            Tab coordinate = tabList.get(i);
            if (x >= coordinate.getTlX() + scrollX && y >= coordinate.getTlY()
                    && x <= coordinate.getBrX() + scrollX && y <= coordinate.getBrY()) {
                return tabList.get(i);
            }
        }
        return null;
    }

    /**
     * 找到标签
     *
     * @param position 位置
     * @return
     */
    public Tab getTab(int position) {
        if (tabList == null || tabList.size() == 0) {
            return null;
        }
        return tabList.get(position);
    }

    /**
     * 获取最后一个tab
     *
     * @return
     */
    public Tab getLastTab() {
        return getTab(getTabCount() - 1);
    }

    public class Tab {

        /**
         * 顶部左边X
         */
        private float tlX;
        /**
         * 顶部左边Y
         */
        private float tlY;
        /**
         * 底部右边X
         */
        private float brX;
        /**
         * 底部右边Y
         */
        private float brY;
        /**
         * 文字界限
         */
        private Rect bounds;
        /**
         * 位置
         */
        private int position;
        /**
         * 文字
         */
        private String text;


        public float getTlX() {
            return tlX;
        }

        public void setTlX(float tlX) {
            this.tlX = tlX;
        }

        public float getTlY() {
            return tlY;
        }

        public void setTlY(float tlY) {
            this.tlY = tlY;
        }

        public float getBrX() {
            return brX;
        }

        public void setBrX(float brX) {
            this.brX = brX;
        }

        public float getBrY() {
            return brY;
        }

        public void setBrY(float brY) {
            this.brY = brY;
        }

        public Rect getBounds() {
            return bounds;
        }

        public void setBounds(Rect bounds) {
            this.bounds = bounds;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * 获取未选中标签颜色
     *
     * @return
     */
    public int getTextUnCheckColor() {
        return textUnCheckColor;
    }

    /**
     * 设置未选中标签颜色
     *
     * @param textUnCheckColor 颜色
     */
    public void setTextUnCheckColor(int textUnCheckColor) {
        this.textUnCheckColor = textUnCheckColor;
        invalidate();
    }

    /**
     * 获取选择标签颜色
     *
     * @return
     */
    public int getTextCheckedColor() {
        return textCheckedColor;
    }

    /**
     * 设置选择标签颜色
     *
     * @param textCheckedColor
     */
    public void setTextCheckedColor(int textCheckedColor) {
        this.textCheckedColor = textCheckedColor;
        invalidate();
    }

    /**
     * 获取文字未选择字体大小
     *
     * @return
     */
    public int getTextUnCheckSize() {
        return textUnCheckSize;
    }

    /**
     * 设置文字未选择字体大小
     *
     * @param textUnCheckSize
     */
    public void setTextUnCheckSize(int textUnCheckSize) {
        this.textUnCheckSize = textUnCheckSize;
        invalidate();
    }

    /**
     * 获取标签文字选中大小
     *
     * @return
     */
    public int getTextCheckedSize() {
        return textCheckedSize;
    }

    /**
     * 设置标签文字选中大小
     *
     * @param textCheckedSize
     */
    public void setTextCheckedSize(int textCheckedSize) {
        this.textCheckedSize = textCheckedSize;
        invalidate();
    }

    /**
     * 获取画笔对象
     *
     * @return
     */
    public Paint getPaint() {
        return paint;
    }

    /**
     * 设置画笔对象
     *
     * @param paint
     */
    public void setPaint(Paint paint) {
        this.paint = paint;
        invalidate();
    }

    /**
     * 设置标签左边间距
     *
     * @return
     */
    public int getTabPaddingLeft() {
        return tabPaddingLeft;
    }

    /**
     * 设置标签右边间距
     *
     * @param tabPaddingLeft
     */
    public void setTabPaddingLeft(int tabPaddingLeft) {
        this.tabPaddingLeft = tabPaddingLeft;
        invalidate();
    }

    /**
     * 获取标签右边间距
     *
     * @return
     */
    public int getTabPaddingRight() {
        return tabPaddingRight;
    }

    /**
     * 设置标签右边间距
     *
     * @param tabPaddingRight
     */
    public void setTabPaddingRight(int tabPaddingRight) {
        this.tabPaddingRight = tabPaddingRight;
        invalidate();
    }

    /**
     * 获取标签宽度
     *
     * @return
     */
    public int getTabWidth() {
        return tabWidth;
    }

    /**
     * 设置标签宽度
     *
     * @param tabWidth
     */
    public void setTabWidth(int tabWidth) {
        this.tabWidth = tabWidth;
        invalidate();
    }

    /**
     * 获取中间Y
     *
     * @return
     */
    public float getCenterY() {
        return centerY;
    }

    /**
     * 设置中间Y
     *
     * @param centerY
     */
    public void setCenterY(float centerY) {
        this.centerY = centerY;
        invalidate();
    }

    /**
     * 获取中间X
     *
     * @return
     */
    public float getCenterX() {
        return centerX;
    }

    /**
     * 设置中间X
     *
     * @param centerX
     */
    public void setCenterX(float centerX) {
        this.centerX = centerX;
        invalidate();
    }

    /**
     * 获取items
     *
     * @return
     */
    public CharSequence[] getItems() {
        return items;
    }

    /**
     * 设置items
     *
     * @param items
     */
    public void setItems(CharSequence[] items) {
        this.items = items;
        invalidate();
    }

    /**
     * 获取标签布局方式
     *
     * @return
     */
    public int getTabLayout() {
        return tabLayout;
    }

    /**
     * 设置标签布局方式
     *
     * @param tabLayout
     */
    public void setTabLayout(int tabLayout) {
        this.tabLayout = tabLayout;
        invalidate();
    }

    /**
     * 获取标签实体列表
     *
     * @return
     */
    public java.util.List<Tab> getTabList() {
        return tabList;
    }

    /**
     * 设置标签实体列表
     *
     * @param tabList
     */
    public void setTabList(java.util.List<Tab> tabList) {
        this.tabList = tabList;
        invalidate();
    }

    /**
     * 获取标签点击事件
     *
     * @return
     */
    public OnTabClickListener getOnTabClickListener() {
        return onTabClickListener;
    }

    /**
     * 获取点击X
     *
     * @return
     */
    public float getDownX() {
        return downX;
    }

    /**
     * 设置点击X
     *
     * @param downX
     */
    public void setDownX(float downX) {
        this.downX = downX;
        invalidate();
    }

    /**
     * 获取点击Y
     *
     * @return
     */
    public float getDownY() {
        return downY;
    }

    /**
     * 设置点击Y
     *
     * @param downY
     */
    public void setDownY(float downY) {
        this.downY = downY;
        invalidate();
    }

    /**
     * 设置滑动X
     *
     * @param scrollX
     */
    public void setScrollX(float scrollX) {
        this.scrollX = scrollX;
        invalidate();
    }

    /**
     * 获取下划线高度
     *
     * @return
     */
    public int getUnderlineHeight() {
        return underlineHeight;
    }

    /**
     * 设置下划线高度
     *
     * @param underlineHeight
     */
    public void setUnderlineHeight(int underlineHeight) {
        this.underlineHeight = underlineHeight;
        invalidate();
    }

    /**
     * 获取下划线颜色
     *
     * @return
     */
    public int getUnderlineColor() {
        return underlineColor;
    }

    /**
     * 设置下划线颜色
     *
     * @param underlineColor
     */
    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    /**
     * 获取下划线圆角
     *
     * @return
     */
    public float getUnderlineRadius() {
        return underlineRadius;
    }

    /**
     * 设置下划线圆角
     *
     * @param underlineRadius
     */
    public void setUnderlineRadius(float underlineRadius) {
        this.underlineRadius = underlineRadius;
        invalidate();
    }

    /**
     * 获取下划线左边间距
     *
     * @return
     */
    public int getUnderlinePaddingLeft() {
        return underlinePaddingLeft;
    }

    /**
     * 设置下划线左边间距
     *
     * @param underlinePaddingLeft
     */
    public void setUnderlinePaddingLeft(int underlinePaddingLeft) {
        this.underlinePaddingLeft = underlinePaddingLeft;
        invalidate();
    }

    /**
     * 获取下划线右边间距
     *
     * @return
     */
    public int getUnderlinePaddingRight() {
        return underlinePaddingRight;
    }

    /**
     * 设置下划线右边间距
     *
     * @param underlinePaddingRight
     */
    public void setUnderlinePaddingRight(int underlinePaddingRight) {
        this.underlinePaddingRight = underlinePaddingRight;
        invalidate();
    }

    /**
     * 获取下划线持续时间
     *
     * @return
     */
    public int getUnderlineDuration() {
        return underlineDuration;
    }

    /**
     * 设置下划线持续时间
     *
     * @param underlineDuration
     */
    public void setUnderlineDuration(int underlineDuration) {
        this.underlineDuration = underlineDuration;
        invalidate();
    }

    /**
     * 获取下划线开始X
     *
     * @return
     */
    public float getUnderlineSX() {
        return underlineSX;
    }

    /**
     * 设置下划线开始x
     *
     * @param underlineSX
     */
    public void setUnderlineSX(float underlineSX) {
        this.underlineSX = underlineSX;
        invalidate();
    }

    /**
     * 获取下划线结束x
     *
     * @return
     */
    public float getUnderlineEX() {
        return underlineEX;
    }

    /**
     * 设置下划线结束X
     *
     * @param underlineEX
     */
    public void setUnderlineEX(float underlineEX) {
        this.underlineEX = underlineEX;
        invalidate();
    }

    /**
     * 获取下划线上次开始X
     *
     * @return
     */
    public float getUnderlineOldSX() {
        return underlineOldSX;
    }

    /**
     * 设置下划线上次开始X
     *
     * @param underlineOldSX
     */
    public void setUnderlineOldSX(float underlineOldSX) {
        this.underlineOldSX = underlineOldSX;
        invalidate();
    }

    /**
     * 获取下划线上次结束X
     *
     * @return
     */
    public float getUnderlineOldEX() {
        return underlineOldEX;
    }

    /**
     * 设置下划线上次结束X
     *
     * @param underlineOldEX
     */
    public void setUnderlineOldEX(float underlineOldEX) {
        this.underlineOldEX = underlineOldEX;
        invalidate();
    }

}
