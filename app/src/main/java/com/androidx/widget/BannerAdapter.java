package com.androidx.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.androidx.R;

import java.util.List;

/**
 * Created by Relin
 * on 2018-10-09.
 */
public abstract class BannerAdapter<T> extends PagerAdapter implements ViewHolder.OnItemClickLister, ViewHolder.OnItemFocusChangeListener {

    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 数据
     */
    private List<T> data;
    /**
     * 数据
     */
    private List<T> realData;
    /**
     * ItemView
     */
    private View convertView;
    /**
     * 位置
     */
    private int position;
    /**
     * 是否循环
     */
    private boolean isLoop = true;
    /**
     * 控件容器
     */
    private ViewHolder viewHolder;


    public BannerAdapter(Context context) {
        this.context = context;
    }

    /**
     * 获取View容器
     *
     * @return
     */
    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    /**
     * 获取数据大小
     *
     * @return
     */
    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * 自定义item视图
     *
     * @return
     */
    public int getItemLayoutResId() {
        return 0;
    }

    /**
     * 获取Item视图
     *
     * @param position 位置
     * @return
     */
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * 获取Item视图
     *
     * @param context
     * @param viewType
     * @return
     */
    protected View getItemView(Context context, int viewType) {
        return LayoutInflater.from(context).inflate(getItemLayoutResId(), null);
    }

    /**
     * 获取item
     *
     * @param position    位置
     * @param convertView item View
     * @param parent      父控件
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (getItemLayoutResId() == 0) {
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setId(R.id.banner_image);
                convertView = imageView;
            } else {
                convertView = getItemView(getContext(), getItemViewType(position));
            }
            viewHolder = new ViewHolder(convertView);
            viewHolder.setItemPosition(position);
            viewHolder.setOnItemClickLister(this);
            viewHolder.setOnItemFocusChangeListener(this);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        onItemBindViewHolder(viewHolder, getItem(position), position);
        if (getItemLayoutResId() == 0) {
            viewHolder.addItemClick(R.id.banner_image);
        }
        return convertView;
    }

    /**
     * 绑定View数据
     *
     * @param holder   控件容器
     * @param item     实体
     * @param position 位置
     */
    public abstract void onItemBindViewHolder(ViewHolder holder, T item, int position);

    /**
     * 实例化Item
     *
     * @param parent   容器
     * @param position 位置
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup parent, int position) {
        this.position = position;
        convertView = getView(position, null, parent);
        parent.addView(convertView);
        return convertView;
    }

    /**
     * 判断是否是同一个Item
     *
     * @param view
     * @param object
     * @return
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 摧毁item
     *
     * @param container 容器
     * @param position  位置
     * @param object    对象
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        this.position = position;
        container.removeView((View) object);
    }


    /**
     * 设置是否循环滑动
     *
     * @param isLoop
     */
    public void setLoop(boolean isLoop) {
        setLoop(isLoop, true);
    }

    /**
     * @param isLoop
     */
    public void setLoop(boolean isLoop, boolean isNotify) {
        if (getCount() > 0 && isLoop) {
            data.add(0, data.get((getCount() - 1)));
            data.add(data.get(1));
        }
        this.isLoop = isLoop;
        if (isNotify) {
            notifyDataSetChanged();
        }
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setItems(List<T> data) {
        this.realData = data;
        this.data = data;
        setLoop(isLoop, false);
        notifyDataSetChanged();
        if (onDataSetChangeListener != null) {
            onDataSetChangeListener.onDataSetChanged(this);
        }
    }

    /**
     * 是否循环滑动
     *
     * @return
     */
    public boolean isLoop() {
        return isLoop;
    }

    /**
     * 获取上下文对象
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取数据
     *
     * @return
     */
    public List<T> getItems() {
        return data;
    }

    /**
     * 获取原数据
     *
     * @return
     */
    public List<T> getRealData() {
        return realData;
    }

    /**
     * 获取真实数据的位置
     *
     * @param position
     * @return
     */
    public int getRealPosition(int position) {
        if (!isLoop) {
            return position;
        }
        if (position == 0) {
            return getCount() - 2;
        } else if (position == getCount() - 1) {
            return 0;
        } else {
            return position - 1;
        }
    }

    /**
     * 获取当前位置
     *
     * @return
     */
    public int getPosition() {
        return position;
    }

    /**
     * 获取Item对象
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        return data.get(position);
    }

    /**
     * 获取Item View
     *
     * @return
     */
    public View getItemView() {
        return convertView;
    }

    @Override
    public void onItemClick(View v, int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(this, v, getItem(position), position);
        }
    }

    @Override
    public void onItemFocusChange(View v, int position, boolean hasFocus) {
        if (onItemFocusChangeListener != null) {
            onItemFocusChangeListener.onItemFocusChange(this, v, getItem(position), position, hasFocus);
        }
    }

    /**
     * 点击事件
     */
    private OnItemClickListener<T> onItemClickListener;

    /**
     * 设置点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener<T> {

        /**
         * Item点击
         *
         * @param adapter  适配器
         * @param view     视图
         * @param item     实体
         * @param position 位置
         */
        void onItemClick(BannerAdapter<T> adapter, View view, T item, int position);

    }

    /**
     * 设置焦点改变点击事件
     */
    public OnItemFocusChangeListener<T> onItemFocusChangeListener;

    /**
     * 获取焦点改变事件
     *
     * @return
     */
    public OnItemFocusChangeListener<T> getOnItemFocusChangeListener() {
        return onItemFocusChangeListener;
    }

    /**
     * 获取焦点改变事件
     *
     * @param onItemFocusChangeListener
     */
    public void setOnItemFocusChangeListener(OnItemFocusChangeListener<T> onItemFocusChangeListener) {
        this.onItemFocusChangeListener = onItemFocusChangeListener;
        notifyDataSetChanged();
    }

    /**
     * 焦点改变事件
     *
     * @param <T>
     */
    public interface OnItemFocusChangeListener<T> {

        /**
         * 焦点修改
         *
         * @param adapter  适配器
         * @param v        控件
         * @param item     实体
         * @param position 位置
         * @param hasFocus 是否获取焦点
         */
        void onItemFocusChange(BannerAdapter<T> adapter, View v, T item, int position, boolean hasFocus);

    }

    public OnDataSetChangeListener<T> onDataSetChangeListener;

    public void setOnDataSetChangeListener(OnDataSetChangeListener<T> onDataSetChangeListener) {
        this.onDataSetChangeListener = onDataSetChangeListener;
    }

    public interface OnDataSetChangeListener<T> {

        void onDataSetChanged(BannerAdapter<T> adapter);

    }

}
