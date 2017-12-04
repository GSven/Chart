package com.sxt.chart.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.sxt.banner.library.adapter.BaseCyclePagerAdapter;
import com.sxt.chart.R;

import java.util.List;

/**
 * Created by izhaohu on 2017/11/10.
 */
public class BannerAdapter extends BaseCyclePagerAdapter {


    public BannerAdapter(Context context, List datas) {
        super(context, datas);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position, int datePosition) {
        ImageView img = new ImageView(context);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String url = (String) datas.get(datePosition);
        if (url != null && url.length() > 0) {
            Picasso.with(context).load(url).error(R.mipmap.pic_comon_study).into(img);
        } else {
            img.setImageResource(R.mipmap.pic_comon_study);
        }
        container.addView(img);

        return img;
    }
}
