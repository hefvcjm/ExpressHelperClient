package com.hefvcjm.expresshelper.activities.nav_activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hefvcjm.expresshelper.R;
import com.hefvcjm.expresshelper.activities.DrawerLayout_OneActivity;
import com.hefvcjm.expresshelper.activities.LoginActivity;
import com.hefvcjm.expresshelper.net.MyHttpClient;
import com.hefvcjm.expresshelper.staticinfos.StaticInfos;
import com.hefvcjm.expresshelper.storage.Storage;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingActivity extends Activity {

    private TextView title;

    private RelativeLayout rl_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_nav_setting);
        title = findViewById(R.id.tv_nav_title);
        title.setText("设置");

        rl_logout = findViewById(R.id.nav_setting_rl_logout);
        rl_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    private void logout() {
        Storage.getInstance(SettingActivity.this).clearTokenCache();
        String url_logout = getResources().getString(R.string.str_server_url) + "/logout";
        try {
            new MyHttpClient(url_logout
                    , new JSONObject().put("Content-Type", "application/json;charset=utf-8")
                    , new JSONObject().put("phone", StaticInfos.getPhone())
                    , new MyHttpClient.ResponseListener() {
                @Override
                public void onResponse(String body, JSONObject headers) {

                }
            }).post();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(SettingActivity.this, LoginActivity.class));
        finish();
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
