package com.sxt.chart.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sxt.banner.library.adapter.BaseCyclePagerAdapter;
import com.sxt.chart.R;

import java.util.List;

/**
 * Created by izhaohu on 2017/11/10.
 */
public class BannerAdapter extends BaseCyclePagerAdapter<String> {


    public BannerAdapter(Context context, List<String> datas) {
        super(context, datas);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position, int datePosition) {
        ImageView img = new ImageView(context);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(context).load(datas.get(datePosition)).error(R.mipmap.pic_comon_study).placeholder(R.mipmap.pic_comon_study).into(img);
        container.addView(img);
        return img;
    }
}
