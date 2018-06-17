package com.hefvcjm.expresshelper.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hefvcjm.expresshelper.express.ExpressInfos;


import java.io.File;

public class Storage {
    public static final String PATH_ROOT = "express_helper";//应用根目录
    public static final String PATH_EXPRESS_INFOS = "express_helper/express_infos";//物流信息存储路径
    public static final String PATH_USER_INFOS = "express_helper/user_infos";//用户信息路径
    private static final String PREF_PATH = "/data/data/com.hefvcjm.expresshelper/shared_prefs";//pref文件路径

    private Context context;

    private static Storage instance;

    private Storage(Context context) {
        File file = new File(PATH_ROOT);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        file = new File(PATH_EXPRESS_INFOS);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        file = new File(PATH_USER_INFOS);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        this.context = context;
    }

    public static Storage getInstance(Context context) {
        if (instance == null) {
            synchronized (Storage.class) {
                if (instance == null) {
                    instance = new Storage(context);
                }
            }
        }
        return instance;
    }

    public void savaExpressInfos(ExpressInfos expressInfos) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PATH_EXPRESS_INFOS
                + "/" + expressInfos.getBarcode(), Context.MODE_PRIVATE).edit();
        editor.putString("barcode", expressInfos.getBarcode())
                .putString("company", expressInfos.getCompany())
                .putString("location", expressInfos.getLocation())
                .putString("code", expressInfos.getCode())
                .putString("deadline", expressInfos.getDeadline())
                .putString("arrivetime", expressInfos.getArrivetime())
                .putString("pickuptime", expressInfos.getPickuptime())
                .putString("state", expressInfos.getState())
                .putString("delay", expressInfos.getDelay())
                .apply();
    }

    public ExpressInfos getExpressInfos(String barcode) {
        ExpressInfos expressInfos = null;
        File file = new File(PATH_EXPRESS_INFOS + "/" + barcode);
        if (!file.exists()) {
            return null;
        }
        SharedPreferences pref = context.getSharedPreferences(PATH_EXPRESS_INFOS
                + "/" + barcode, Context.MODE_PRIVATE);
        barcode = pref.getString("barcode", "");//条形码
        if (barcode == "") {
            return null;
        }
        String company = pref.getString("company", "");//快递公司
        String location = pref.getString("location", "");//取货地点
        String code = pref.getString("code", "");//取货码
        String deadline = pref.getString("deadline", "");//截止时间
        String arrivetime = pref.getString("arrivetime", "");//到货时间
        String pickuptime = pref.getString("pickuptime", "");//取货时间
        String state = pref.getString("state", "");//状态
        String delay = pref.getString("delay", "0");//是否延时过
        String json = "{" + "\"barcode\":\"" + barcode + "\"," + "\"company\":\"" + company
                + "\"," + "\"location\":\"" + location + "\"," + "\"code\":\"" + code + "\","
                + "\"arrivetime\":\"" + arrivetime + "\"," + "\"pickuptime\":\"" + pickuptime + "\","
                + "\"deadline\":\"" + deadline + "\"," + "\"state\":\"" + state + "\"," + "\"delay\":\"" + delay + "\"}";
        expressInfos = new ExpressInfos(json);
        return expressInfos;
    }


    public String loadToken() {
        String token;
        SharedPreferences pref = context.getSharedPreferences("token", Context.MODE_PRIVATE);
        token = pref.getString("token", null);
        return token;
    }

    public void saveToken(String token) {
        SharedPreferences.Editor editor = context.getSharedPreferences("token", Context.MODE_PRIVATE).edit();
        editor.putString("token", token);
        editor.apply();
    }

    public void clearTokenCache() {
        saveToken("");
    }

}
