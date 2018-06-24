package com.hefvcjm.expresshelper.activities.nav_activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hefvcjm.expresshelper.R;
import com.hefvcjm.expresshelper.net.MyHttpClient;
import com.hefvcjm.expresshelper.staticinfos.StaticInfos;

import org.json.JSONException;
import org.json.JSONObject;


public class MeActivity extends Activity {

    private TextView tv_title;
    private TextView tv_edit;

    private TextView tv_phone;
    private EditText et_name;
    private EditText et_email;
    private EditText et_sex;
    private EditText et_birth;
    private EditText et_address;

    private String phone;
    private String url;

    private boolean isEditable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_nav_me);

        url = getResources().getString(R.string.str_server_url);
        phone = StaticInfos.getPhone();

        tv_title = findViewById(R.id.tv_nav_title);
        tv_title.setText("我的");

        tv_edit = findViewById(R.id.tv_nav_edit);
        tv_edit.setVisibility(View.VISIBLE);

        tv_phone = findViewById(R.id.nav_me_phone);
        et_name = findViewById(R.id.nav_me_name);
        et_email = findViewById(R.id.nav_me_email);
        et_sex = findViewById(R.id.nav_me_sex);
        et_birth = findViewById(R.id.nav_me_birthday);
        et_address = findViewById(R.id.nav_me_address);

        tv_phone.setText(phone);

        init();

        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEditable) {
                    et_name.setFocusable(true);
                    et_name.setFocusableInTouchMode(true);
                    et_name.setClickable(true);

                    et_email.setFocusable(true);
                    et_email.setFocusableInTouchMode(true);
                    et_email.setClickable(true);

                    et_address.setFocusable(true);
                    et_address.setFocusableInTouchMode(true);
                    et_address.setClickable(true);

                    et_sex.setFocusable(true);
                    et_sex.setFocusableInTouchMode(true);
                    et_sex.setClickable(true);

                    et_birth.setFocusable(true);
                    et_birth.setFocusableInTouchMode(true);
                    et_birth.setClickable(true);

                    isEditable = true;
                    tv_edit.setText("保存");
                } else {
                    et_name.setFocusable(false);
                    et_name.setFocusableInTouchMode(false);
                    et_name.setClickable(false);

                    et_email.setFocusable(false);
                    et_email.setFocusableInTouchMode(false);
                    et_email.setClickable(false);

                    et_address.setFocusable(false);
                    et_address.setFocusableInTouchMode(false);
                    et_address.setClickable(false);

                    et_sex.setFocusable(false);
                    et_sex.setFocusableInTouchMode(false);
                    et_sex.setClickable(false);

                    et_birth.setFocusable(false);
                    et_birth.setFocusableInTouchMode(false);
                    et_birth.setClickable(false);

                    try {
                        new MyHttpClient(url + "/update/user"
                                , new JSONObject().put("Content-Type", "application/json;charset=utf-8")
                                , new JSONObject().put("phone", phone)
                                .put("name", et_name.getText().toString())
                                .put("email", et_email.getText().toString())
                                .put("address", et_address.getText().toString())
                                .put("birth", et_birth.getText().toString())
                                .put("sex", et_sex.getText().toString().equals("男") ? "1" : "0")
                                , new MyHttpClient.ResponseListener() {
                            @Override
                            public void onResponse(String body, JSONObject headers) {
                                if (body == null) {
                                    return;
                                }
                            }
                        }).post();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    isEditable = false;
                    tv_edit.setText("编辑");
                }
            }
        });

    }

    private void init() {
        try {
            new MyHttpClient(url + "/query/user"
                    , new JSONObject().put("Content-Type", "application/json;charset=utf-8")
                    , new JSONObject().put("phone", phone)
                    , new MyHttpClient.ResponseListener() {
                @Override
                public void onResponse(String body, JSONObject headers) {
                    if (body == null) {
                        return;
                    }
                    new AsyncTask<String, Integer, JSONObject>() {

                        @Override
                        protected JSONObject doInBackground(String... strings) {
                            try {
                                JSONObject rsp = new JSONObject(strings[0]);
                                if (rsp.getInt("code") == 1) {
                                    return new JSONObject(rsp.getString("data"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(JSONObject json) {
                            if (json != null) {
                                try {
                                    et_name.setText(json.getString("name"));
                                    et_address.setText(json.getString("address"));
                                    et_birth.setText(json.getString("birth"));
                                    et_email.setText(json.getString("email"));
                                    if (json.getString("sex").equals("1")) {
                                        et_sex.setText("男");
                                    } else {
                                        et_sex.setText("女");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.execute(body);
                }
            }).post();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
