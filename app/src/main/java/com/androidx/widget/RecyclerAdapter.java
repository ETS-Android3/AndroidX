package com.androidx.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidx.util.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Relin
 * Describe:Recycler使用的基础Adapter
 * Date:2020/12/26 19:17
 */
public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter implements ViewHolder.OnItemClickLister, ViewHolder.OnItemFocusChangeListener {

    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 数据对象
     */
    private List<T> data;
    /**
     * 空视图
     */
    private View emptyView;
    /**
     * View容器
     */
    private ViewHolder viewHolder;

    public RecyclerAdapter() {

    }

    public RecyclerAdapter(Context context) {
        this.context = context;
    }

    /**
     * 获取Item布局资源
     *
     * @return
     */
    protected abstract int getItemLayoutResId(int viewType);

    /**
     * 获取View容器
     *
     * @return
     */
    public ViewHolder getViewHolder() {
        return viewHolder;
    }

    /**
     * 获取Item布局视图
     *
     * @param parent   父级
     * @param viewType 类型
     * @return
     */
    protected View getItemView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(getContext()).inflate(getItemLayoutResId(viewType), parent, false);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getItemView(parent, viewType));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        viewHolder = (ViewHolder) vh;
        viewHolder.setItemPosition(position);
        viewHolder.setOnItemClickLister(this);
        viewHolder.setOnItemFocusChangeListener(this);
        onItemBindViewHolder(viewHolder, getItem(position), position);
    }

    /**
     * 绑定数据
     *
     * @param holder   控件容器
     * @param item     单个数据
     * @param position 位置
     */
    protected abstract void onItemBindViewHolder(ViewHolder holder, T item, int position);

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        int itemCount = Size.of(data);
        if (emptyView != null) {
            emptyView.setVisibility(itemCount == 0 ? View.VISIBLE : View.GONE);
        }
        return itemCount;
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
            emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
        if (notify) {
            notifyDataSetChanged();
        }
        getItemCount();
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
     * 添加Items
     *
     * @param data
     */
    public void addItems(List<T> data) {
        int size = getItemCount();
        int positionStart = size == 0 ? 0 : size;
        if (data == null) {
            data = new ArrayList<>();
        }
        data.addAll(data);
        notifyItemRangeInserted(positionStart, getItemCount());
    }

    /**
     * 添加Item
     *
     * @param t
     */
    public void addItem(T t) {
        if (data == null) {
            data = new ArrayList<>();
        }
        if (t != null) {
            data.add(t);
        }
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * 删除Item
     *
     * @param position
     */
    public void removeItem(int position) {
        if (getItemCount() > 0) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * 删除Item
     *
     * @param positionStart
     */
    public void removeItems(int positionStart, int itemCount) {
        if (getItemCount() > 0) {
            for (int i = 0; i < getItemCount() && itemCount <= getItemCount(); i++) {
                if (i >= positionStart && i < itemCount) {
                    data.remove(i);
                }
            }
            notifyItemRangeRemoved(positionStart, itemCount);
        }
    }

    /**
     * 移动Item
     *
     * @param fromPosition 原位置
     * @param toPosition   新位置
     */
    public void moveItem(int fromPosition, int toPosition) {
        data.add(toPosition, data.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
        notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition - toPosition) + 1);
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
     * 获取Item
     *
     * @param position 位置
     * @return
     */
    public T getItem(int position) {
        if (data == null) {
            return null;
        }
        return data.get(position);
    }

    /**
     * 获取空视图
     *
     * @return
     */
    public View getEmptyView() {
        return emptyView;
    }

    /**
     * 设置空视图
     *
     * @param emptyView 视图
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
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
     * 设置Item点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 获取Item点击事件
     *
     * @return
     */
    public OnItemClickListener<T> getOnItemClickListener() {
        return onItemClickListener;
    }

    /**
     * Item点击事件回调
     *
     * @param <T>
     */
    public interface OnItemClickListener<T> {

        /**
         * Item点击
         *
         * @param adapter  适配器
         * @param v        数据
         * @param item     数据
         * @param position 位置
         */
        void onItemClick(RecyclerAdapter<T> adapter, View v, T item, int position);

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
         * 焦点修改
         *
         * @param adapter  适配器
         * @param v        控件
         * @param item     实体
         * @param position 位置
         * @param hasFocus 是否获取焦点
         */
        void onItemFocusChange(RecyclerAdapter<T> adapter, View v, T item, int position, boolean hasFocus);

    }

}
