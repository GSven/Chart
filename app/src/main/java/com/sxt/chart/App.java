package com.sxt.chart;

import android.app.Application;
import android.content.Context;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;
import com.sxt.chart.utils.LogUtil;
import com.yanzhenjie.nohttp.NoHttp;

import java.io.File;

/**
 * Created by izhaohu on 2017/11/10.
 */

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        setPicassoCacheDir();
        NoHttp.initialize(this);
    }

    public static Context getCtx() {
        return context;
    }

    /**
     * 修改Picasso的默认缓存目录
     */
    public void setPicassoCacheDir() {

        String CACHE_DIR_NAME = getResources().getString(R.string.save_image_picasso_folder);

        LogUtil.i("MyApplication", CACHE_DIR_NAME);
        //创建缓存目录
        File file = new File(CACHE_DIR_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }

        LogUtil.i("MyApplication", file.getPath());

        //设置自定义的图片缓存目录
        Picasso picasso = new Picasso.Builder(this).downloader(new OkHttpDownloader(file)).build();
        Picasso.setSingletonInstance(picasso);

    }
}
