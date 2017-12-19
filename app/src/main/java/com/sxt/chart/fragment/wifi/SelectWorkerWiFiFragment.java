package com.sxt.chart.fragment.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chart.R;
import com.sxt.chart.adapter.WiFiListAdapter;
import com.sxt.chart.chart.DialogBuilder;
import com.sxt.chart.chart.LoadingDialog;
import com.sxt.chart.fragment.BaseFragment;
import com.sxt.chart.utils.Prefs;
import com.sxt.chart.utils.ToastUtil;
import com.sxt.chart.utils.WifiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION;

/**
 * Created by izhaohu on 2017/12/15.
 */

public class SelectWorkerWiFiFragment extends BaseFragment implements View.OnClickListener {

    private Activity activity;
    private ImageView imgLevel;
    private TextView tvName;
    private EditText etPwd;
    private TextView tvNext;
    private Bundle bundle;
    private WifiManager wifiManager;
    private WifiReceiver wifiReceiver;
    private View view;
    private View wifiArrowRoot;
    private DialogBuilder builder;

    private ExpandableListView expandableListView;
    private ArrayList<ScanResult> scanResults = new ArrayList<>();
    private ArrayList<WifiUtils.WifiScanResult> scanResultHaveLinked = new ArrayList<>();
    private ArrayList<WifiUtils.WifiScanResult> scanResultNoLinked = new ArrayList<>();
    private WifiUtils.WifiScanResult selectedScanResult;
    private WiFiListAdapter wifiAdapter;
    private View inputWifiItem;
    private DialogBuilder inputWifiBuilder;
    private TextView tvWifiName;
    private EditText etWifiPwd;
    private LoadingDialog loadingDialog;
    private Timer timer;
    private TimerTask timerTask;
    private long time = 8 * 1000;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
        loadingDialog = new LoadingDialog(activity);
        initWifiSettings();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.fragment_select_worker_wifi_layout, null);
            wifiArrowRoot = view.findViewById(R.id.wifi_arrow);
            imgLevel = (ImageView) view.findViewById(R.id.img_wifi_level);
            tvName = (TextView) view.findViewById(R.id.tv_wifi_name);
            etPwd = (EditText) view.findViewById(R.id.et_wifi_pwd);
            tvNext = (TextView) view.findViewById(R.id.tv_next);
            tvNext.setOnClickListener(this);
            wifiArrowRoot.setOnClickListener(this);
        }
        activity.setTitle(R.string.select_wifi);

        return view;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.wifi_arrow://选择WIFI
                if (builder == null) {
                    builder = new DialogBuilder(activity, R.style.BottomSheet_Dialog_white_bg_Style);
                    View wifiItem = LayoutInflater.from(activity).inflate(R.layout.item_wifi_list_layout, null);
                    initWifiItem(wifiItem);
                    builder.replaceView(wifiItem).setCancelableOutSide(false);
                }
                wifiManager.startScan();
                builder.show(1, Gravity.TOP);

                break;

            case R.id.tv_next:
                next();
                break;
        }
    }

    private void next() {

//        if (TextUtils.isEmpty(etPwd.getText()) && !selectedScanResult.capabilities.equals("[ESS]")) {
//            ToastUtil.showToast(activity, "请输入Wi-Fi密码");
//            return;
//        }
//
//        if ("[ESS]".equals(selectedScanResult.capabilities)) {
//            WifiUtils.getInstance().connectWifi(tvName.getText().toString(), etPwd.getText().toString().trim(), WifiUtils.WIFICIPHER_NOPASS);
//        } else {
//            WifiUtils.getInstance().connectWifi(tvName.getText().toString(), etPwd.getText().toString().trim(), WifiUtils.WIFICIPHER_WPA);
//        }

        SelectIzhaohuWiFiFragment fragment = new SelectIzhaohuWiFiFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Prefs.KEY_WIFI_SSID, currentLinked.SSID);
        arguments.putString(Prefs.KEY_WIFI_PWD, currentLinked.PWD);
        fragment.setArguments(arguments);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.wifi_setting_content, fragment, SelectIzhaohuWiFiFragment.class.getName())
                .addToBackStack(SelectIzhaohuWiFiFragment.class.getName())
                .commit();
    }

    private void initWifiItem(View wifiItem) {
        expandableListView = (ExpandableListView) wifiItem.findViewById(R.id.wifi_lsitview);
        expandableListView.setGroupIndicator(null);//隐藏系统自带的箭头
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;//屏蔽 分组点击展开
            }
        });
        dealwithLinkedWifiData();
        wifiAdapter = new WiFiListAdapter(activity, scanResultHaveLinked, scanResultNoLinked);
        expandableListView.setAdapter(wifiAdapter);
        for (int i = 0; i < wifiAdapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);//将全部分组展开
        }
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                selectedScanResult = (WifiUtils.WifiScanResult) wifiAdapter.getChild(groupPosition, childPosition);
                String ssid = selectedScanResult.SSID.replaceAll("\"", "");
                if ("[ESS]".equals(selectedScanResult.capabilities)) {
                    etPwd.setEnabled(false);
                    etPwd.setHint("该Wi-Fi未加密");
                    WifiUtils.getInstance().connectWifi(ssid, "", WifiUtils.WIFICIPHER_NOPASS);
                } else {
                    etPwd.setEnabled(true);
                    etPwd.setHint(R.string.input_wifi_pwd);
                    connectWiFi(selectedScanResult);
                }
                tvName.setText(ssid);
                return false;
            }
        });
    }

    public void connectWiFi(final WifiUtils.WifiScanResult result) {
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
                                startWatch();
                                WifiUtils.getInstance().connectWifi(result.SSID, trim, WifiUtils.WIFICIPHER_WPA);
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

    private WifiUtils.WifiScanResult currentLinked;

    private void dealwithLinkedWifiData() {
        scanResultHaveLinked.clear();
        scanResultNoLinked.clear();
        List<WifiConfiguration> networks = wifiManager.getConfiguredNetworks();
        if (networks != null) {
            for (WifiConfiguration config : networks) {
                if (config == null || config.SSID == null || config.SSID.length() == 0) {
                    continue;
                }
                WifiUtils.WifiScanResult result = new WifiUtils.WifiScanResult(
                        config.BSSID,
                        WifiUtils.getInstance().initSSID(config.SSID),
                        WifiUtils.getInstance().getSecurity(config),
                        0);
                if (wifiManager.getConnectionInfo().getSSID().equals(config.SSID)) {
                    currentLinked = result;
                }
                scanResultHaveLinked.add(result);
            }
        }

        if (scanResultHaveLinked.contains(currentLinked)) {
            scanResultHaveLinked.remove(currentLinked);
            scanResultHaveLinked.add(0, currentLinked);
        }

        if (scanResults != null) {
            for (ScanResult s : scanResults) {
                if (s == null || s.SSID == null || s.SSID.length() == 0) {
                    continue;
                }
                WifiUtils.WifiScanResult result = new WifiUtils.WifiScanResult(
                        s.BSSID,
                        WifiUtils.getInstance().initSSID(s.SSID),
                        s.capabilities,
                        s.level);
                scanResultNoLinked.add(result);
            }
        }
    }

    private class WifiReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SCAN_RESULTS_AVAILABLE_ACTION)) {
                scanResults = (ArrayList<ScanResult>) wifiManager.getScanResults();
                if (scanResults != null && scanResults.size() > 0) {
                    wifiArrowRoot.setEnabled(true);
                    tvName.setText(wifiManager.getConnectionInfo().getSSID().replaceAll("\"", ""));
                } else {
                    wifiArrowRoot.setEnabled(false);
                    tvName.setText("暂未发现周边W-Fi,请确保Wi-Fi开关已打开");
                }
                notifyDataSetChanged();

            } else if (intent.getAction().equals(WIFI_STATE_CHANGED_ACTION)) {
                ToastUtil.showToast(activity, "change");
            }

        }
    }

    private void notifyDataSetChanged() {
        dealwithLinkedWifiData();
        if (builder != null) {
            wifiAdapter.notifyAdapter(scanResultHaveLinked, scanResultNoLinked);
        }
    }

    private void startWatch() {
        if (timerTask != null) timerTask.cancel();
        if (timer == null) {
            timer = new Timer();
        }
        timerTask = new Task();
        timer.schedule(timerTask, time);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.show();
            }
        });
    }

    public void stopWatch() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (inputWifiBuilder != null) {
                    inputWifiBuilder.dismiss();
                }
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }

                if (selectedScanResult != null) {
                    if (WifiUtils.getInstance().initSSID(wifiManager.getConnectionInfo().getSSID()).equals(WifiUtils.getInstance().initSSID(selectedScanResult.SSID))) {
                        ToastUtil.showToast(activity, "连接成功");
                    } else {
                        ToastUtil.showToast(activity, "连接超时,请检查密码是否输入正确");
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if (wifiReceiver != null) {
            activity.unregisterReceiver(wifiReceiver);
        }
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    private class Task extends TimerTask {
        @Override
        public void run() {
            stopWatch();
        }
    }
}
