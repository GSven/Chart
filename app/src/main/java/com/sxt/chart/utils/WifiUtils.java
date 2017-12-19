package com.sxt.chart.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.sxt.chart.App;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

@SuppressLint("WifiManagerLeak")
public class WifiUtils {

    private static WifiUtils WifiUtils = new WifiUtils();
    private static WifiManager mWifiManager;

    private WifiUtils() {
    }

    public static WifiUtils getInstance() {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) App.getCtx().getSystemService(Context.WIFI_SERVICE);
        }
        return WifiUtils;
    }

    public WifiManager getmWifiManager() {
        return mWifiManager;
    }

    public static final int WIFICIPHER_NOPASS = 0;
    public static final int WIFICIPHER_WEP = 1;
    public static final int WIFICIPHER_WPA = 2;

    /**
     * 判断是否连接上wifi
     *
     * @return boolean值(isConnect), 对应已连接(true)和未连接(false)
     */
    public boolean isWifiConnect() {
        NetworkInfo mNetworkInfo = ((ConnectivityManager) App.getCtx().getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mNetworkInfo.isConnected();
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public void OpenWifi() {
        if (!mWifiManager.isWifiEnabled())
            mWifiManager.setWifiEnabled(true);
    }

    public void closeWifi() {
        mWifiManager.setWifiEnabled(false);
    }

    public void addNetwork(WifiConfiguration paramWifiConfiguration) {
        int i = mWifiManager.addNetwork(paramWifiConfiguration);
        mWifiManager.enableNetwork(i, true);
    }

    public void removeNetwork(int netId) {
        if (mWifiManager != null) {
            mWifiManager.removeNetwork(netId);
//            mWifiManager.saveConfiguration();
        }
    }

    public void connectWifi(String SSID, String Password, int Type) {

        if (!isWifiEnabled()) {
            OpenWifi();
        }
        // 开启wifi需要一段时间,要等到wifi状态变成WIFI_STATE_ENABLED
        while ((mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING)) {
            try {
                // 避免程序不停循环
                Thread.currentThread();
                Thread.sleep(500);
            } catch (Exception ie) {
            }
        }

        WifiConfiguration wifiConfig = createWifiInfo(SSID, Password, Type);
        if (wifiConfig == null) {
            return;
        }
        disableAllWifi();

        int netID = wifiConfig.networkId;

        LogUtil.i("wifi", "前 netID == " + netID);
        if (netID == -1) {
            netID = mWifiManager.addNetwork(wifiConfig);
            LogUtil.i("wifi", "后 netID == " + netID);
        }
        LogUtil.i("wifi", "果 netID == " + netID);
        boolean network = mWifiManager.enableNetwork(netID, true);
        LogUtil.i("wifi", "果 network == " + network);
        if (network) {//保持WIFI配置
//            mWifiManager.saveConfiguration();
        }
        mWifiManager.reconnect();
        LogUtil.i("wifi", "走完了");
    }

    public void disconnectWifi(int paramInt) {
        mWifiManager.disableNetwork(paramInt);
    }

    public void startScan() {
        mWifiManager.startScan();
    }

    private void disableAllWifi() {
        if (mWifiManager != null) {
            for (WifiConfiguration config : mWifiManager.getConfiguredNetworks()) {
                mWifiManager.disableNetwork(config.networkId);
                mWifiManager.saveConfiguration();
            }
        }
    }

    private WifiConfiguration getConfigBySSID(String SSID) {
        SSID = initSSID(SSID);
        WifiConfiguration config = null;
        if (mWifiManager != null) {
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig == null) continue;
                if (SSID.equals(existingConfig.SSID)) {
                    config = existingConfig;
                    break;
                }
            }
        }
        return config;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = getConfigBySSID(SSID);
        if (config == null) {
            config = new WifiConfiguration();
        }
        config.SSID = initSSID(SSID);

        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();

        if (Type == WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
            config.priority = 20000;
            config.wepKeys[0] = "\"" + "\"";
        }
        if (Type == WIFICIPHER_WEP) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (Type == WIFICIPHER_WPA) {

            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;

        } else {
            return null;
        }

        LogUtil.i("wifi", " Type == " + Type);
        return config;
    }

    public String getApSSID() {
        try {
            Method localMethod = mWifiManager.getClass().getDeclaredMethod("getWifiApConfiguration", new Class[0]);
            if (localMethod == null)
                return null;
            Object localObject1 = localMethod.invoke(mWifiManager, new Object[0]);
            if (localObject1 == null)
                return null;
            WifiConfiguration localWifiConfiguration = (WifiConfiguration) localObject1;
            if (localWifiConfiguration.SSID != null)
                return localWifiConfiguration.SSID;
            Field localField1 = WifiConfiguration.class.getDeclaredField("mWifiApProfile");
            if (localField1 == null)
                return null;
            localField1.setAccessible(true);
            Object localObject2 = localField1.get(localWifiConfiguration);
            localField1.setAccessible(false);
            if (localObject2 == null)
                return null;
            Field localField2 = localObject2.getClass().getDeclaredField("SSID");
            localField2.setAccessible(true);
            Object localObject3 = localField2.get(localObject2);
            if (localObject3 == null)
                return null;
            localField2.setAccessible(false);
            String str = (String) localObject3;
            return str;
        } catch (Exception localException) {
        }
        return null;
    }

    public String getBSSID() {
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo.getBSSID();
    }

    public String getSSID() {
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo.getSSID();
    }

    public String initSSID(String SSID) {
        if (SSID != null) {
            if (SSID.startsWith("\"") && SSID.endsWith("\"")) {
                return SSID;
            }
            if (SSID.startsWith("\"")) {
                return SSID + "\"";
            }
            if (SSID.endsWith("\"")) {
                return "\"" + SSID;
            }
            return "\"" + SSID + "\"";
        }
        return null;
    }

    public String getLocalIPAddress() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return intToIp(wifiInfo.getIpAddress());
    }

    public String getServerIPAddress() {
        DhcpInfo mDhcpInfo = mWifiManager.getDhcpInfo();
        return intToIp(mDhcpInfo.gateway);
    }

    public String getBroadcastAddress() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        try {
            for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements(); ) {
                NetworkInterface ni = niEnum.nextElement();
                if (!ni.isLoopback()) {
                    for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                        if (interfaceAddress.getBroadcast() != null) {
                            //                            LogUtils.d(TAG, interfaceAddress.getBroadcast().toString().substring(1));
                            return interfaceAddress.getBroadcast().toString().substring(1);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getMacAddress() {
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo == null)
            return "NULL";
        return mWifiInfo.getMacAddress();
    }

    public int getNetworkId() {
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo == null)
            return 0;
        return mWifiInfo.getNetworkId();
    }

    public WifiInfo getWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    public List<ScanResult> getScanResults() {
        return mWifiManager.getScanResults();
    }

    // 查看以前是否也配置过这个网络
    private WifiConfiguration isExsits(String SSID) {
        if (mWifiManager == null || SSID == null) {
            return null;
        }
        SSID = initSSID(SSID);
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs == null) {
            return null;
        }
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig != null && SSID.equals(existingConfig.SSID)) {
                return existingConfig;
            }
        }
        return null;
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    /**
     * 获取网络的加密方式
     */
//    public int getSecurity(WifiConfiguration config) {
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
//            return WIFICIPHER_WPA;
//        }
//        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
//            return WIFICIPHER_WEP;
//        }
//        return (config.wepKeys[0] != null) ? WIFICIPHER_WEP : WIFICIPHER_NOPASS;
//    }
    public String getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return "[WPA]";
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return "WEP";
        }
        return (config.wepKeys[0] != null) ? "WEP" : "[ESS]";
    }

    public abstract class TimerCheck {
        private int mCount = 0;
        private int mTimeOutCount = 1;
        private int mSleepTime = 1000; // 1s
        private boolean mExitFlag = false;
        private Thread mThread = null;

        /**
         * Do not process UI work in this.
         */
        public abstract void doTimerCheckWork();

        public abstract void doTimeOutWork();

        public TimerCheck() {
            mThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (!mExitFlag) {
                        mCount++;
                        if (mCount < mTimeOutCount) {
                            doTimerCheckWork();
                            try {
                                Thread.sleep(mSleepTime);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                exit();
                            }
                        } else {
                            doTimeOutWork();
                        }
                    }
                }
            });
        }

        /**
         * start
         *
         * @param timeOutCount How many times will check?
         * @param sleepTime    ms, Every check sleep time.
         */
        public void start(int timeOutCount, int sleepTime) {
            mTimeOutCount = timeOutCount;
            mSleepTime = sleepTime;

            mThread.start();
        }

        public void exit() {
            mExitFlag = true;
        }

    }

    @SuppressLint("ParcelCreator")
    public static class WifiScanResult {

        public String BSSID;
        public String SSID;
        public String capabilities;
        public int level;
        public String PWD;

        public WifiScanResult(String BSSID, String SSID, String capabilities, int level) {
            this.BSSID = BSSID;
            this.SSID = SSID;
            this.capabilities = capabilities;
            this.level = level;
        }
    }
}
