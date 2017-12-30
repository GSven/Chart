package com.sxt.chart.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.sxt.chart.R;
import com.sxt.chart.adapter.WifiIzhaohuAdapter;
import com.sxt.chart.chart.DialogBuilder;
import com.sxt.chart.chart.LoadingDialog;
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

import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static com.sxt.chart.utils.WifiUtils.WIFICIPHER_WPA;

/**
 * Created by izhaohu on 2017/12/14.
 */

public class WifiSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private ImageView img;
    private TextView tvSetWiFi;
    private TextView tvWifiName;
    private WifiIzhaohuAdapter wifiAdapter;
    private DialogBuilder builder;
    private ArrayList<ScanResult> scanResults = new ArrayList<>();
    private ArrayList<ScanResult> scanResultsIzhaoHu = new ArrayList<>();
    private Socket client;
    private SocketClientThread socketClientThread;
    private WifiCountDown downTimer;
    private boolean isStartConnStation = false;
    private boolean isClientStart = false;
    private boolean isSuccess = false;
    private String addressIP;
    private String sendMsg;
    private String c_ssid;
    private String c_pwd;
    private ViewSwitcher viewSwitcher;
    private TextView tvSuc;
    private TextView tvResult;
    private LoadingDialog loadingDialog;
    private WifiUtils.WifiScanResult selectedScanResult;
    private WiFiConnectTimer wiFiConnectTimer;
    Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_select_wifi_layout);
        img = (ImageView) findViewById(R.id.img);
        tvWifiName = (TextView) findViewById(R.id.tv_current_wifi);
        tvSetWiFi = (TextView) findViewById(R.id.tv_setWifi);
        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        tvSuc = (TextView) findViewById(R.id.tv_suc);
        tvResult = (TextView) findViewById(R.id.tv_result);

//        activity.setTitle(R.string.select_izhaohu_wifi);
        tvSetWiFi.setOnClickListener(this);

        activity = this;
//        c_ssid = getArguments().getString(Prefs.KEY_WIFI_SSID);
//        c_pwd = getArguments().getString(Prefs.KEY_WIFI_PWD);
        c_ssid = "Qiong.Wang";
        c_pwd = "WQ987654321";
        downTimer = new WifiCountDown(30 * 1000, 6 * 1000);
        loadingDialog = new LoadingDialog(this);
        loadingDialog.show();

        initWifiSettings();
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
            registerReceiver(wifiReceiver, filter);
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
        selectedScanResult = new WifiUtils.WifiScanResult(result.BSSID, result.SSID, result.capabilities, result.level);
        startWatch();
        new Thread() {
            @Override
            public void run() {
                super.run();
                WifiUtils.getInstance().connectWifi(result.SSID, "izhaohu123", WifiUtils.WIFICIPHER_WPA);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_setWifi:

                if (isSuccess) {
                    activity.finish();
                } else {
                    //发起过认证 ,但是没成功 , 点击重新连接
                    String ssid = wifiManager.getConnectionInfo().getSSID();
                    if (ssid != null) {
                        ssid = ssid.replaceAll("\"", "");
                        if (ssid.startsWith("izhaohu")) {
                            if (builder != null) builder.dismiss();
                            loadingDialog.show();
                            //TODO t*帐号*密码#
                            sendMsg = "t*" + c_ssid.replaceAll("\"", "") + "*" + c_pwd + "#";
                            LogUtil.i("wifi", " msg = " + sendMsg);
                            downTimer.start();

                            return;
                        }
                    }

                    if (builder == null) {
                        builder = new DialogBuilder(activity, R.style.BottomSheet_Dialog_white_bg_Style);
                        View wifiItem = LayoutInflater.from(activity).inflate(R.layout.item_wifi_list_layout2, null);
                        initWifiItem(wifiItem);
                        builder.replaceView(wifiItem).setCancelableOutSide(false);
                        builder.show(1, Gravity.TOP);
                    } else {
                        builder.show(1, Gravity.TOP);
                        notifyDataSetChanged();
                    }
                }

                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SCAN_RESULTS_AVAILABLE_ACTION)) {
                scanResults = (ArrayList<ScanResult>) wifiManager.getScanResults();
                loadingDialog.dismiss();
                if (wifiManager.getConnectionInfo().getSSID() != null) {
                    tvWifiName.setText(wifiManager.getConnectionInfo().getSSID().replaceAll("\"", ""));
                }

                notifyDataSetChanged();

            }
        }
    }

    private void notifyDataSetChanged() {
        scanResultsIzhaoHu.clear();
        for (ScanResult result : scanResults) {
            String ssid = result.SSID;
            if (ssid != null) {
                String s = ssid.replaceAll("\"", "");
                if (s.startsWith("izhaohu")) {
                    scanResultsIzhaoHu.add(result);
                }
            }
        }

        if (builder != null) {
            wifiAdapter.notifyAdapter(scanResultsIzhaoHu);
        }
    }


    private class WifiCountDown extends CountDownTimer {

        public WifiCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            addressIP = WifiUtils.getInstance().getServerIPAddress();
            LogUtil.i("wifi", " ip = " + addressIP);
//            if (addressIP.endsWith("1")) {
            socketClientThread = new SocketClientThread(sendMsg);
            socketClientThread.start();

//            }
        }

        @Override
        public void onFinish() {
            if (client != null && !client.isClosed()) {
                try {
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (socketClientThread != null) {
                socketClientThread.interrupt();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isClientStart) {
                        changeResult(false);
                    }
                }
            });
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
                client = new Socket(addressIP, 5000);
                LogUtil.i("wifi", " 创建Socket =  ip = " + addressIP);
                if (client == null) {
                    LogUtil.i("wifi", "----------------- Client == null -----------------");
                } else {
                    LogUtil.i("wifi", "----------------- Client !!!! = = null -----------------");
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (client != null && !client.isClosed()) {
                try {
                    PrintWriter out = new PrintWriter(client.getOutputStream());
                    out.println(msg);
                    out.flush();
//                    out.close();
                    LogUtil.i("wifi", " flush msg = " + msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (client != null && client.isConnected()) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String accept = reader.readLine();
                    if (!TextUtils.isEmpty(accept) && accept.contains("ok")) {
                        close();
                        LogUtil.i("wifi", "成功 ok ");
                    } else {
                        LogUtil.i("wifi", "bu ok");
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                downTimer.cancel();
                // 收到完整消息即判断为设置成功
                isClientStart = true;
                connPreviousWifi();
                changeResult(true);
            }
        });
    }

    private void changeResult(boolean isSuccess) {
        loadingDialog.dismiss();
        viewSwitcher.setDisplayedChild(1);
        this.isSuccess = isSuccess;

        if (isSuccess) {
            Drawable drawable = getResources().getDrawable(R.drawable.credite_icon_suc);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvSuc.setCompoundDrawables(null, drawable, null, null);
            tvResult.setText("数据工作站已经开始努力工作啦");
            tvSuc.setText("设备连接成功");
            tvSetWiFi.setText("确定");
        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.dataset_pic_failed);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            tvSuc.setCompoundDrawables(null, drawable, null, null);
            tvResult.setText("可能是网络延迟");
            tvSuc.setText("设备连接失败");
            tvSetWiFi.setText("重新连接");
        }

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

    private void startWatch() {
        if (wiFiConnectTimer == null) {
            wiFiConnectTimer = new WiFiConnectTimer(30 * 1000, 5 * 1000);
        }
        wiFiConnectTimer.start();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.show();
            }
        });
    }

    public void stopWatch() {
        if (wiFiConnectTimer != null) {
            wiFiConnectTimer.cancel();
            wiFiConnectTimer = null;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dismissDialogs();

                if (selectedScanResult != null) {
                    if (wifiManager.getConnectionInfo().getSSID().equals(WifiUtils.getInstance().initSSID(selectedScanResult.SSID))) {
                        ToastUtil.showToast(activity, "连接成功");
                    } else {
                        ToastUtil.showToast(activity, "连接超时,请检查密码是否输入正确/或检查当前Wi-Fi是否可用");
                    }
                } else {
                    ToastUtil.showToast(activity, "连接超时,请检查密码是否输入正确/或检查当前Wi-Fi是否可用");
                }
            }
        });
    }


    private class WiFiConnectTimer extends CountDownTimer {

        public WiFiConnectTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (selectedScanResult != null && wifiManager.getConnectionInfo() != null && wifiManager.getConnectionInfo().getSSID() != null) {
                String ssid = wifiManager.getConnectionInfo().getSSID();
                if (WifiUtils.getInstance().initSSID(ssid).equals(WifiUtils.getInstance().initSSID(selectedScanResult.SSID))) {
                    stopWatch();
                }
            }
        }

        @Override
        public void onFinish() {
            stopWatch();
        }
    }

    private void dismissDialogs() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (builder != null) {
            builder.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        if (wifiReceiver != null) {
            activity.unregisterReceiver(wifiReceiver);
        }
        if (wiFiConnectTimer != null) {
            wiFiConnectTimer.cancel();
        }
        if (downTimer != null) {
            downTimer.cancel();
            downTimer = null;
        }

        super.onDestroy();
    }
}
