package com.androidx.app;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.androidx.R;
import com.androidx.dialog.CoreDialog;
import com.androidx.net.Http;
import com.androidx.net.ResponseBody;
import com.androidx.text.Time;
import com.androidx.view.DragTextView;
import com.androidx.widget.BasisAdapter;
import com.androidx.widget.ViewHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Author: Relin
 * Describe:调试
 * Date:2021/01/01 22:12
 */
public class Debug implements View.OnClickListener {

    /**
     * 上下文
     */
    private Context context;
    /**
     * 父级
     */
    private ViewGroup parent;
    /**
     * 内容布局
     */
    private View contentView;
    /**
     * 点击次数
     */
    private int clickCount = 0;
    /**
     * 列表
     */
    private ListView listView;
    /**
     * 适配器
     */
    private DebugAdapter adapter;
    /**
     * 列表数据
     */
    private List<ResponseBody> bodies;
    /**
     * 弹框
     */
    private CoreDialog dialog;


    public Debug(Context context, ViewGroup parent) {
        this.context = context;
        this.parent = parent;
        onCreate(context, parent);
    }

    /**
     * 创建
     *
     * @param context 上下文
     * @param parent  父级
     */
    protected void onCreate(Context context, ViewGroup parent) {
        if (!Http.options().isDebug()) {
            return;
        }
        View buttonView = LayoutInflater.from(context).inflate(R.layout.android_debug_button, parent, true);
        DragTextView button = buttonView.findViewById(R.id.debug_button);
        button.setOnClickListener(this);
        CoreDialog.Builder builder = new CoreDialog.Builder(context);
        builder.cancelable(true);
        builder.canceledOnTouchOutside(true);
        builder.themeResId(CoreDialog.THEME_TRANSLUCENT);
        builder.layoutResId(R.layout.android_debug_content);
        builder.width(LinearLayout.LayoutParams.MATCH_PARENT);
        builder.height(LinearLayout.LayoutParams.MATCH_PARENT);
        builder.animResId(CoreDialog.ANIM_BOTTOM);
        builder.gravity(Gravity.BOTTOM);
        dialog = builder.build();
        onBindView(dialog.contentView);
    }

    /**
     * 绑定数据
     *
     * @param contentView 内容
     */
    protected void onBindView(View contentView) {
        this.contentView = contentView;
        contentView.findViewById(R.id.debug_close).setOnClickListener(this);
        listView = contentView.findViewById(R.id.debug_list);
        bodies = new ArrayList<>();
        adapter = new DebugAdapter(context);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.debug_close) {
            dialog.dismiss();
        }
        if (v.getId() == R.id.debug_button) {
            clickCount++;
            if (clickCount < 2) {
                return;
            }
            if (!dialog.isShowing()) {
                dialog.show();
            } else {
                dialog.dismiss();
            }
            clickCount = 0;
        }
    }

    /**
     * 添加请求结果
     *
     * @param body
     */
    public void addResponseBody(ResponseBody body) {
        if (!Http.options().isDebug()) {
            return;
        }
        bodies.add(body);
        sort(bodies);
        adapter.setItems(bodies);
    }

    /**
     * 排序
     *
     * @param bodies
     */
    protected void sort(List<ResponseBody> bodies) {
        Collections.sort(bodies, new Comparator<ResponseBody>() {
            @Override
            public int compare(ResponseBody o1, ResponseBody o2) {
                long time1 = Time.parse(o1.time()).getTime();
                long time2 = Time.parse(o2.time()).getTime();
                if (time1 > time2) {
                    return -1;
                }
                if (time1 < time2) {
                    return 1;
                }
                return 0;
            }
        });
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取适配器
     *
     * @return
     */
    public DebugAdapter getAdapter() {
        return adapter;
    }

    /**
     * 获取数据
     *
     * @return
     */
    public List<ResponseBody> getBodies() {
        return bodies;
    }

    /**
     * 获取列表控件
     *
     * @return
     */
    public ListView getListView() {
        return listView;
    }

    /**
     * 获取内容视图
     *
     * @return
     */
    public View getContentView() {
        return contentView;
    }

    private class DebugAdapter extends BasisAdapter<ResponseBody> {

        public DebugAdapter(Context context) {
            super(context);
        }

        @Override
        public int getItemLayoutResId(int viewType) {
            return R.layout.android_debug_item;
        }

        @Override
        public void onItemBindViewHolder(ViewHolder holder, ResponseBody item, int position) {
            holder.find(TextView.class, R.id.debug_row_page_value).setText(item.page());
            holder.find(TextView.class, R.id.debug_row_time_value).setText(item.time());
            holder.find(TextView.class, R.id.debug_row_url_value).setText(item.url());
            holder.find(TextView.class, R.id.debug_row_header_value).setText(item.requestParams().header() == null ? "" : item.requestParams().header().toString());
            holder.find(TextView.class, R.id.debug_row_params_value).setText(item.requestParams().params() == null ? "" : item.requestParams().params().toString());
            holder.find(TextView.class, R.id.debug_row_result_value).setText(item.body());
        }

    }

}
