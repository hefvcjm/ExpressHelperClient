package com.hefvcjm.expresshelper.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.hefvcjm.expresshelper.R;
import com.hefvcjm.expresshelper.net.MyHttpClient;
import com.hefvcjm.expresshelper.storage.Storage;

import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends Activity {

    private static final String TAG = "LoginActivity";

    private static final int WHAT_LOGIN = 0;

    private String server_url;
    private TelephonyManager telephonyManager;
    private WifiManager wifi;
    private String imei;
    private String mac;
    private String phone;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_LOGIN:
                    JPushInterface.setAlias(LoginActivity.this, 0, phone);
                    startActivity(new Intent(LoginActivity.this, ExpressListActivity.class).putExtra("phone", phone));
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressLint({"WifiManagerLeak", "MissingPermission"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_login);

        server_url = getResources().getString(R.string.str_server_url);;
        Button bn_login = findViewById(R.id.bn_login);
        Button bn_send_vcode = findViewById(R.id.bn_send_vcode);
        final EditText et_phone = findViewById(R.id.et_phone);
        final EditText et_vcode = findViewById(R.id.et_vcode);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        imei = telephonyManager.getDeviceId();
        mac = wifi.getConnectionInfo().getMacAddress();


        bn_send_vcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_phone.getText().toString().length() != 11) {
                    Toast.makeText(LoginActivity.this, "手机号码不正确！", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    new MyHttpClient(server_url
                            , new JSONObject().put("type", "vcode").put("Content-Type", "application/json;charset=utf-8")
                            , new JSONObject().put("phone", et_phone.getText().toString()), new MyHttpClient.ResponseListener() {
                        @Override
                        public void onResponse(String result) {
                            if (result == null) {
                                Toast.makeText(LoginActivity.this, "发送验证码出了点问题！", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject rsp = new JSONObject(result);
                                    Toast.makeText(LoginActivity.this, rsp.getString("msg"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    System.out.println("result:" + result);
                                }
                            }
                        }
                    }).post();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        bn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_phone.getText().toString().length() != 11) {
                    Toast.makeText(LoginActivity.this, "手机号码不正确！", Toast.LENGTH_LONG).show();
                    return;
                }
                if (et_vcode.getText().toString().length() != 6) {
                    Toast.makeText(LoginActivity.this, "验证码不正确！", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    new MyHttpClient(server_url
                            , new JSONObject().put("type", "login")
                            .put("Content-Type", "application/json;charset=utf-8")
                            , new JSONObject().put("phone", et_phone.getText().toString())
                            .put("vcode", et_vcode.getText().toString())
                            .put("imei", imei)
                            .put("mac", mac)
                            , new MyHttpClient.ResponseListener() {
                        @Override
                        public void onResponse(String result) {
                            if (result == null) {
                                Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject rsp = new JSONObject(result);
                                    Toast.makeText(LoginActivity.this, rsp.getString("msg"), Toast.LENGTH_LONG).show();
                                    if (rsp.getString("msg").equals("登录成功！")) {
                                        phone = et_phone.getText().toString();
                                        Message msg = handler.obtainMessage();
                                        msg.what = WHAT_LOGIN;
                                        handler.sendMessage(msg);
                                    }
                                    String token = rsp.getString("token");
                                    if (token != null) {
                                        Storage.getInstance(LoginActivity.this).saveToken(token);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
//                                    System.out.println("result:" + result);
                                }
                            }
                        }
                    }).post();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
