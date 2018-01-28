package com.sxt.chart.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.sxt.chart.App;
import com.sxt.chart.R;
import com.sxt.chart.utils.ToastUtil;
import com.sxt.chart.view.HorizontalselectedView;
import com.sxt.chart.view.RulerView;
import com.sxt.chart.view.ScrollPickerView;
import com.sxt.chart.view.StringScrollPicker;
import com.sxt.chart.view.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SXT on 2018/1/25.
 */

public class ScrollTextActivity extends BaseActivity implements View.OnClickListener {
    private WheelView mWheelView, mWheelView2, mWheelView3, mWheelView4, mWheelView5;
    private TextView mSelectedTv, mChangedTv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scroll_text);
        /*final TextView select = findViewById(R.id.select);
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
        });*/


//        findViewById(R.id.one).setOnClickListener(this);
//        findViewById(R.id.two).setOnClickListener(this);
//        findViewById(R.id.three).setOnClickListener(this);
//        findViewById(R.id.four).setOnClickListener(this);

        RulerView rulerView = findViewById(R.id.rulerView);
        rulerView.setOnSelectChangeListener(new RulerView.OnSelectChangeListenrer() {
            @Override
            public void onSelectChange(RulerView rulerView, float selectValue) {
                TextView select = findViewById(R.id.rulerSelect);
                select.setText(String.valueOf(selectValue));
            }
        });

        initWWheelView();
    }

    private void initWWheelView() {
        mWheelView = (WheelView) findViewById(R.id.wheelview);
        mWheelView2 = (WheelView) findViewById(R.id.wheelview2);
        mWheelView3 = (WheelView) findViewById(R.id.wheelview3);
        mWheelView4 = (WheelView) findViewById(R.id.wheelview4);
        mWheelView5 = (WheelView) findViewById(R.id.wheelview5);
        mSelectedTv = (TextView) findViewById(R.id.selected_tv);
        mChangedTv = (TextView) findViewById(R.id.changed_tv);

        final List<String> items = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            items.add(String.valueOf(i * 1000));
        }

        mWheelView.setItems(items);
        mWheelView.selectIndex(8);
        mWheelView.setAdditionCenterMark("元");

        List<String> items2 = new ArrayList<>();
        items2.add("一月");
        items2.add("二月");
        items2.add("三月");
        items2.add("四月");
        items2.add("五月");
        items2.add("六月");
        items2.add("七月");
        items2.add("八月");
        items2.add("九月");
        items2.add("十月");
        items2.add("十一月");
        items2.add("十二月");
        mWheelView2.setItems(items2);
        List<String> items3 = new ArrayList<>();
        items3.add("1");
        items3.add("2");
        items3.add("3");
        items3.add("5");
        items3.add("7");
        items3.add("11");
        items3.add("13");
        items3.add("17");
        items3.add("19");
        items3.add("23");
        items3.add("29");
        items3.add("31");

        mWheelView3.setItems(items3);
        mWheelView3.setAdditionCenterMark("m");
//		mWheelView4.setItems(items);
//		mWheelView4.setEnabled(false);
        mWheelView5.setItems(items);
        mWheelView5.setMinSelectableIndex(3);
        mWheelView5.setMaxSelectableIndex(items.size() - 3);
        items.remove(items.size() - 1);
        items.remove(items.size() - 2);
        items.remove(items.size() - 3);
        items.remove(items.size() - 4);
        mSelectedTv.setText(String.format("onWheelItemSelected：%1$s", ""));
        mChangedTv.setText(String.format("onWheelItemChanged：%1$s", ""));
        mWheelView5.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectedListener() {
            @Override
            public void onWheelItemSelected(WheelView wheelView, int position) {
                mSelectedTv.setText(String.format("onWheelItemSelected：%1$s", wheelView.getItems().get(position)));
            }
            @Override
            public void onWheelItemChanged(WheelView wheelView, int position) {
                mChangedTv.setText(String.format("onWheelItemChanged：%1$s", wheelView.getItems().get(position)));
            }
        });

        mWheelView4.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWheelView4.setItems(items);
            }
        }, 3000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.one:
                ToastUtil.showToast(this, "one");
                break;
            case R.id.two:
                ToastUtil.showToast(this, "two");
                break;
            case R.id.three:
                ToastUtil.showToast(this, "three");
                break;
            case R.id.four:
                ToastUtil.showToast(this, "four");
                break;
        }

    }
}
