package com.sxt.chart.chart;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface.OnDismissListener;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chart.R;
import com.sxt.chart.utils.Px2DpUtil;


public class LoadingDialog {
    private Activity mActivity = null;
    private LayoutInflater mInflater;
    private Dialog mDialog;
    private View rootView;
    private TextView tips;
    private TextView centerTips;
    private ImageView loading_image;
    private boolean isShowLoadingImage;

    /**
     * 原始构造函数
     *
     * @param activity
     */
    public LoadingDialog(Activity activity) {
        this(activity, true);
    }

    /**
     * isShowLoadingImage为是否显示loading旋转图片
     *
     * @param activity
     * @param isShowLoadingImage
     */
    public LoadingDialog(Activity activity, boolean isShowLoadingImage) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        this.isShowLoadingImage = isShowLoadingImage;
        init();
    }

    private void init() {
        rootView = mInflater.inflate(R.layout.dialog_layout_loading, null);
        tips = (TextView) rootView.findViewById(R.id.loading_tips);
        centerTips = (TextView) rootView.findViewById(R.id.loading_tips_center);
        loading_image = (ImageView) rootView.findViewById(R.id.loading_image);
        if (!isShowLoadingImage) {
            loading_image.setVisibility(View.INVISIBLE);
            tips.setVisibility(View.INVISIBLE);
            centerTips.setVisibility(View.VISIBLE);
        } else {
            Animation loadingAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.loading_animation);
            loading_image.startAnimation(loadingAnimation);
        }
        mDialog = new Dialog(mActivity, R.style.PopContextMenu);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
    }

    public LoadingDialog setMessage(int resId) {
        if (isShowLoadingImage) {
            tips.setText(resId);
        } else {
            centerTips.setText(resId);
        }
        return this;
    }

    public LoadingDialog setMessage(CharSequence msg) {
        if (isShowLoadingImage) {
            tips.setText(msg);
        } else {
            centerTips.setText(msg);
        }
        return this;
    }

    public LoadingDialog setMessage(String msg) {
        if (isShowLoadingImage) {
            tips.setText(msg);
        } else {
            centerTips.setText(msg);
        }
        return this;
    }

    public LoadingDialog setView(View view) {
        rootView = view;
        return this;
    }

    public LoadingDialog changeState(String msg, boolean isShowLoadingImage) {
        if (!isShowLoadingImage) {
            loading_image.setVisibility(View.INVISIBLE);
            tips.setVisibility(View.INVISIBLE);
            centerTips.setVisibility(View.VISIBLE);
            centerTips.setText(msg);
            return this;
        } else {
            loading_image.setVisibility(View.VISIBLE);
            tips.setVisibility(View.VISIBLE);
            centerTips.setVisibility(View.INVISIBLE);
            tips.setText(msg);
            return this;
        }
    }

    public LoadingDialog setCancelable(boolean cancelable) {
        if (mDialog != null) {
            mDialog.setCancelable(cancelable);
        }
        return this;
    }

    public LoadingDialog setOnDismissListener(
            final OnDismissListener listener) {
        if (mDialog != null) {
            mDialog.setOnDismissListener(listener);
        }
        return this;
    }

    @SuppressWarnings("deprecation")
    public void show() {
        try {
            mDialog.setContentView(rootView);
            mDialog.show();
            Window window = mDialog.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            WindowManager wm = mActivity.getWindowManager();
            Display display = wm.getDefaultDisplay();
            layoutParams.width = Px2DpUtil.dip2px(mActivity, 148);
            layoutParams.height = Px2DpUtil.dip2px(mActivity, 60);
            //layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }
}
