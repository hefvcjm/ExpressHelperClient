package com.hefvcjm.expresshelper.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.hefvcjm.expresshelper.R;
import com.hefvcjm.expresshelper.net.MyHttpClient;
import com.hefvcjm.expresshelper.storage.Storage;

import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private String cookies;

    private Button bn_login;
    private Button bn_send_vcode;
    private TextView tv_change_login_way;
    private EditText et_phone;
    private EditText et_vcode;

    private boolean isPasswordMode = false;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_LOGIN:
                    JPushInterface.setAlias(LoginActivity.this, 0, phone);
                    startActivity(new Intent(LoginActivity.this, DrawerLayout_OneActivity.class).putExtra("phone", phone));
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

        server_url = getResources().getString(R.string.str_server_url);
        bn_login = findViewById(R.id.bn_login);
        bn_send_vcode = findViewById(R.id.bn_send_vcode);
        tv_change_login_way = findViewById(R.id.tv_change_login_way);
        tv_change_login_way.setText(Html.fromHtml("<p><u>密码登录</u></p>"));

        et_phone = findViewById(R.id.et_phone);
        et_vcode = findViewById(R.id.et_vcode);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        imei = telephonyManager.getDeviceId();
        mac = wifi.getConnectionInfo().getMacAddress();


        tv_change_login_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPasswordMode = !isPasswordMode;
                if (isPasswordMode) {
                    tv_change_login_way.setText(Html.fromHtml("<p><u>验证码登录</u></p>"));
                    bn_send_vcode.setVisibility(View.GONE);
                    et_vcode.setHint("密码");
                    et_vcode.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    et_vcode.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    tv_change_login_way.setText(Html.fromHtml("<p><u>密码登录</u></p>"));
                    bn_send_vcode.setVisibility(View.VISIBLE);
                    et_vcode.setHint("验证码");
                    et_vcode.setInputType(InputType.TYPE_CLASS_NUMBER);
                    et_vcode.setText("");
                    et_vcode.setTransformationMethod(null);
                }
            }
        });


        bn_send_vcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_phone.getText().toString().length() != 11) {
                    Toast.makeText(LoginActivity.this, "手机号码不正确！", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    new MyHttpClient(server_url + "/vcode"
                            , new JSONObject().put("Content-Type", "application/json;charset=utf-8")
                            , new JSONObject().put("phone", et_phone.getText().toString()), new MyHttpClient.ResponseListener() {
                        @Override
                        public void onResponse(String body, JSONObject headers) {
                            if (body == null) {
                                Toast.makeText(LoginActivity.this, "发送验证码出了点问题！", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject rsp = new JSONObject(body);
                                    if (headers != null) {
                                        List<String> cookiesHeader = (List<String>) headers.get("Set-Cookie");
                                        if (cookiesHeader != null) {
                                            CookieManager cookieManager = new CookieManager();
                                            for (String cookie : cookiesHeader) {
                                                cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                                            }
                                            if (cookieManager.getCookieStore().getCookies().size() > 0) {
                                                cookies = TextUtils.join(";", cookieManager.getCookieStore().getCookies());
                                            }
                                        }
                                    }
                                    Toast.makeText(LoginActivity.this, rsp.getString("msg"), Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    System.out.println("result:" + body);
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
                String login_url = null;
                JSONObject headers = new JSONObject();
                JSONObject body = new JSONObject();
                if (isPasswordMode) {
                    if (et_vcode.getText().toString().length() < 6 || et_vcode.getText().toString().length() > 16) {
                        Toast.makeText(LoginActivity.this, "验证码不正确！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    login_url = server_url + "/login";
                    try {
                        headers.put("Content-Type", "application/json;charset=utf-8");
                        body.put("phone", et_phone.getText().toString())
                                .put("password", et_vcode.getText().toString())
                                .put("imei", imei)
                                .put("mac", mac);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (et_vcode.getText().toString().length() != 6) {
                        Toast.makeText(LoginActivity.this, "验证码不正确！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    login_url = server_url + "/login/vcode";
                    try {
                        headers.put("Content-Type", "application/json;charset=utf-8").put("Cookie", cookies);
                        body.put("phone", et_phone.getText().toString())
                                .put("code", et_vcode.getText().toString())
                                .put("imei", imei)
                                .put("mac", mac);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                new MyHttpClient(login_url
                        , headers
                        , body
                        , new MyHttpClient.ResponseListener() {
                    @Override
                    public void onResponse(String body, JSONObject headers) {
                        if (body == null) {
                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                JSONObject rsp = new JSONObject(body);
                                Toast.makeText(LoginActivity.this, rsp.getString("msg"), Toast.LENGTH_LONG).show();
                                if (rsp.getInt("code") == 1) {
                                    phone = et_phone.getText().toString();
                                    Message msg = handler.obtainMessage();
                                    msg.what = WHAT_LOGIN;
                                    handler.sendMessage(msg);
                                    String token = rsp.getString("token");
                                    if (token != null) {
                                        Storage.getInstance(LoginActivity.this).saveToken(token);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
//                                    System.out.println("result:" + result);
                            }
                        }
                    }
                }).post();
            }
        });

    }
}
