package com.hefvcjm.expresshelper.phoneinfos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by win10 on 2018/5/4.
 */
public class PhoneUtils {

    /**
     * 获取手机的ip地址
     * 注意添加权限android:name="android.permission.INTERNET"
     *
     * @return 返回地址是本地地址 例如 192.168.1.100
     */
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * 获取本机mac 记得添加权限 android:name="android.permission.ACCESS_WIFI_STATE"
     *
     * @return 返回本机的mac地址
     */
//    public static String getLocalMacAddress(Context context) {
//        WifiManager wifi = (WifiManager) context.getApplicationContext()
//                .getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = wifi.getConnectionInfo();
//        return info.getMacAddress();
//    }
//
//    /**
//     * @param ctx 上下文对象 记得添加权限android:name="android.permission.READ_PHONE_STATE"
//     * @return 返回获取到的手机信息, 返回的东西太多.可以分多个函数进行写
//     */
//    public static String getPhoneInfo(Context ctx) {
//        TelephonyManager tm = (TelephonyManager) ctx.getApplicationContext()
//                .getSystemService(Context.TELEPHONY_SERVICE);
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("\nDeviceID(IMEI)" + tm.getDeviceId());
//        sb.append("\nDeviceSoftwareVersion:" + tm.getDeviceSoftwareVersion());
//        sb.append("\ngetLine1Number:" + tm.getLine1Number());
//        sb.append("\nNetworkCountryIso:" + tm.getNetworkCountryIso());
//        sb.append("\nNetworkOperator:" + tm.getNetworkOperator());
//        sb.append("\nNetworkOperatorName:" + tm.getNetworkOperatorName());
//        sb.append("\nNetworkType:" + tm.getNetworkType());
//        sb.append("\nPhoneType:" + tm.getPhoneType());
//        sb.append("\nSimCountryIso:" + tm.getSimCountryIso());
//        sb.append("\nSimOperator:" + tm.getSimOperator());
//        sb.append("\nSimOperatorName:" + tm.getSimOperatorName());
//        sb.append("\nSimSerialNumber:" + tm.getSimSerialNumber());
//        sb.append("\ngetSimState:" + tm.getSimState());
//        sb.append("\nSubscriberId:" + tm.getSubscriberId());
//        sb.append("\nVoiceMailNumber:" + tm.getVoiceMailNumber());
//        return sb.toString();
//    }

    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialnocustom");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";

    public static String getAdresseMAC(Context context) {
        WifiManager wifiMan = (WifiManager)context.getSystemService(Context.WIFI_SERVICE) ;
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        if(wifiInf !=null && marshmallowMacAddress.equals(wifiInf.getMacAddress())){
            String result = null;
            try {
                result= getAdressMacByInterface();
                if (result != null){
                    return result;
                } else {
                    result = getAddressMacByFile(wifiMan);
                    return result;
                }
            } catch (IOException e) {
                Log.e("MobileAccess", "Erreur lecture propriete Adresse MAC");
            } catch (Exception e) {
                Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
            }
        } else{
            if (wifiInf != null && wifiInf.getMacAddress() != null) {
                return wifiInf.getMacAddress();
            } else {
                return "";
            }
        }
        return marshmallowMacAddress;
    }

    /**
     * 通过接口获取mac
     * @return
     */
    public static String getAdressMacByInterface(){
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (nif.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder res1 = new StringBuilder();
                    for (byte b : macBytes) {
                        res1.append(String.format("%02X:",b));
                    }

                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }

        } catch (Exception e) {
            Log.e("MobileAcces", "Erreur lecture propriete Adresse MAC ");
        }
        return null;
    }

    /**
     * 通过文件获取mac
     * @param wifiMan
     * @return
     * @throws Exception
     */
    public static String getAddressMacByFile(WifiManager wifiMan) throws Exception {
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = new FileInputStream(fl);
        ret = crunchifyGetStringFromStream(fin);
        fin.close();

        boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
        wifiMan.setWifiEnabled(enabled);
        return ret;
    }

    public static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {
        if (crunchifyStream != null) {
            Writer crunchifyWriter = new StringWriter();

            char[] crunchifyBuffer = new char[2048];
            try {
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
                int counter;
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);
                }
            } finally {
                crunchifyStream.close();
            }
            return crunchifyWriter.toString();
        } else {
            return "No Contents";
        }
    }

    public static String  getLocalMac() {
        String mac=null;
        String str = "";
        try
        {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str;)
            {
                str = input.readLine();
                if (str != null)
                {
                    mac = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return mac;

    }

}


