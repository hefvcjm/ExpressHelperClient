package com.hefvcjm.expresshelper.activities.nav_activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hefvcjm.expresshelper.R;
import com.hefvcjm.expresshelper.activities.LoginActivity;
import com.hefvcjm.expresshelper.net.MyHttpClient;
import com.hefvcjm.expresshelper.staticinfos.StaticInfos;

import org.json.JSONException;
import org.json.JSONObject;

public class SecurityActivity extends Activity {

    private static final int WHAT_MODIFY_OK = 1;//修改密码成功

    private TextView title;
    private LinearLayout ll_modify_pw;
    private LinearLayout ll_modify_pw_view;

    private EditText et_old_pw;
    private EditText et_new_pw;
    private EditText et_confirm_pw;
    private Button bn_ok;

    private String phone;
    private String url;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case WHAT_MODIFY_OK:
                    ll_modify_pw_view.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_nav_security);
        title = findViewById(R.id.tv_nav_title);
        title.setText("安全");

        url = getResources().getString(R.string.str_server_url);
        phone = StaticInfos.getPhone();

        ll_modify_pw_view = findViewById(R.id.nav_security_modify_pw_view);
        et_old_pw = findViewById(R.id.nav_security_old_pw);
        et_new_pw = findViewById(R.id.nav_security_new_pw);
        et_confirm_pw = findViewById(R.id.nav_security_confirm_pw);
        bn_ok = findViewById(R.id.nav_security_ok_modify_pw);

        ll_modify_pw = findViewById(R.id.nav_security_modify_pw);
        ll_modify_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_modify_pw_view.setVisibility(View.VISIBLE);
            }
        });

        bn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int length = et_new_pw.getText().toString().length();
                if (length < 6 || length > 16) {
                    Toast.makeText(SecurityActivity.this, "密码长度小于6或者大于16", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!et_new_pw.getText().toString().equals(et_confirm_pw.getText().toString())) {
                    Toast.makeText(SecurityActivity.this, "两次输入的新密码不一致", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    new MyHttpClient(url + "/modify/password"
                            , new JSONObject().put("Content-Type", "application/json;charset=utf-8")
                            , new JSONObject().put("phone", phone)
                            .put("old", et_old_pw.getText().toString())
                            .put("new", et_new_pw.getText().toString())
                            , new MyHttpClient.ResponseListener() {
                        @Override
                        public void onResponse(String body, JSONObject headers) {
                            if (body == null) {
                                Toast.makeText(SecurityActivity.this, "修改密码出现异常", Toast.LENGTH_LONG).show();
                                return;
                            }
                            try {
                                JSONObject rsp = new JSONObject(body);
                                if (rsp.getInt("code") == 1) {
                                    if (rsp.getInt("modify") == 1) {
                                        Message msg = handler.obtainMessage();
                                        msg.what = WHAT_MODIFY_OK;
                                        handler.sendMessage(msg);
                                    }
                                }
                                Toast.makeText(SecurityActivity.this, rsp.getString("msg"), Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).post();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.bn_back:
                finish();
                break;
            default:
                break;
        }
    }
}
