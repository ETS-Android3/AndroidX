package com.androidx.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.androidx.util.Size;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Relin
 * Describe:ListView,GridView使用的基础Adapter
 * Date:2020/12/26 19:16
 */
public abstract class BasisAdapter<T> extends BaseAdapter implements ViewHolder.OnItemClickLister
        , ViewHolder.OnItemFocusChangeListener {

    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 数据
     */
    private List<T> data;

    /**
     * 空视图
     */
    private View emptyView;
    /**
     * 控件容器
     */
    private ViewHolder viewHolder;

    public BasisAdapter() {

    }

    public BasisAdapter(Context context) {
        this.context = context;
    }

    /**
     * Item布局资源id
     *
     * @return
     */
    protected abstract int getItemLayoutResId(int viewType);

    /**
     * 获取视图类型
     *
     * @param position
     * @return
     */
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * 获取控件容器
     *
     * @return
     */
    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    /**
     * 获取类型View
     *
     * @param parent   父级
     * @param viewType 类型
     * @return
     */
    protected View getItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(getContext()).inflate(getItemLayoutResId(viewType), parent, false);
    }

    /**
     * 绑定Item视图<br/>
     * 给控件设置数据，{@link ViewHolder#find(Class, int)}
     *
     * @param holder   视图容器
     * @param item     单个数据
     * @param position 位置
     */
    public abstract void onItemBindViewHolder(ViewHolder holder, T item, int position);

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = getItemView(parent, getItemViewType(position));
            viewHolder = new ViewHolder(convertView);
            viewHolder.setItemPosition(position);
            viewHolder.setOnItemClickLister(this);
            viewHolder.setOnItemFocusChangeListener(this);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        onItemBindViewHolder(viewHolder, getItem(position), position);
        return convertView;
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
     * 获取空试图
     *
     * @return
     */
    public View getEmptyView() {
        return emptyView;
    }

    /**
     * 设置空试图
     *
     * @param emptyView
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    /**
     * 获取数据
     *
     * @return
     */
    public List<T> getItems() {
        if (data == null) {
            data = new ArrayList<>();
        }
        return data;
    }

    /**
     * 设置数据源
     *
     * @param data
     */
    public void setItems(List<T> data) {
        setItems(data, true);
    }

    /**
     * 设置数据
     *
     * @param data
     */
    public void setItems(List<T> data, boolean notify) {
        this.data = data;
        if (emptyView != null) {
            emptyView.setVisibility(getCount() == 0 ? View.GONE : View.VISIBLE);
        }
        if (notify) {
            notifyDataSetChanged();
        }
        getCount();
    }

    /**
     * 设置分页数据
     *
     * @param page 页面
     * @param data 数据
     */
    public void setPageItems(int page, List<T> data) {
        if (page == 1) {
            setItems(data);
        } else {
            addItems(data);
        }
    }

    /**
     * 添加Item
     *
     * @param t
     */
    public void addItem(T t) {
        if (t != null) {
            getItems().add(t);
        }
        notifyDataSetChanged();
    }

    /**
     * 添加Item
     *
     * @param items
     */
    public void addItems(List<T> items) {
        getItems().addAll(items);
        notifyDataSetChanged();
    }

    /**
     * 删除Item
     *
     * @param position
     */
    public void removeItem(int position) {
        if (getCount() > 0 && position < getCount()) {
            getItems().remove(position);
        }
        notifyDataSetChanged();
    }

    /**
     * 删除Items
     *
     * @param positionStart
     * @param itemCount
     */
    public void removeItems(int positionStart, int itemCount) {
        for (int i = 0; i < getCount(); i++) {
            if (i >= positionStart && i < itemCount) {
                getItems().remove(i);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        int count = Size.of(data);
        if (emptyView != null) {
            emptyView.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
        }
        return count;
    }

    @Override
    public T getItem(int position) {
        return getItems().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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
     * Item点击事件
     */
    private OnItemClickListener<T> onItemClickListener;

    /**
     * 获取Item点击事件
     *
     * @return
     */
    public OnItemClickListener<T> getOnItemClickListener() {
        return onItemClickListener;
    }

    /**
     * 设置Item点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 点击事件回调接口
     *
     * @param <T>
     */
    public interface OnItemClickListener<T> {

        /**
         * Item点击
         *
         * @param adapter  item视图
         * @param view     视图
         * @param item     实体
         * @param position 位置
         */
        void onItemClick(BasisAdapter<T> adapter, View view, T item, int position);

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
    }

    /**
     * 焦点改变事件
     *
     * @param <T>
     */
    public interface OnItemFocusChangeListener<T> {

        /**
         * Item焦点监听
         *
         * @param adapter  适配器
         * @param view     控件
         * @param position 位置
         * @param item     实体
         * @param hasFocus 是否获取焦点
         */
        void onItemFocusChange(BasisAdapter<T> adapter, View view, T item, int position, boolean hasFocus);

    }

}
