package com.sxt.chart.fragment.wifi;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chart.R;
import com.sxt.chart.adapter.WifiIzhaohuAdapter;
import com.sxt.chart.chart.DialogBuilder;
import com.sxt.chart.chart.LoadingDialog;
import com.sxt.chart.fragment.BaseFragment;
import com.sxt.chart.utils.LogUtil;
import com.sxt.chart.utils.ToastUtil;
import com.sxt.chart.utils.WifiUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;

import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION;
import static com.sxt.chart.utils.WifiUtils.WIFICIPHER_WPA;

/**
 * Created by izhaohu on 2017/12/15.
 */

public class SelectIzhaohuWiFiFragment extends BaseFragment implements View.OnClickListener {

    private Activity activity;
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private ImageView img;
    private TextView tvSetWiFi;
    private TextView tvWiFiname;
    private View inputWifiItem;
    private DialogBuilder inputWifiBuilder;
    private TextView tvWifiName;
    private EditText etWifiPwd;
    private WifiIzhaohuAdapter wifiAdapter;
    private DialogBuilder builder;
    private ArrayList<ScanResult> scanResults = new ArrayList<>();
    private ArrayList<ScanResult> scanResultsIzhaoHu = new ArrayList<>();
    private LoadingDialog loadingDialog;
    private Socket client;
    private SocketClientThread socketClientThread;
    private Timer timer;
    private WifiCountDown downTimer;
    private boolean isStartConnStation = false;
    private boolean isClientStart = false;
    private String addressIP;
    private String sendMsg;
    private String conn_ssid;
    private String c_ssid;
    private String c_pwd;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        c_ssid = getArguments().getString(Prefs.KEY_WIFI_SSID);
//        c_pwd = getArguments().getString(Prefs.KEY_WIFI_PWD);

        c_pwd = "vsi@2017";

        timer = new Timer();
        downTimer = new WifiCountDown(15 * 1000, 1000);

        initWifiSettings();
        loadingDialog = new LoadingDialog(activity);
        loadingDialog.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_select_wifi_layout, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        img = (ImageView) view.findViewById(R.id.img);
        tvWiFiname = (TextView) view.findViewById(R.id.tv_current_wifi);
        tvSetWiFi = (TextView) view.findViewById(R.id.tv_setWifi);

        activity.setTitle(R.string.select_izhaohu_wifi);
        tvSetWiFi.setOnClickListener(this);
    }

    private void initWifiSettings() {
        if (wifiManager == null) {
            wifiManager = WifiUtils.getInstance().getmWifiManager();
        }
        if (!WifiUtils.getInstance().isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);//开启WIFI
        }
        if (wifiReceiver == null) {
            wifiReceiver = new WifiReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(SCAN_RESULTS_AVAILABLE_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.setPriority(1000);
            getActivity().registerReceiver(wifiReceiver, filter);
        }
        wifiManager.startScan();
    }

    private void initWifiItem(View wifiItem) {
        RecyclerView recyclerView = (RecyclerView) wifiItem.findViewById(R.id.wifi_lsitview);

        wifiAdapter = new WifiIzhaohuAdapter(activity, scanResultsIzhaoHu);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(wifiAdapter);
        wifiAdapter.setOnItemClickListener(new WifiIzhaohuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, WifiIzhaohuAdapter.ViewHolder holder, Object object) {
                connectWiFi((ScanResult) object);
            }
        });
    }

    public void connectWiFi(final ScanResult result) {
        if (inputWifiBuilder == null) {
            inputWifiBuilder = new DialogBuilder(activity);
            inputWifiItem = LayoutInflater.from(activity).inflate(R.layout.item_input_wifi_pwd, null);
            tvWifiName = (TextView) inputWifiItem.findViewById(R.id.tv_wifi_name);
            etWifiPwd = (EditText) inputWifiItem.findViewById(R.id.et_wifi_pwd);
            inputWifiItem.findViewById(R.id.tv_wifi_connect).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if ("[ESS]".equals(result.capabilities)) {
                        WifiUtils.getInstance().connectWifi(result.SSID, "", WifiUtils.WIFICIPHER_NOPASS);
                    } else {
                        final String trim = etWifiPwd.getText().toString().trim();
                        if (TextUtils.isEmpty(trim)) {
                            ToastUtil.showToast(activity, "请输入Wi-Fi密码");
                            return;
                        }
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                // t*帐号*密码#
                                sendMsg = "t*" + result.SSID + "*" + trim + "*";
                                downTimer.start();
                                WifiUtils.getInstance().connectWifi(result.SSID, trim, WIFICIPHER_WPA);
                            }
                        }.start();
                    }
                }
            });
            inputWifiItem.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputWifiBuilder.dismiss();
                }
            });
            inputWifiBuilder.replaceView(inputWifiItem).setCancelableOutSide(false);
        }
        tvWifiName.setText(result.SSID.replaceAll("\"", ""));
        etWifiPwd.setText("");
        inputWifiBuilder.show(0.875f, Gravity.CENTER);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setWifi:

                if (builder == null) {
                    builder = new DialogBuilder(activity, R.style.BottomSheet_Dialog_white_bg_Style);
                    View wifiItem = LayoutInflater.from(activity).inflate(R.layout.item_wifi_list_layout2, null);
                    initWifiItem(wifiItem);
                    builder.replaceView(wifiItem).setCancelableOutSide(false);
                } else {
                    notifyDataSetChanged();
                }

                if (!builder.isShowing()) {
                    builder.show(1, Gravity.TOP);
                } else {
                    builder.dismiss();
                    builder.show(1, Gravity.TOP);
                }
                break;
        }
    }

    private class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SCAN_RESULTS_AVAILABLE_ACTION)) {
                scanResults = (ArrayList<ScanResult>) wifiManager.getScanResults();
                loadingDialog.dismiss();
                if (inputWifiBuilder != null && inputWifiBuilder.isShowing()) {
                    inputWifiBuilder.dismiss();
                    builder.dismiss();
                }
                notifyDataSetChanged();

            } else if (intent.getAction().equals(WIFI_STATE_CHANGED_ACTION)) {
            }
        }
    }

    private void notifyDataSetChanged() {
        scanResultsIzhaoHu.clear();
        scanResultsIzhaoHu = scanResults;
//        for (ScanResult result : scanResults) {
//            String ssid = result.SSID;
//            if (ssid != null) {
//                String s = ssid.replaceAll("\"", "");
//                if (s.startsWith("izhaohu")) {
//                    scanResultsIzhaoHu.add(result);
//                }
//            }
//        }

        if (builder != null) {
            wifiAdapter.notifyAdapter(scanResultsIzhaoHu);
        }
    }


    private class WifiCountDown extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public WifiCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            addressIP = WifiUtils.getInstance().getServerIPAddress();
            if (addressIP.endsWith("1")) {
                socketClientThread = new SocketClientThread(sendMsg);
                socketClientThread.start();
            }
        }

        @Override
        public void onFinish() {
            if (client != null && !client.isClosed()) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socketClientThread != null) {
                socketClientThread.interrupt();
            }
//            if (failedFragment == null) {
//                failedFragment = new WifiConnFailedFragment();
//            }
//            ft.replace(R.id.wifi_setting_content, failedFragment).commit();
//            connPreviousWifi();
        }
    }

    private class SocketClientThread extends Thread {

        String msg;

        public SocketClientThread(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {

            try {
                addressIP = WifiUtils.getInstance().getServerIPAddress();
                client = new Socket(addressIP, 30000);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (client != null && !client.isClosed()) {
                try {
                    PrintWriter out = new PrintWriter(client.getOutputStream());
                    out.println(msg);
                    out.flush();
//                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while (!isInterrupted()) {
                if (client != null && !client.isClosed()) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        String accept = reader.readLine();
                        if (!TextUtils.isEmpty(accept) && accept.contains("ok")) {
                            close();
                        }
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    private void close() {
        if (client != null && !client.isClosed()) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (socketClientThread != null) {
            socketClientThread.interrupt();
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                timer.cancel();
                downTimer.cancel();
                // 收到完整消息即判断为设置成功
                isClientStart = true;
                connPreviousWifi();
            }
        });
    }

    private void connPreviousWifi() {
        // 同时断开基站的连接，连上先前wifi
        new Thread(new Runnable() {
            @Override
            public void run() {
                WifiUtils.getInstance().connectWifi(c_ssid, c_pwd, WIFICIPHER_WPA);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        if (wifiReceiver != null) {
            activity.unregisterReceiver(wifiReceiver);
        }
        timer.cancel();
        downTimer.cancel();
        super.onDestroy();
    }
}
