package com.sxt.chart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sxt.chart.App;
import com.sxt.chart.R;
import com.sxt.chart.adapter.MsgAdapter;
import com.sxt.chart.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sxt on 2018/1/7.
 */

public class ViewTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewtype_layout);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (i < 4) {
                data.add("TYPE_1");
            } else {
                data.add("TYPE_2");
            }
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new

                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MsgAdapter adapter = new MsgAdapter(this, data);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickLister(new MsgAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Object object) {
                startActivity(new Intent(App.getCtx(), ScoreMsgActivity.class));
            }
        });
        adapter.setOnTextViewClickListener(new MsgAdapter.OnTextViewClickListener() {
            @Override
            public void onClick(int position, Object object) {
                ToastUtil.showToast(App.getCtx(), "积分消息 position = " + position);
            }
        });
    }
}
