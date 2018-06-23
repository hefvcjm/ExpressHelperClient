package com.hefvcjm.expresshelper.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;


import com.hefvcjm.expresshelper.R;
import com.hefvcjm.expresshelper.net.MyHttpClient;
import com.hefvcjm.expresshelper.storage.Storage;

/**
 * 欢迎页
 */
public class WelcomeActivity extends Activity {

    private static final int WHAT_TOKEN_PASS = 0;//token通过服务器验证
    private static final int WHAT_TOKEN_FAIL = 1;//token验证出错

    private String server_url;
    private String token;
    private String phone;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_TOKEN_PASS:
                    startActivity(new Intent(WelcomeActivity.this, DrawerLayout_OneActivity.class).putExtra("phone", phone));
                    finish();
                    break;
                case WHAT_TOKEN_FAIL:
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.layout_welcome);


        token = Storage.getInstance(WelcomeActivity.this).loadToken();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    Thread.sleep(3000L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if (token != null) {
                    server_url = getResources().getString(R.string.str_server_url) + "/login/token";
                    ;
                    try {
                        new MyHttpClient(server_url
                                , new JSONObject().put("Content-Type", "application/json;charset=utf-8")
                                , new JSONObject().put("token", token), new MyHttpClient.ResponseListener() {
                            @Override
                            public void onResponse(String body, JSONObject headers) {
                                if (body == null) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = WHAT_TOKEN_FAIL;
                                    handler.sendMessage(msg);
                                } else {
                                    try {
                                        JSONObject rsp = new JSONObject(body);
                                        if (rsp.getInt("code") == 1) {
                                            String token = rsp.getString("token");
                                            if (token != null) {
                                                Storage.getInstance(WelcomeActivity.this).saveToken(token);
                                            }
                                            phone = rsp.getString("phone");
                                            Message msg = handler.obtainMessage();
                                            msg.what = WHAT_TOKEN_PASS;
                                            handler.sendMessage(msg);
                                        } else {
                                            Message msg = handler.obtainMessage();
                                            msg.what = WHAT_TOKEN_FAIL;
                                            handler.sendMessage(msg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Message msg = handler.obtainMessage();
                                        msg.what = WHAT_TOKEN_FAIL;
                                        handler.sendMessage(msg);
                                    }
                                }
                            }
                        }).post();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Message msg = handler.obtainMessage();
                        msg.what = WHAT_TOKEN_FAIL;
                        handler.sendMessage(msg);
                    }
                } else {
                    Message msg = handler.obtainMessage();
                    msg.what = WHAT_TOKEN_FAIL;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }
}
