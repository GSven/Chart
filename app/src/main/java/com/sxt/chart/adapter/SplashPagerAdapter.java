package com.sxt.chart.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sxt.chart.activity.MainActivity;

import java.util.List;

/**
 * Created by izhaohu on 2017/11/27.
 */

public class SplashPagerAdapter extends BasePagerAdapter<Integer> {

    public SplashPagerAdapter(Context context, List<Integer> data) {
        super(context, data);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView img = new ImageView(context);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        img.setImageResource(data.get(position));

        container.addView(img);

        if (position == data.size() - 1) {
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                    context.startActivity(new Intent(context, MainActivity.class));
                }
            });
        }

        return img;
    }
}
