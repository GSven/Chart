package com.sxt.chart.fragment.wifi;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sxt.chart.R;
import com.sxt.chart.utils.Common;
import com.sxt.chart.utils.UtilsWifi;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by archer on 2016/4/22.
 */
public class WifiSelectedFragment extends Fragment {

    public static final String KEY_WIFI_SELECTED_TYPE = "KEY_WIFI_SELECTED_TYPE";
    public static final String KEY_WIFI_SELECTED_SSID = "KEY_WIFI_SELECTED_SSID";
    public static final String KEY_WIFI_SELECTED_LEVEL = "KEY_WIFI_SELECTED_LEVEL";
    private String type;
    private String ssid;
    private int level;

    private ProgressDialog prDialog;
    private TextView toast;
    private View msgItem;
    private LinearLayout manualLinear;
    private EditText ssidEidt;
    private EditText pwdEdit;
    private TextView commitText;

    private String c_ssid = "";
    private String c_pwd;
    private boolean isStartConnStation = false;
    private boolean isSetting = false;
    private Timer timer;
    private boolean isScanning = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ssid = getArguments().getString(KEY_WIFI_SELECTED_SSID);
        level = getArguments().getInt(KEY_WIFI_SELECTED_LEVEL);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi_selected, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toast = (TextView) view.findViewById(R.id.wifi_selected_toast);
        msgItem = view.findViewById(R.id.wifi_selected_msg_item);
        manualLinear = (LinearLayout) view.findViewById(R.id.wifi_selected_manual_linear);
        ssidEidt = (EditText) view.findViewById(R.id.wifi_selected_manual_ssid);
        pwdEdit = (EditText) view.findViewById(R.id.wifi_selected_manual_pwd);
        commitText = (TextView) view.findViewById(R.id.wifi_selected_commit_text);

        initScanSelectedView();
        commitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWifiOptions();
            }
        });
    }

    private void initScanSelectedView() {
        toast.setText("您选择了以下无线网络：");
        msgItem.setVisibility(View.VISIBLE);
        manualLinear.setVisibility(View.GONE);
        TextView name = (TextView) msgItem.findViewById(R.id.wifi_msg_item_name);
        name.setText(ssid);
        ImageView levelImg = (ImageView) msgItem.findViewById(R.id.wifi_msg_item_level);
        if (Math.abs(level) < 50) {
            levelImg.setImageResource(R.drawable.ic_wifi_level3);
        } else if (Math.abs(level) < 75) {
            levelImg.setImageResource(R.drawable.ic_wifi_level2);
        } else if (Math.abs(level) < 100) {
            levelImg.setImageResource(R.drawable.ic_wifi_level1);
        } else {
            levelImg.setImageResource(R.drawable.ic_wifi_level0);
        }
    }

    private void startWifiOptions() {
        c_ssid = ssid;
        if (pwdEdit.getText().toString().trim().length() != 0) {
            c_pwd = pwdEdit.getText().toString().trim();
            UtilsWifi.init(getActivity());
//            showProgress("正在连接wifi...");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UtilsWifi.connectWifi(c_ssid, c_pwd, UtilsWifi.WifiCipherType.WIFICIPHER_WPA);
                }
            }).start();
            isStartConnStation = true;
//            timer.schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    if (isScanning) {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
////                                hideProgress();
//                                Toast.makeText(getActivity(), "连接超时！", Toast.LENGTH_SHORT).show();
//                                ssidEidt.setText("");
//                                cancel();
//                            }
//                        });
//                    }
//                }
//            }, Common.WIFI_TIME_OUT);
        } else {
            Toast.makeText(getActivity(), "请输入无线网络密码！", Toast.LENGTH_SHORT).show();
        }
    }

}
