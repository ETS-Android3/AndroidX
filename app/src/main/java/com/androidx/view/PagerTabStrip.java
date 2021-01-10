package com.androidx.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.androidx.R;
import com.androidx.util.Log;

/**
 * Author: Relin
 * Description:页面标签
 * Date:2021/01/08 00:37
 */
public class PagerTabStrip extends HorizontalScrollView {

    public static final String TAG = PagerTabStrip.class.getSimpleName();
    /**
     * 标签父级的父级
     */
    private FrameLayout container;
    /**
     * 标签父级
     */
    private LinearLayout tabParent;
    /**
     * 下划线持续时间
     */
    private int duration = 300;
    /**
     * 标签位置
     */
    private int position = 0;
    /**
     * 标签布局参数
     */
    private int tabLayoutParams = TabLayoutParams.MATCH_PARENT;
    /**
     * 标签下划线参数
     */
    private int tabUnderlineParams = TabUnderlineParams.MATCH_PARENT;
    /**
     * 水平间距
     */
    private int tabPaddingHorizontal = 20;
    /**
     * 垂直间距
     */
    private int tabPaddingVertical = 30;
    /**
     * 标签宽度
     */
    private int tabWidth = LinearLayout.LayoutParams.WRAP_CONTENT;
    /**
     * 标签文字大小
     */
    private int textSize = (int) (14 * Resources.getSystem().getDisplayMetrics().density);
    /**
     * 选中标签文字大小
     */
    private int textSelectSize = (int) (14 * Resources.getSystem().getDisplayMetrics().density);
    /**
     * 标签文字状态颜色
     */
    private ColorStateList textColorStateList;
    /**
     * 标签文字颜色
     */
    private int textColor = Color.parseColor("#333333");
    /**
     * 标签选中颜色
     */
    private int textSelectedColor = Color.parseColor("#3D9D69");
    /**
     * 分割线View
     */
    private View dividerView;
    /**
     * 分割线颜色
     */
    private int dividerColor = Color.parseColor("#FFFFFF");
    /**
     * 分割线资源
     */
    private int dividerResId = 0;
    /**
     * 分割线宽度
     */
    private int dividerWidth = 2;
    /**
     * 跟个先垂直间距
     */
    private int dividerPaddingVertical = 20;
    /**
     * 下划线
     */
    private View underlineView;
    /**
     * 下划线颜色
     */
    private int underlineColor = Color.parseColor("#E6E6E6");
    /**
     * 下划线资源
     */
    private int underlineResId = 0;
    /**
     * 下划线背景
     */
    private Drawable underlineDrawable;
    /**
     * 下划线高度
     */
    private int underlineHeight = 8;

    /**
     * 标签
     */
    private CharSequence[] pageTitle;
    /**
     * ViewPager
     */
    private ViewPager viewPager;
    /**
     * PagerAdapter
     */
    private PagerAdapter pagerAdapter;
    /**
     * Adapter数据监听
     */
    private ViewPagerDataSetObserver dataSetObserver;


    public PagerTabStrip(@NonNull Context context) {
        super(context);
        initAttributeSet(context, null);
    }

    public PagerTabStrip(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributeSet(context, attrs);
    }

    public PagerTabStrip(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeSet(context, attrs);
    }

    protected void initAttributeSet(Context context, AttributeSet attrs) {
        setHorizontalScrollBarEnabled(false);
        setFillViewport(true);
        setWillNotDraw(false);
        //xml参数
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PagerTabStrip);
        position = typedArray.getInt(R.styleable.PagerTabStrip_position, position);
        tabLayoutParams = typedArray.getInt(R.styleable.PagerTabStrip_tabLayout, tabLayoutParams);
        tabUnderlineParams = typedArray.getInt(R.styleable.PagerTabStrip_underlineLayout, tabUnderlineParams);
        tabPaddingHorizontal = typedArray.getDimensionPixelOffset(R.styleable.PagerTabStrip_tabPaddingHorizontal, tabPaddingHorizontal);
        tabPaddingVertical = typedArray.getDimensionPixelOffset(R.styleable.PagerTabStrip_tabPaddingVertical, tabPaddingVertical);
        tabWidth = typedArray.getDimensionPixelOffset(R.styleable.PagerTabStrip_tabWidth, tabWidth);
        textSize = typedArray.getDimensionPixelOffset(R.styleable.PagerTabStrip_tabTextSize, textSize);
        textSelectSize = typedArray.getDimensionPixelSize(R.styleable.PagerTabStrip_tabTextSelectSize, textSelectSize);
        textColorStateList = typedArray.getColorStateList(R.styleable.PagerTabStrip_tabTextColor);
        if (textColorStateList == null) {
            int normalColor = typedArray.getResources().getColor(R.color.colorPagerTabStripText);
            int selectColor = typedArray.getResources().getColor(R.color.colorPagerTabStripSelectedText);
            textColorStateList = buildColorStateList(selectColor, normalColor);
        }
        textColor = textColorStateList.getDefaultColor();
        textSelectedColor = textColorStateList.getColorForState(new int[]{android.R.attr.state_selected}, typedArray.getResources().getColor(R.color.colorPagerTabStripSelectedText));
        dividerColor = typedArray.getColor(R.styleable.PagerTabStrip_dividerColor, dividerColor);
        dividerWidth = typedArray.getDimensionPixelOffset(R.styleable.PagerTabStrip_dividerWidth, dividerWidth);
        dividerPaddingVertical = typedArray.getDimensionPixelOffset(R.styleable.PagerTabStrip_dividerPaddingVertical, dividerPaddingVertical);
        underlineColor = typedArray.getColor(R.styleable.PagerTabStrip_underlineColor, underlineColor);
        underlineDrawable = typedArray.getDrawable(R.styleable.PagerTabStrip_underlineResId);
        underlineHeight = typedArray.getDimensionPixelOffset(R.styleable.PagerTabStrip_underlineHeight, underlineHeight);
        duration = typedArray.getInt(R.styleable.PagerTabStrip_underlineDuration, duration);
        typedArray.recycle();
        //初始化父级和容器
        initContainerParent(context);
    }

    /**
     * 初始化容器和父级
     *
     * @param context
     */
    protected void initContainerParent(Context context) {
        if (container == null) {
            container = new FrameLayout(context);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        addView(container, params);
        //标签父级
        if (tabParent == null) {
            tabParent = buildTabParent();
        }
        addTabParent(tabParent);
        //初始化模拟数据
        int count = 5;
        pageTitle = new CharSequence[count];
        for (int i = 0; i < count; i++) {
            pageTitle[i] = "item";
        }
        setPageTitle(pageTitle);
    }

    /**
     * 构建状态颜色
     *
     * @param selected 选中
     * @param normal   正常
     * @return
     */
    protected ColorStateList buildColorStateList(int selected, int normal) {
        int[] colors = new int[]{selected, normal};
        int[][] states = new int[2][];
        states[0] = new int[]{android.R.attr.state_selected};
        states[1] = new int[]{};
        return new ColorStateList(states, colors);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        underlineResId = R.drawable.android_shape_radius8_primary;
        if (underlineView == null) {
            underlineView = buildTabUnderline(underlineColor, underlineResId);
        }
        addTabUnderline(container, underlineView, underlineHeight);
    }

    public class TabLayoutParams {
        /**
         * 平均分
         */
        public static final int MATCH_PARENT = -1;
        /**
         * 自适应
         */
        public static final int WRAP_CONTENT = -2;
    }

    public class TabUnderlineParams {

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
     * 设置页面标题
     *
     * @param pageTitle 页面标题
     */
    public void setPageTitle(CharSequence[] pageTitle) {
        this.pageTitle = pageTitle;
        notifyDataSetChanged();
    }

    /**
     * 设置ViewPager
     *
     * @param viewPager
     */
    public void setViewPager(ViewPager viewPager) {
        if (viewPager != null && viewPager.getAdapter() != null) {
            pagerAdapter = viewPager.getAdapter();
            if (dataSetObserver == null) {
                dataSetObserver = new ViewPagerDataSetObserver();
            }
            pagerAdapter.registerDataSetObserver(dataSetObserver);
        }
        this.viewPager = viewPager;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (pagerAdapter != null) {
            pagerAdapter.unregisterDataSetObserver(dataSetObserver);
        }
    }

    private class ViewPagerDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }

    /**
     * 通知数据改变
     */
    public void notifyDataSetChanged() {
        if (pagerAdapter != null) {
            int count = pagerAdapter.getCount();
            pageTitle = new String[count];
            for (int i = 0; i < count; i++) {
                pageTitle[i] = pagerAdapter.getPageTitle(i);
            }
        }
        if (pageTitle != null) {
            addTabText(tabParent, pageTitle, position, tabPaddingHorizontal, tabPaddingVertical);
        }
    }

    /**
     * 设置选中位置
     *
     * @param position 位置
     */
    public void setPosition(int position) {
        this.position = position;
        notifyDataSetChanged();
    }

    /**
     * 标签父级
     *
     * @return
     */
    protected LinearLayout buildTabParent() {
        LinearLayout tabParent = new LinearLayout(getContext());
        tabParent.setOrientation(LinearLayout.HORIZONTAL);
        return tabParent;
    }

    /**
     * 添加父级
     *
     * @param tabParent
     */
    protected void addTabParent(LinearLayout tabParent) {
        if (tabParent.getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) tabParent.getParent();
            viewGroup.removeView(tabParent);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        container.addView(tabParent, params);
    }

    /**
     * 清空子控件
     */
    protected void clearTabParent() {
        if (tabParent != null) {
            tabParent.removeAllViews();
        }
    }

    /**
     * 标签文字
     *
     * @param textSize          文字大小
     * @param textColor         文字颜色
     * @param textSelectedColor 选中颜色
     * @param text              文本
     * @return
     */
    protected TextView buildTabText(int textSize, int textColor, int textSelectedColor, CharSequence text) {
        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setTextColor(textColor);
        textView.setText(text);
        if (textSelectedColor != 0) {
            textView.setTextColor(textSelectedColor);
        }
        return textView;
    }

    /**
     * 添加标签
     *
     * @param parent               父级
     * @param pageTitles           标题
     * @param position             选中位置
     * @param tabPaddingHorizontal 水平间距
     * @param tabPaddingVertical   垂直间距
     */
    protected void addTabText(ViewGroup parent, CharSequence[] pageTitles, int position, int tabPaddingHorizontal, int tabPaddingVertical) {
        clearTabParent();
        int count = pageTitles.length;
        for (int i = 0; i < count; i++) {
            CharSequence text = pageTitles[i];
            TextView tab = buildTabText(position == i ? textSelectSize : textSize, textColor, position == i ? textSelectedColor : 0, text);
            tab.setGravity(Gravity.CENTER);
            tab.setOnClickListener(new TabClickListener(this, tab, i));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tabWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            //平均分
            if (tabLayoutParams == TabLayoutParams.MATCH_PARENT) {
                params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                tab.setPadding(0, tabPaddingVertical, 0, tabPaddingVertical);
                params.weight = 1;
            }
            //自适应
            if (tabLayoutParams == TabLayoutParams.WRAP_CONTENT) {
                params = new LinearLayout.LayoutParams(tabWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                tab.setPadding(tabPaddingHorizontal, tabPaddingVertical, tabPaddingHorizontal, tabPaddingVertical);
            }
            params.gravity = Gravity.CENTER;
            tab.setLayoutParams(params);
            parent.addView(tab);
            //分割线
            dividerView = buildTabDivider(dividerWidth, dividerColor, dividerResId, dividerPaddingVertical);
            if (i != count - 1) {
                addTabDivider(parent, dividerView);
            }
        }
    }

    /**
     * 标签点击
     */
    private class TabClickListener implements OnClickListener {

        /**
         * 页面标签
         */
        private PagerTabStrip pagerTabStrip;
        /**
         * 位置
         */
        private int position;
        /**
         * 标签
         */
        private TextView tab;

        public TabClickListener(PagerTabStrip pagerTabStrip, TextView tab, int position) {
            this.pagerTabStrip = pagerTabStrip;
            this.tab = tab;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            setPosition(position);
            notifyDataSetChanged();
            Log.i(TAG, "->tab click position = " + position);
            if (onPagerTabStripItemClickListener != null) {
                onPagerTabStripItemClickListener.onPagerTabStripItemClick(pagerTabStrip, tab, pageTitle, position);
            }
        }
    }

    private OnPagerTabStripItemClickListener onPagerTabStripItemClickListener;

    /**
     * 设置标签点击时间
     *
     * @param onPagerTabStripItemClickListener
     */
    public void setOnPagerTabStripItemClickListener(OnPagerTabStripItemClickListener onPagerTabStripItemClickListener) {
        this.onPagerTabStripItemClickListener = onPagerTabStripItemClickListener;
    }

    public interface OnPagerTabStripItemClickListener {

        void onPagerTabStripItemClick(PagerTabStrip strip, TextView tab, CharSequence[] pagerTitles, int position);

    }

    /**
     * 标签分割线
     *
     * @param dividerWidth 宽度
     * @param dividerColor 颜色
     * @param dividerResId 资源id
     */
    protected View buildTabDivider(int dividerWidth, int dividerColor, int dividerResId, int dividerPaddingVertical) {
        View dividerView = new View(getContext());
        dividerView.setBackgroundColor(dividerColor);
        if (dividerResId != 0) {
            dividerView.setBackgroundResource(dividerResId);
        }
        LayoutParams params = new LayoutParams(dividerWidth, LayoutParams.MATCH_PARENT);
        params.topMargin = dividerPaddingVertical;
        params.bottomMargin = dividerPaddingVertical;
        dividerView.setLayoutParams(params);
        return dividerView;
    }

    /**
     * 添加标签分割线
     *
     * @param parent      父级
     * @param dividerView 分割线View
     */
    protected void addTabDivider(ViewGroup parent, View dividerView) {
        if (dividerView.getParent() != null) {
            parent.removeView(dividerView);
        }
        parent.addView(dividerView);
    }

    /**
     * 标签下划线
     *
     * @param underlineColor 颜色
     * @param underlineResId 资源id
     * @return
     */
    protected View buildTabUnderline(int underlineColor, int underlineResId) {
        View underline = new View(getContext());
        underline.setBackgroundColor(underlineColor);
        if (underlineResId != 0) {
            underline.setBackgroundResource(underlineResId);
        }
        if (underlineDrawable != null) {
            underline.setBackground(underlineDrawable);
        }
        return underline;
    }

    /**
     * 添加标签下划线
     *
     * @param parent          父级
     * @param underlineView   下划线
     * @param underlineHeight 高度
     */
    protected void addTabUnderline(ViewGroup parent, View underlineView, int underlineHeight) {
        setTabUnderlineWidth(underlineView, underlineHeight, position);
        if (underlineView.getParent() == null) {
            parent.addView(underlineView);
        }
        int end = computeTabUnderlineTranslationY(position);
        startTabUnderlineTranslation(underlineView, end);
    }

    /**
     * 设置标签下划线宽度
     *
     * @param underlineView   下划线
     * @param underlineHeight 高度
     * @param position        位置
     */
    protected void setTabUnderlineWidth(View underlineView, int underlineHeight, int position) {
        int count = tabParent.getChildCount();
        TextView tabView = findTabView(position);
        int textWidth = (int) tabView.getPaint().measureText(tabView.getText().toString());
        int underlineWidth = tabView.getMeasuredWidth() + (position == count - 1 ? 0 : dividerWidth);
        int padding = 0, marginHorizontal = tabPaddingHorizontal;
        if (tabLayoutParams == TabLayoutParams.MATCH_PARENT) {
            if (tabUnderlineParams == TabUnderlineParams.MATCH_PARENT) {
                padding = 0;
            }
            if (tabUnderlineParams == TabUnderlineParams.WRAP_CONTENT) {
                padding = underlineWidth - textWidth;
                marginHorizontal = padding / 2;
            }
        }
        if (tabLayoutParams == TabLayoutParams.WRAP_CONTENT) {
            if (tabUnderlineParams == TabUnderlineParams.MATCH_PARENT) {
                padding = 0;
            }
            if (tabUnderlineParams == TabUnderlineParams.WRAP_CONTENT) {
                padding = tabView.getPaddingLeft() + tabView.getPaddingRight();
                //自定义标签宽度
                if (tabWidth != LinearLayout.LayoutParams.WRAP_CONTENT) {
                    padding = underlineWidth - textWidth;
                    marginHorizontal = padding / 2;
                }
            }
        }
        underlineWidth -= padding;
        LayoutParams underlineParams = new LayoutParams(underlineWidth, underlineHeight);
        if (tabUnderlineParams == TabUnderlineParams.MATCH_PARENT) {
            underlineParams.leftMargin = 0;
            underlineParams.rightMargin = 0;
        }
        if (tabUnderlineParams == TabUnderlineParams.WRAP_CONTENT) {
            underlineParams.leftMargin = marginHorizontal;
            underlineParams.rightMargin = marginHorizontal;
        }
        underlineParams.gravity = Gravity.BOTTOM;
        underlineView.setLayoutParams(underlineParams);
    }

    /**
     * 找到标签
     *
     * @param position 标签位置
     * @return
     */
    public TextView findTabView(int position) {
        int index = position * 2;
        return (TextView) tabParent.getChildAt(index);
    }

    /**
     * 计算
     *
     * @param position
     * @return
     */
    protected int computeTabUnderlineTranslationY(int position) {
        int translationY = 0;
        int count = tabParent.getChildCount();
        if (position == 0) {
            translationY = 0;
        }
        if (position < count && position != 0) {
            for (int i = 0; i < position * 2; i++) {
                View view = tabParent.getChildAt(i);
                if (view instanceof TextView) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
                    translationY += view.getMeasuredWidth() + params.leftMargin + params.rightMargin;
                    translationY += i == count - 1 ? 0 : dividerWidth;
                }
            }
        }
        return translationY;
    }

    /**
     * 开始位移动画
     *
     * @param view 控件
     * @param endX 结束位置
     */
    protected void startTabUnderlineTranslation(final View view, float endX) {
        float x = view.getX();
        if (tabUnderlineParams == TabUnderlineParams.WRAP_CONTENT && endX < x) {
            x -= tabPaddingHorizontal;
        }
        ValueAnimator animator = ValueAnimator.ofFloat(x, endX);
        animator.setTarget(view);
        animator.setDuration(duration).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setTranslationX((Float) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    /**
     * 获取标签容器
     *
     * @return
     */
    public FrameLayout getContainer() {
        return container;
    }

    /**
     * 设置标签容器
     *
     * @param container
     */
    public void setContainer(FrameLayout container) {
        this.container = container;
        requestLayout();
    }

    /**
     * 获取标签父级
     *
     * @return
     */
    public LinearLayout getTabParent() {
        return tabParent;
    }

    /**
     * 设置标签父级
     *
     * @param tabParent
     */
    public void setTabParent(LinearLayout tabParent) {
        this.tabParent = tabParent;
        requestLayout();
    }

    /**
     * 获取下划线持续时间
     *
     * @return
     */
    public int getDuration() {
        return duration;
    }

    /**
     * 设置下划线持续时间
     *
     * @param duration
     */
    public void setDuration(int duration) {
        this.duration = duration;
        requestLayout();
    }

    /**
     * 选中位置
     *
     * @return
     */
    public int getPosition() {
        return position;
    }

    /**
     * 标签布局方式
     *
     * @return
     */
    public int getTabLayoutParams() {
        return tabLayoutParams;
    }

    /**
     * 设置标签布局
     *
     * @param tabLayoutParams
     */
    public void setTabLayoutParams(int tabLayoutParams) {
        this.tabLayoutParams = tabLayoutParams;
        requestLayout();
    }

    /**
     * 获取标签下划线参数
     *
     * @return
     */
    public int getTabUnderlineParams() {
        return tabUnderlineParams;
    }

    /**
     * 设置标签下划线参数
     *
     * @param tabUnderlineParams
     */
    public void setTabUnderlineParams(int tabUnderlineParams) {
        this.tabUnderlineParams = tabUnderlineParams;
        requestLayout();
    }

    /**
     * 获取标签水平间距
     *
     * @return
     */
    public int getTabPaddingHorizontal() {
        return tabPaddingHorizontal;
    }

    /**
     * 设置标签水平间距
     *
     * @param tabPaddingHorizontal
     */
    public void setTabPaddingHorizontal(int tabPaddingHorizontal) {
        this.tabPaddingHorizontal = tabPaddingHorizontal;
        requestLayout();
    }

    /**
     * 获取标签垂直间距
     *
     * @return
     */
    public int getTabPaddingVertical() {
        return tabPaddingVertical;
    }

    /**
     * 设置标签垂直间距
     *
     * @param tabPaddingVertical
     */
    public void setTabPaddingVertical(int tabPaddingVertical) {
        this.tabPaddingVertical = tabPaddingVertical;
        requestLayout();
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
        requestLayout();
    }

    /**
     * 获取文字大小
     *
     * @return
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * 设置文字大小
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        this.textSize = textSize;
        requestLayout();
    }

    /**
     * 获取文字选中文字大小
     *
     * @return
     */
    public int getTextSelectSize() {
        return textSelectSize;
    }

    /**
     * 设置文字选中大小
     *
     * @param textSelectSize
     */
    public void setTextSelectSize(int textSelectSize) {
        this.textSelectSize = textSelectSize;
        requestLayout();
    }

    /**
     * 获取文字颜色状态
     *
     * @return
     */
    public ColorStateList getTextColorStateList() {
        return textColorStateList;
    }

    /**
     * 设置文字颜色状态
     *
     * @param textColorStateList
     */
    public void setTextColorStateList(ColorStateList textColorStateList) {
        this.textColorStateList = textColorStateList;
        requestLayout();
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
        requestLayout();
    }

    /**
     * 获取文字选中颜色
     *
     * @return
     */
    public int getTextSelectedColor() {
        return textSelectedColor;
    }

    /**
     * 设置文字选中颜色
     *
     * @param textSelectedColor
     */
    public void setTextSelectedColor(int textSelectedColor) {
        this.textSelectedColor = textSelectedColor;
        requestLayout();
    }

    /**
     * 获取分割线
     *
     * @return
     */
    public View getDividerView() {
        return dividerView;
    }

    /**
     * 设置分割线
     *
     * @param dividerView
     */
    public void setDividerView(View dividerView) {
        this.dividerView = dividerView;
        requestLayout();
    }

    /**
     * 获取分割线颜色
     *
     * @return
     */
    public int getDividerColor() {
        return dividerColor;
    }

    /**
     * 设置分割线颜色
     *
     * @param dividerColor
     */
    public void setDividerColor(@ColorInt int dividerColor) {
        this.dividerColor = dividerColor;
        requestLayout();
    }

    /**
     * 获取分割线资源
     *
     * @return
     */
    public int getDividerResId() {
        return dividerResId;
    }

    /**
     * 设置分割线资源
     *
     * @param dividerResId
     */
    public void setDividerResId(@ColorRes int dividerResId) {
        this.dividerResId = dividerResId;
        requestLayout();
    }

    /**
     * 获取分割线宽度
     *
     * @return
     */
    public int getDividerWidth() {
        return dividerWidth;
    }

    /**
     * 设置分割线宽度
     *
     * @param dividerWidth
     */
    public void setDividerWidth(int dividerWidth) {
        this.dividerWidth = dividerWidth;
        requestLayout();
    }

    /**
     * 获取分割线垂直间距
     *
     * @return
     */
    public int getDividerPaddingVertical() {
        return dividerPaddingVertical;
    }

    /**
     * 设置分割线垂直间距
     *
     * @param dividerPaddingVertical
     */
    public void setDividerPaddingVertical(int dividerPaddingVertical) {
        this.dividerPaddingVertical = dividerPaddingVertical;
        requestLayout();
    }

    /**
     * 获取下划线
     *
     * @return
     */
    public View getUnderlineView() {
        return underlineView;
    }

    /**
     * 设置下划线
     *
     * @param underlineView
     */
    public void setUnderlineView(View underlineView) {
        this.underlineView = underlineView;
        requestLayout();
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
        requestLayout();
    }

    /**
     * 获取下划线资源
     *
     * @return
     */
    public int getUnderlineResId() {
        return underlineResId;
    }

    /**
     * 设置下划线资源
     *
     * @param underlineResId
     */
    public void setUnderlineResId(int underlineResId) {
        this.underlineResId = underlineResId;
        requestLayout();
    }

    /**
     * 获取下划线Drawable
     *
     * @return
     */
    public Drawable getUnderlineDrawable() {
        return underlineDrawable;
    }

    /**
     * 设置下划线Drawable
     *
     * @param underlineDrawable
     */
    public void setUnderlineDrawable(Drawable underlineDrawable) {
        this.underlineDrawable = underlineDrawable;
        requestLayout();
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
        requestLayout();
    }

    /**
     * 获取页面标题
     *
     * @return
     */
    public CharSequence[] getPageTitle() {
        return pageTitle;
    }

    /**
     * 设置ViewPager
     *
     * @return
     */
    public ViewPager getViewPager() {
        return viewPager;
    }

    /**
     * 获取PagerAdapter
     *
     * @return
     */
    public PagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    /**
     * 设置PagerAdapter
     *
     * @param pagerAdapter
     */
    public void setPagerAdapter(PagerAdapter pagerAdapter) {
        this.pagerAdapter = pagerAdapter;
    }

    /**
     * 获取数据监听
     *
     * @return
     */
    public ViewPagerDataSetObserver getDataSetObserver() {
        return dataSetObserver;
    }

    /**
     * 设置数据监听
     *
     * @param dataSetObserver
     */
    public void setDataSetObserver(ViewPagerDataSetObserver dataSetObserver) {
        this.dataSetObserver = dataSetObserver;
    }
}
