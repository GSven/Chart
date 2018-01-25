package com.sxt.chart.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.sxt.chart.App;
import com.sxt.chart.R;
import com.sxt.chart.utils.ToastUtil;
import com.sxt.chart.view.HorizontalselectedView;
import com.sxt.chart.view.ScrollPickerView;
import com.sxt.chart.view.StringScrollPicker;

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

        StringScrollPicker scrollPicker = findViewById(R.id.scrollPicker);
        List<CharSequence> newList = new ArrayList<>();
        newList.addAll(strings);
        scrollPicker.setData(newList);
        scrollPicker.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView scrollPickerView, int position) {
                ToastUtil.showToast(App.getCtx(), (String) scrollPickerView.getSelectedItem());
            }
        });
    }
}
