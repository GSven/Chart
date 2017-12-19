package com.sxt.chart.fragment.wifi;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxt.chart.R;
import com.sxt.chart.fragment.BaseFragment;

/**
 * Created by izhaohu on 2017/12/15.
 */

public class AddWorkerFragment extends BaseFragment implements View.OnClickListener {

    private Activity activity;
    private TextView tvPower;
    private TextView tvConfirm;
    private Bundle bundle;
    private View view;

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
            tvPower = (TextView) view.findViewById(R.id.tv_link_power);
            tvConfirm = (TextView) view.findViewById(R.id.tv_confirm);
            tvPower.setOnClickListener(this);
            tvConfirm.setOnClickListener(this);
        }
        activity.setTitle(R.string.add_work);

        return view;
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
                        .addToBackStack(SelectWorkerWiFiFragment.class.getName())
                        .commit();
                break;
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
