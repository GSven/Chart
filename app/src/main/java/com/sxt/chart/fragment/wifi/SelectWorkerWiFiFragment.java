package com.sxt.chart.fragment.wifi;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.sxt.chart.utils.LogUtil;
import com.sxt.chart.utils.Prefs;
import com.sxt.chart.utils.ToastUtil;
import com.sxt.chart.utils.WifiUtils;

import java.util.ArrayList;
import java.util.List;

import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;
import static android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION;

/**
 * Created by izhaohu on 2017/12/15.
 */
public class SelectWorkerWiFiFragment extends BaseFragment implements View.OnClickListener {

    private Activity activity;
    private ImageView imgLevel;
    private TextView tvName;
    //    private EditText tvPwd;
    private TextView tvNext;
    private Bundle bundle;
    private WifiReceiver wifiReceiver;
    private View view;
    private View wifiArrowRoot;
    private DialogBuilder builder;
    private WifiManager wifiManager = WifiUtils.getInstance().getmWifiManager();
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
    private WiFiCountDownTimer wiFiCountDownTimer;

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
        checkPermission();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.fragment_select_worker_wifi_layout, null);
            wifiArrowRoot = view.findViewById(R.id.wifi_arrow);
            imgLevel = (ImageView) view.findViewById(R.id.img_wifi_level);
            tvName = (TextView) view.findViewById(R.id.tv_wifi_name);
//            tvPwd = (EditText) view.findViewById(R.id.et_wifi_pwd);
            tvNext = (TextView) view.findViewById(R.id.tv_next);
            tvNext.setOnClickListener(this);
            wifiArrowRoot.setOnClickListener(this);
        }
        activity.setTitle(R.string.select_wifi);

        return view;
    }

    private void configWiFiSettings() {
        if (!WifiUtils.getInstance().isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);//开启WIFI
        }
        if (wifiReceiver == null) {
            wifiReceiver = new SelectWorkerWiFiFragment.WifiReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(SCAN_RESULTS_AVAILABLE_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.setPriority(1000);
            getActivity().registerReceiver(wifiReceiver, filter);
        }
        wifiManager.startScan();
    }

    @Override
    public void onResume() {
        super.onResume();
        configWiFiSettings();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            String[] permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_STATE,
                    Manifest.permission.CHANGE_WIFI_MULTICAST_STATE
            };
            /**
             *检测需要获取的权限
             */
            List<String> needRequestPermissonList = findDeniedPermissions(permissions);
            if (null != needRequestPermissonList
                    && needRequestPermissonList.size() > 0) {
                ActivityCompat.requestPermissions(activity,
                        needRequestPermissonList.toArray(
                                new String[needRequestPermissonList.size()]),
                        111);
            }

        } else {
            configWiFiSettings();
        }
    }

    /**
     * 获取权限集 中需要申请权限的列表
     *
     * @param permissions
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<String>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(activity,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    activity, perm)) {
                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 111:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    configWiFiSettings();
                    LogUtil.i("wifi", "获取到wifi权限");
                } else {
                    LogUtil.i("wifi", "没有获取到wifi权限");
                }
                break;
            default:
                break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
                builder.show(1, Gravity.TOP);
                break;

            case R.id.tv_next:
                next();
                break;
        }
    }

    private void next() {
        if (selectedScanResult == null || selectedScanResult.SSID == null || !WifiUtils.getInstance().initSSID(selectedScanResult.SSID).equals(WifiUtils.getInstance().initSSID(wifiManager.getConnectionInfo().getSSID()))) {
            ToastUtil.showToast(activity, "请确认您选择的Wi-Fi是否正常连接");
            return;
        }
        SelectIzhaohuWiFiFragment fragment = new SelectIzhaohuWiFiFragment();
        Bundle arguments = new Bundle();
        arguments.putString(Prefs.KEY_WIFI_SSID, selectedScanResult.SSID);
        arguments.putString(Prefs.KEY_WIFI_PWD, selectedScanResult.PWD);
        fragment.setArguments(arguments);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.wifi_setting_content, fragment, SelectIzhaohuWiFiFragment.class.getName())
//                .addToBackStack(SelectIzhaohuWiFiFragment.class.getName())
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

                    WifiUtils.getInstance().connectWifi(ssid, "", WifiUtils.WIFICIPHER_NOPASS);
                } else {
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
            inputWifiItem.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputWifiBuilder.dismiss();
                }
            });
            inputWifiBuilder.replaceView(inputWifiItem).setCancelableOutSide(false);
        }
        inputWifiItem.findViewById(R.id.tv_wifi_connect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("[ESS]".equals(result.capabilities)) {
                    startWatch();
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            WifiUtils.getInstance().connectWifi(result.SSID, "", WifiUtils.WIFICIPHER_NOPASS);
                        }
                    }.start();

                } else {
                    final String trim = etWifiPwd.getText().toString().trim();
                    if (TextUtils.isEmpty(trim)) {
                        ToastUtil.showToast(activity, "请输入Wi-Fi密码");
                        return;
                    }
                    startWatch();
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            if (selectedScanResult != null) {
                                selectedScanResult.PWD = trim;
                            }
                            WifiUtils.getInstance().connectWifi(result.SSID, trim, WifiUtils.WIFICIPHER_WPA);
                        }
                    }.start();
                }
            }
        });
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
                WifiUtils.WifiScanResult result = new WifiUtils.WifiScanResult(
                        config.BSSID,
                        config.SSID,
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
                if (s != null && s.SSID != null && !TextUtils.isEmpty(s.SSID)) {
                    WifiUtils.WifiScanResult result = new WifiUtils.WifiScanResult(
                            s.BSSID,
                            s.SSID,
                            s.capabilities,
                            s.level);
                    scanResultNoLinked.add(result);
                }
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
                    tvName.setText("暂未发现周边W-Fi");
                }
                notifyDataSetChanged();

            } else if (intent.getAction().equals(WIFI_STATE_CHANGED_ACTION)) {
            }

        }
    }

    private void notifyDataSetChanged() {
        dealwithLinkedWifiData();
        if (builder != null) {
            wifiAdapter.notifyAdapter(scanResultHaveLinked, scanResultNoLinked);
        }
    }

    private Handler handler = new Handler();

    private void startWatch() {
        if (wiFiCountDownTimer == null) {
            wiFiCountDownTimer = new WiFiCountDownTimer(16 * 1000, 4 * 1000);
        }
        wiFiCountDownTimer.start();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog.show();
            }
        });
    }

    public void stopWatch() {

        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (wiFiCountDownTimer != null) {
                    wiFiCountDownTimer.cancel();
                    wiFiCountDownTimer = null;
                }

                dismissDialogs();

                if (selectedScanResult != null) {
                    String ssid = wifiManager.getConnectionInfo().getSSID();
                    if (ssid != null) {
                        if (selectedScanResult.SSID.contains(ssid) || ssid.contains(selectedScanResult.SSID)) {
                            ToastUtil.showToast(activity, "连接成功");
                            tvName.setText(selectedScanResult.SSID.replaceAll("\"", ""));
//                            tvPwd.setText(selectedScanResult.PWD);
                        } else {
                            ToastUtil.showToast(activity, "连接出错,请重试");
                            selectedScanResult = null;
                        }
                    }
                } else {
                    ToastUtil.showToast(activity, "连接出错,请重新选择Wi-Fi");
                }
            }
        }, 3000);
    }


    private class WiFiCountDownTimer extends CountDownTimer {

        public WiFiCountDownTimer(long millisInFuture, long countDownInterval) {
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
        if (inputWifiBuilder != null) {
            inputWifiBuilder.dismiss();
        }
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
        if (wiFiCountDownTimer != null) {
            wiFiCountDownTimer.cancel();
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
