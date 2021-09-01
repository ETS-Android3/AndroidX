package com.androidx;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidx.app.CoreActivity;
import com.androidx.app.Loading;
import com.androidx.app.NavigationBar;
import com.androidx.util.Log;
import com.androidx.widget.BasisAdapter;
import com.androidx.widget.RecyclerAdapter;
import com.androidx.widget.SwipeLayout;
import com.androidx.widget.SwipeRequestLayout;
import com.androidx.widget.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class AndroidX extends CoreActivity implements BasisAdapter.OnItemClickListener<String>, SwipeLayout.OnSwipeLoadListener {

    private SwipeRequestLayout swipe;
    private RecyclerView rv_content;
    private ListView lv_content;
    private ItemAdapter adapter;
    private List<String> list;

    @Override
    protected int getContentViewResId() {
        return R.layout.android_x;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState, NavigationBar bar) {
        bar.setTitle("AndroidX");
        bar.setBackgroundColor(Color.parseColor("#0EB692"));
        swipe = findViewById(R.id.swipe);
        swipe.setOnSwipeLoadListener(this);
        rv_content = findViewById(R.id.rv_content);
        lv_content = findViewById(R.id.lv_content);
        adapter = new ItemAdapter(this);
        adapter.setOnItemClickListener(this);
//        rv_content.setLayoutManager(new LinearLayoutManager(this));
//        rv_content.setAdapter(adapter);

        lv_content.setAdapter(adapter);

        list = new ArrayList<>();
        list.add("SWIPE REFRESH START");
        list.add("SWIPE REFRESH STOP");
        list.add("SHOW LOADING");
        list.add("DISMISS LOADING");
        for (int i = 0; i < 20; i++) {
            list.add("item " + (i + 1));
        }
        list.add("SWIPE LOADING START");
        list.add("SWIPE LOADING STOP");
        adapter.setItems(list);

//        showLoading(Loading.TOP, Loading.HORIZONTAL, "");
//        getLoading().showDialog(Loading.VERTICAL,"Loading");
//        getLoading().showCover(Loading.VERTICAL,"首页",R.color.colorDebugButton);
//        getLoading().showUpper();
        
    }

    private int count = 0;

    @Override
    public void onSwipeLoad() {
        super.onSwipeLoad();
        Log.i("RRL", "->onSwipeLoad");
        if (count < 2) {
            for (int i = 0; i < 5; i++) {
                list.add("LOAD " + (i + 1));
            }
        }
        count++;
        adapter.setItems(list);
        swipe.setLoading(false);

    }

//    @Override
//    public void onItemClick(RecyclerAdapter<String> adapter, View v, String item, int position) {
//        switch (item) {
//            case "SWIPE REFRESH START":
//                swipe.setRefreshing(true);
//                break;
//            case "SWIPE REFRESH STOP":
//                swipe.setRefreshing(false);
//                break;
//            case "SWIPE LOADING START":
//                swipe.setLoading(true);
//                break;
//            case "SWIPE LOADING STOP":
//                swipe.setLoading(false);
//                break;
//        }
//    }

    @Override
    public void onItemClick(BasisAdapter<String> adapter, View view, String item, int position) {
        Log.i("RRL", "->onItemClick position=" + position + ",item=" + item);
        switch (item) {
            case "SWIPE REFRESH START":
                swipe.setRefreshing(true);
                break;
            case "SWIPE REFRESH STOP":
                swipe.setRefreshing(false);
                break;
            case "SHOW LOADING":
                getLoading().showUpper();
                break;
            case "DISMISS LOADING":
                dismissLoading();
                break;
            case "SWIPE LOADING START":
                swipe.setLoading(true);
                break;
            case "SWIPE LOADING STOP":
                swipe.setLoading(false);
                break;
        }
    }

    private class ItemAdapter extends BasisAdapter<String> {

        public ItemAdapter(Context context) {
            super(context);
        }

        @Override
        protected int getItemLayoutResId(int viewType) {
            return R.layout.androidx_items;
        }

        @Override
        protected void onItemBindViewHolder(ViewHolder holder, String item, int position) {

            holder.find(TextView.class, R.id.btn_item).setText(item);
            holder.addItemClick(R.id.btn_item);
        }
    }

}
