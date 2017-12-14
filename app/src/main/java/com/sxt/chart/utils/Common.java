package com.sxt.chart.utils;

/**
 * Created by mars on 16-1-13.
 */
public class Common {
    protected static final String PROTOCOL_HTTP = "http://";
    protected static final String PROTOCOL_HTTPS = "https://";
    public static final String ICLOUD = "www.icloudcare.com/icare";
    public static final String T1 = "t1.icloudcare.com/icare";
    public static final String I_ZHAOHU = "www.i-zhaohu.com/icare";
    public static final String IZHAOHU = "www.izhaohu.com.cn/icare";
    public static final String LOCAL = "10.100.10.15/icare";
//    public static final String SERVER_URL = PROTOCOL_HTTPS + ICLOUD;
//    public static final String SERVER_URL = PROTOCOL_HTTPS + I_ZHAOHU;
    public static final String UTF_8 = "UTF-8";
    public static final int USER_ICON = 0;
    public static final int SENIOR_ICON = 2;
    public static final int STATUS_COMPLETE = 100; // 完成工单
    public static final int STATUS_EXPIRED = 200; // 过期
    public static final long ONEDAY_TIME_MILLIS = 24 * 60 * 60 * 1000l;
    // wifi设置
    public static final int WIFI_SOCKET_PORT = 30000;
    public static final String WIFI_SOCKET_PWD = "izhaohu123";
    public static final int WIFI_TIME_OUT = 15 * 1000; // 延时15s发送是否连接成功
    public static final int WIFI_ACCEPT_TIME_OUT = WIFI_TIME_OUT * 2; // 延时15s发送是否连接成功
    public static final String SEND_START = "t";
    public static final String SEND_MID = "*";
    public static final String SEND_END = "#";

    //UPDATE_USER_INFO的请求码
    public static final int BACK_CODE_MOBILE = 0;
    public static final int BACK_CODE_PASSWORD = 1;
    public static final int BACK_CODE_LASTNAME = 2;
    public static final int BACK_CODE_FIRSTNAME = 3;
    public static final int BACK_CODE_EMAIL = 4;

    public static final String USER_INFO_MOBILE = "mobile";
    public static final String USER_INFO_PASSWORD = "password";
    public static final String USER_INFO_LASTNAME = "lastname";
    public static final String USER_INFO_FIRSTNAME = "firstname";
    public static final String USER_INFO_EMAIL = "email";

    // 服务包区分
    public static final int TYPE_0 = 0; // 标准包
    public static final int TYPE_1 = 1; // 叠加包
    public static final int TYPE_2 = 2; // 押金
    public static final int TYPE_DEGREE_1 = 1; // 经济版
    public static final int TYPE_DEGREE_2 = 2; // 基础班
    public static final int TYPE_DEGREE_3 = 3; // 专业版

    public static final int MSG_SAFETY_IRRUPT = 1; // 入侵警报
    public static final int MSG_SAFETY_ACTION = 2; // 老人行为异常警报
    public static final int MSG_SAFETY_FALLS = 3; // 跌倒报警
    public static final int MSG_SAFETY_CALL_FAIL = 4; // 紧急呼叫失败
    public static final int MSG_SAFETY_OTHERS = 5; // 门忘记关等等其他安全警报

    public static final int MSG_SIGNS_PL = 1; // 舒张压
    public static final int MSG_SIGNS_PH = 2; // 收缩压
    public static final int MSG_SIGNS_HR = 3; // 心率
    public static final int MSG_SIGNS_BO = 4; // 血氧
    public static final int MSG_SIGNS_TEMP = 5; // 体温
    public static final int MSG_SIGNS_BS = 6; // 血 糖
    public static final int MSG_SIGNS_BSD = 7; // 饭后血糖
    public static final int MSG_SIGNS_CALF = 8; // 小腿围
    public static final int MSG_SIGNS_WEIGHT = 9; // 体重
    public static final int MSG_SIGNS_BR = 10; // 呼吸频率

    public static final int MSG_NOTICE_PAY = 1; // 订单支付通知
    public static final int MSG_NOTICE_PKG = 2; // 服务包到期续约通知
    public static final int MSG_NOTICE_BED_P = 3; // 床位预定通知
    public static final int MSG_NOTICE_BED_O = 4; // 床位到期通知
    public static final int MSG_NOTICE_CONTRACT = 5; // 合约类通知
}
