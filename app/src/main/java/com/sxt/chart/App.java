package com.sxt.chart;

import android.app.Application;
import android.content.Context;

/**
 * Created by izhaohu on 2017/11/10.
 */

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getCtx() {
        return context;
    }

    /**
     * 修改Picasso的默认缓存目录
     */
    public void setPicassoCacheDir() {

//        String CACHE_DIR_NAME = getResources().getString(R.string.save_image_picasso_folder);
//
//        LogUtil.i("MyApplication", CACHE_DIR_NAME);
//        //创建缓存目录
//        File file = new File(CACHE_DIR_NAME);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//
//        LogUtil.i("MyApplication", file.getPath());
//
//        //设置自定义的图片缓存目录
//        Picasso picasso = new Picasso.Builder(this).downloader(new OkHttpDownloader(file)).build();
//        Picasso.setSingletonInstance(picasso);
    }

//    //static 代码段可以防止内存泄露
//    static {
//        //设置全局的Header构建器
//        SmartRefreshLayout.setDefaultRefreshHeaderCreater(new DefaultRefreshHeaderCreater() {
//            @Override
//            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
//                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
//                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
//            }
//        });
//        //设置全局的Footer构建器
//        SmartRefreshLayout.setDefaultRefreshFooterCreater(new DefaultRefreshFooterCreater() {
//            @Override
//            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
//                //指定为经典Footer，默认是 BallPulseFooter
//                return new ClassicsFooter(context).setDrawableSize(20);
//            }
//        });
//    }

}
