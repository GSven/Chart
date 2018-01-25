package com.sxt.chart.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.sxt.chart.R;
import com.sxt.chart.view.HorizontalselectedView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SXT on 2018/1/25.
 */

public class ScrollTextActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scroll_text);
        final TextView select = findViewById(R.id.select);
        HorizontalselectedView hsView = findViewById(R.id.scrollText);

        List<String> strings = new ArrayList<>();
        for (int i = 18; i < 100; i++) {
            strings.add(String.valueOf(i));
        }

        hsView.setData(strings);
        hsView.setOnSelectChangeListener(new HorizontalselectedView.OnSelectChangeListener() {
            @Override
            public void onSelected(int position, String selectedText) {
                select.setText(selectedText);
            }
        });

    }
}
