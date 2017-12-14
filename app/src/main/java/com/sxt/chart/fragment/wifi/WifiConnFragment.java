package com.sxt.chart.fragment.wifi;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sxt.chart.R;
import com.sxt.chart.activity.MainActivity;
import com.sxt.chart.adapter.ObserverHelper;
import com.sxt.chart.utils.Common;
import com.sxt.chart.utils.UtilsWifi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by archer on 2016/4/25.
 */
public class WifiConnFragment extends Fragment {

    public static final String KEY_CON_SSID = "KEY_CON_SSID";
    public static final String KEY_WIFI_SSID = "KEY_WIFI_SSID";
    public static final String KEY_WIFI_PWD = "KEY_WIFI_PWD";

    private String conn_ssid;
    private String c_ssid;
    private String c_pwd;

    private TextView conn_ssidText;
    private ProgressBar connPgb;

    private Socket client;
    private SocketClientThread socketClientThread;
    private Timer timer;
    private WifiCountDown downTimer;
    private boolean isStartConnStation = false;
    private boolean isClientStart = false;
    private String addressIP;
    private String sendMsg;
    private WifiConnFailedFragment failedFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conn_ssid = getArguments().getString(KEY_CON_SSID);
        c_ssid = getArguments().getString(KEY_WIFI_SSID);
        c_pwd = getArguments().getString(KEY_WIFI_PWD);
        timer = new Timer();
        downTimer = new WifiCountDown(Common.WIFI_TIME_OUT, 1000);
        ObserverHelper.getInstance().registerObserver(listener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi_connection, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        conn_ssidText = (TextView) view.findViewById(R.id.wifi_conn_ssid_text);
        connPgb = (ProgressBar) view.findViewById(R.id.wifi_conn_pgb);
        conn_ssidText.setText(conn_ssid);
        new Thread(new Runnable() {
            @Override
            public void run() {
                UtilsWifi.connectWifi(conn_ssid, Common.WIFI_SOCKET_PWD, UtilsWifi.WifiCipherType.WIFICIPHER_WPA);
            }
        }).start();
        isStartConnStation = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!isClientStart) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            connPreviousWifi();
                            Toast.makeText(getActivity(), "设置超时！", Toast.LENGTH_SHORT).show();
                            if (failedFragment == null) {
                                failedFragment = new WifiConnFailedFragment();
                            }
                            getFragmentManager().beginTransaction().replace(R.id.wifi_setting_content, failedFragment).commit();
                        }
                    });
                }
            }
        }, Common.WIFI_ACCEPT_TIME_OUT);
    }

    private ObserverHelper.ObserverListener listener = new ObserverHelper.ObserverListener() {
        @Override
        public void update(int type, Object object) {
            if (type == ObserverHelper.ACTION_WIFI) {
                if (isStartConnStation) {
                    // t*帐号*密码#
                    sendMsg = Common.SEND_START + Common.SEND_MID + c_ssid + Common.SEND_MID + c_pwd + Common.SEND_END;
                    String conSSID = (String) object;
                    if (conSSID.startsWith("\"") && conSSID.endsWith("\"")) {
                        conSSID = conSSID.substring(1, conSSID.length() - 1);
                    }
                    if (conn_ssid.equals(conSSID)) {
                        downTimer.start();
//                        new Timer().schedule(new WifiTimeTask(), Common.WIFI_TIME_OUT);
                    } else {
                        // connPreviousWifi();
//                        if (failedFragment == null) {
//                            failedFragment = new WifiConnFailedFragment();
//                        }
//                        getFragmentManager().beginTransaction().replace(R.id.wifi_setting_content, failedFragment).commit();
                    }
                }
            }
        }
    };

    private class WifiTimeTask extends TimerTask {

        @Override
        public void run() {
            UtilsWifi.init(getActivity());
            socketClientThread = new SocketClientThread(sendMsg);
            socketClientThread.start();
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
            addressIP = UtilsWifi.getServerIPAddress();
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
                addressIP = UtilsWifi.getServerIPAddress();
                client = new Socket(addressIP, Common.WIFI_SOCKET_PORT);
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
                connPgb.setVisibility(View.GONE);
                getFragmentManager().beginTransaction().replace(R.id.wifi_setting_content, new WifiConnSuccessFragment()).commit();
                connPreviousWifi();
            }
        });
    }

    private void connPreviousWifi() {
        // 同时断开基站的连接，连上先前wifi
        new Thread(new Runnable() {
            @Override
            public void run() {
                UtilsWifi.connectWifi(c_ssid, c_pwd, UtilsWifi.WifiCipherType.WIFICIPHER_WPA);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        downTimer.cancel();
        ObserverHelper.getInstance().removeObserver(listener);
    }

}
