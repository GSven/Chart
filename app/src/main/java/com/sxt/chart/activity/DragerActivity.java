package com.sxt.chart.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sxt.chart.App;
import com.sxt.chart.R;
import com.sxt.chart.adapter.WifiIzhaohuAdapter2;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by izhaohu on 2017/12/21.
 */

public class DragerActivity extends AppCompatActivity {

    protected SwipeMenuRecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.ItemDecoration mItemDecoration;

    protected List<String> mDataList;
    private WifiIzhaohuAdapter2 adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drager);
        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);

        mDataList = createDataList();
        adapter = new WifiIzhaohuAdapter2(this, mDataList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    protected List<String> createDataList() {
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            dataList.add("第" + i + "个Item");
        }
        return dataList;
    }


    public class ViewHolder {

        public View root;
        public View line;
        public TextView tvWifiName;
        public TextView tvWifiType;
        public ImageView imgWifiLevel;

    }


    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加左侧的，如果不添加，则左侧不会出现菜单。
//            {
//                SwipeMenuItem addItem = new SwipeMenuItem(App.getCtx())
//                        .setBackground(R.color.yellow_green_middle)
//                        .setImage(R.mipmap.xuetang)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeLeftMenu.addMenuItem(addItem); // 添加菜单到左侧。
//
//                SwipeMenuItem closeItem = new SwipeMenuItem(App.getCtx())
//                        .setBackground(R.color.alpha_2)
//                        .setImage(R.mipmap.heart)
//                        .setWidth(width)
//                        .setHeight(height);
//                swipeLeftMenu.addMenuItem(closeItem); // 添加菜单到左侧。
//            }

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(App.getCtx())
                        .setBackground(R.color.gray)
//                        .setImage(R.mipmap.xuetang)
                        .setText("删除")
                        .setTextColor(Color.BLACK)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。

                SwipeMenuItem addItem = new SwipeMenuItem(App.getCtx())
                        .setBackground(R.color.green)
                        .setText("添加")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(addItem); // 添加菜单到右侧。
            }
        }
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();

            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(DragerActivity.this, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(DragerActivity.this, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
