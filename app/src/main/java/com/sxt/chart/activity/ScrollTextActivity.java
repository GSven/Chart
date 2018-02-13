package com.sxt.chart.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.sxt.chart.App;
import com.sxt.chart.R;
import com.sxt.chart.utils.ToastUtil;
import com.sxt.chart.view.RulerViewLast;
import com.sxt.chart.view.ScrollPickerView;
import com.sxt.chart.view.StringScrollPicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SXT on 2018/1/25.
 */

public class ScrollTextActivity extends BaseActivity {
    private TextView mSelectedTv, mChangedTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scroll_text);

        StringScrollPicker scrollPicker = findViewById(R.id.scrollPicker);
        List<CharSequence> newList = new ArrayList<>();

        for (int i = 18; i < 50; i++) {
            newList.add(String.valueOf(i));
        }
        scrollPicker.setData(newList);
        scrollPicker.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
            @Override
            public void onSelected(ScrollPickerView scrollPickerView, int position) {
                ToastUtil.showToast(App.getCtx(), (String) scrollPickerView.getSelectedItem());
            }
        });

//        RulerView rulerViewLast = findViewById(R.id.rulerView);
//        rulerViewLast.setOnSelectChangeListener(new RulerView.OnSelectChangeListenrer() {
//            @Override
//            public void onSelectChange(RulerView rulerView, float selectValue) {
//                TextView select = findViewById(R.id.rulerSelect);
//                select.setText(String.valueOf(selectValue));
//            }
//        });
//        rulerViewLast.setData(100, 250);
//        rulerViewLast.setSelectValue(180);


        init();
    }

    private void init() {

        final TextView mTvWeight = (TextView) findViewById(R.id.tv_weight);
        RulerViewLast mRulerViewWeight = (RulerViewLast) findViewById(R.id.rulerView_weight);
        mRulerViewWeight.setOnValueChangeListener(new RulerViewLast.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                mTvWeight.setText(value + "kg");
            }
        });
//        mRulerViewWeight.setValue(170.0f, 100.0f, 500.0f, 1.0f);
    }

}
