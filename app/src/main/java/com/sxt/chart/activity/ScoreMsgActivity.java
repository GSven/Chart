package com.sxt.chart.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.sxt.chart.R;
import com.sxt.chart.adapter.ScoreMsgAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sxt on 2018/1/7.
 */

public class ScoreMsgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_layout);

        List<String> data = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            if (i < 3) {
                data.add("1992-11-27 12:00");
            } else if(i<8){
                data.add("1993-03-15 12:00");
            }else{
                data.add("1992-11-27 12:00");
            }
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new ScoreMsgAdapter(this, data));
    }
}
