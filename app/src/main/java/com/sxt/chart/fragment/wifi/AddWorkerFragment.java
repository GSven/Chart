package com.sxt.chart.fragment.wifi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxt.chart.R;
import com.sxt.chart.fragment.BaseFragment;
import com.sxt.chart.utils.ToastUtil;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by izhaohu on 2017/12/15.
 */

public class AddWorkerFragment extends BaseFragment implements View.OnClickListener {

    private Activity activity;
    private TextView tvPower;
    private TextView tvConfirm;
    private Bundle bundle;
    private View view;
    private GifDrawable gif;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.fragment_add_work_layout, null);
            GifImageView img = (GifImageView) view.findViewById(R.id.img);
            gif = (GifDrawable) img.getDrawable();
            view.findViewById(R.id.start).setOnClickListener(this);
            view.findViewById(R.id.stop).setOnClickListener(this);
            view.findViewById(R.id.reset).setOnClickListener(this);
            tvPower = (TextView) view.findViewById(R.id.tv_link_power);
            tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
            tvPower.setOnClickListener(this);
            tvConfirm.setOnClickListener(this);
        }
        activity.setTitle(R.string.add_work);

        return view;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onResume() {
        super.onResume();
//        Glide.with(this)
//                .load(R.drawable.loading1)
//                .asGif()
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .into(img);
    }

    public void start(View view) {
        gif.start();
    }

    public void stop(View view) {
        gif.stop();
    }

    public void reset(View view) {
        gif.reset();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_link_power:
                linkPower();
                break;

            case R.id.tv_confirm:
                SelectWorkerWiFiFragment fragment = new SelectWorkerWiFiFragment();
                fragment.setArguments(bundle);
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.wifi_setting_content, fragment, SelectWorkerWiFiFragment.class.getName())
//                        .addToBackStack(SelectWorkerWiFiFragment.class.getName())
                        .commit();
                break;

            case R.id.start:
                start(view);
                break;
            case R.id.stop:
                stop(view);
                break;
            case R.id.reset:
                reset(view);
                break;
        }
    }

    /**
     * 跳转到系统短信页面发送
     */
    private void sendSMS() {

        String trim = "17612109892";

//            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + trim));
//            // 如果需要将内容传过去增加如下代码
//            intent.putExtra("sms_body", SMS.toString());
//            startActivityForResult(intent, SEND_SMS_REQUEST);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 如果需要将内容传过去增加如下代码.0
        intent.putExtra("sms_body", "呵呵额");
        intent.putExtra("address", trim);
        intent.setType("vnd.android-dir/mms-sms");
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            ToastUtil.showToast(activity, "回来了");
        }
    }

    private boolean isLink;

    private void linkPower() {
        isLink = !isLink;
        if (isLink) {
            Drawable drawable = getResources().getDrawable(R.drawable.credit_button_choosen);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvPower.setCompoundDrawables(drawable, null, null, null);
            tvConfirm.setEnabled(true);
            tvConfirm.setBackgroundResource(R.drawable.green_solid_round_radios38_bg);

        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.credit_button_unchoosen);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvPower.setCompoundDrawables(drawable, null, null, null);
            tvConfirm.setEnabled(false);
            tvConfirm.setBackgroundResource(R.drawable.green_white_solid_round_radios38_bg);
        }
    }

}
