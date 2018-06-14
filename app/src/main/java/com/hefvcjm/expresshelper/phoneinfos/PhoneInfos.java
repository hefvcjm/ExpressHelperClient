package com.hefvcjm.expresshelper.phoneinfos;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 获取手机信息
 */
public class PhoneInfos extends Activity {

    private String serialNumber;//序列号
    private String androidd;//android_id
    private String phoneNumber;//手机号码
    private String simSerialNumber;//手机卡序列号
    private String imsi;//IMSI
    private String simCountry;//手机卡国家
    private String simOperator;//运营商
    private String simOperatorName;//运营商名字
    private String networkCountryIso;//国家Iso代号
    private String networkOperator;//网络运营商类型
    private String networkOperatorName;//网络运营商名字
    private String networkType;//网络类型
    private String phoneType;//手机类型
    private String simState;//手机卡状态
    private String macAddress;//MAC地址
    private String bluetoothName;//蓝牙名称
    private String deviceSoftwareVersion;//系统版本
    private String cpuInfo;//CPU型号
    private String radioVersion;//固件版本

    private String release;//系统版本
    private String sdk;//系统版本值
    private String brand;//手机品牌
    private String model;//手机型号
    private String id;//设备版本ID
    private String displayId;//设备显示的版本ID
    private String productName;//产品名称
    private String manuFacturer;//设备制造商
    private String devide;//设备驱动名
    private String hardware;//硬件名
    private String fingerprint;//指纹信息
    private String serial;//串口序列号
    private String type;//本版类型
    private String tags;//设备标签
    private String host;//设备主机地址
    private String user;//设备用户
    private String codename;//固件开发版本号
    private String biuldIncremental;//源码控制版本号
    private String board;//主板名称
    private String bootloader;//主板引导程序版本号
    private String time;//build时间
    private String sdkInt;//系统的API级别
    private String cpuABI;//CPU指令集名称
    private String cpuABI2;//CPU指令集2

    //
    private String bluetoothAddress;//蓝牙MAC地址
    private String wifiName;//WiFi名称
    private String ipAddress;//ip地址

    //屏幕信息
    private float density;//屏幕密度（像素比例：0.75/1.0/1.5/2.0）
    private int densityDpi;//屏幕密度（每寸像素：120/160/240/320）
    private int width;//手机内置分辨率
    private int height;//手机内置分辨率
    private int xdpi;//屏幕x方向每英寸像素点数
    private int ydpi;//屏幕y方向每英寸像素点数
    private float scaledDensity;//字体缩放比例

    private TelephonyManager phone;
    private WifiManager wifi;
    private Display display;
    private DisplayMetrics metrics;
    private NetworkInfo networkInfo;

    private boolean isInit = false;

    @SuppressLint("WifiManagerLeak")
    public void init() {
        phone = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        display = getWindowManager().getDefaultDisplay();
        metrics = getResources().getDisplayMetrics();
        DisplayMetrics book = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(book);
        //获取网络连接管理者
        ConnectivityManager connectionManager = (ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        //获取网络的状态信息，有下面三种方式
        networkInfo = connectionManager.getActiveNetworkInfo();
        isInit = true;
    }

}
