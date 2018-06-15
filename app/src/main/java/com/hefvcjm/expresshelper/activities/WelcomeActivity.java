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
                    startActivity(new Intent(WelcomeActivity.this, ExpressListActivity.class).putExtra("phone", phone));
                    finish();
                    break;
                case WHAT_TOKEN_FAIL:
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


        token = loadToken();
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    Thread.sleep(3000L);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                if (token != null) {
                    server_url = getResources().getString(R.string.str_server_url);;
                    try {
                        new MyHttpClient(server_url
                                , new JSONObject().put("type", "token_login").put("Content-Type", "application/json;charset=utf-8")
                                , new JSONObject().put("token", token), new MyHttpClient.ResponseListener() {
                            @Override
                            public void onResponse(String result) {
                                if (result == null) {
                                    Message msg = handler.obtainMessage();
                                    msg.what = WHAT_TOKEN_FAIL;
                                    handler.sendMessage(msg);
                                } else {
                                    try {
                                        JSONObject rsp = new JSONObject(result);
                                        if (rsp.getString("msg").equals("登录成功！")) {
                                            String token = rsp.getString("token");
                                            if (token != null) {
                                                SharedPreferences.Editor editor = getSharedPreferences("token", MODE_PRIVATE).edit();
                                                editor.putString("token", token);
                                                editor.commit();
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

    private String loadToken() {
        String token;
        SharedPreferences pref = getSharedPreferences("token", MODE_PRIVATE);
        token = pref.getString("token", null);
        return token;
    }
}
