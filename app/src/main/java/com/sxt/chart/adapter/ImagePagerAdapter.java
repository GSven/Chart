package com.sxt.chart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sxt.banner.library.adapter.BaseCyclePagerAdapter;
import com.sxt.chart.R;

import java.util.List;

/**
 * Created by izhaohu on 2018/1/23.
 */

public class ImagePagerAdapter extends BaseCyclePagerAdapter<Integer> {


    public ImagePagerAdapter(Context context, List<Integer> datas) {
        super(context, datas);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position, int datePosition) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_img, null, false);
        ImageView img = view.findViewById(R.id.img);
        img.setImageResource(datas.get(datePosition));
        container.addView(view);
        return view;
    }
}
