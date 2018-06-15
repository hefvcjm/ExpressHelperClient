package com.hefvcjm.expresshelper.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.hefvcjm.expresshelper.R;
import com.hefvcjm.expresshelper.adapter.ExpressListAdapter;
import com.hefvcjm.expresshelper.express.ExpressInfos;
import com.hefvcjm.expresshelper.net.MyHttpClient;
import com.hefvcjm.expresshelper.user.UserInfos;

/**
 * Created by win10 on 2018/5/4.
 */

public class ExpressListActivity extends Activity {

    private static final int WHAT_DATASETCHANED = 0;

    public static final int REQUESTCODE_STATE_CHAGED = 1;

    private List<ExpressInfos> expressList = new ArrayList<ExpressInfos>();
    private String url;
    private String phone;
    ExpressListAdapter adapter;

    ListView lv_express_list;


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_DATASETCHANED:
                    adapter.notifyDataSetChanged();
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
        setContentView(R.layout.layout_express_list);

        url = getResources().getString(R.string.str_server_url);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        UserInfos.getInstance().setPhone(phone);
        adapter = new ExpressListAdapter(ExpressListActivity.this, R.layout.item_list_express, expressList);
        lv_express_list = findViewById(R.id.lv_express_list);
        lv_express_list.setAdapter(adapter);
        synchronize_express(url, phone);


        lv_express_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExpressInfos expressInfos = expressList.get(position);
                Intent intent = new Intent(ExpressListActivity.this, ExpressDetailActivity.class);
                intent.putExtra("express_detail", expressInfos.toString());
                intent.putExtra("position", position);
                Log.d("listview", expressInfos.toString());
                startActivityForResult(intent, REQUESTCODE_STATE_CHAGED);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_STATE_CHAGED:
                try {
                    JSONObject json = new JSONObject(data.getStringExtra("result"));
                    if ((boolean) json.get("ischanged")) {
                        int position = (int) json.get("position");
                        if (position != -1) {
                            String state = json.getString("state");
                            int visibleFirstPosition = lv_express_list.getFirstVisiblePosition();
                            int visibleLastPosition = lv_express_list.getLastVisiblePosition();
                            if (position >= visibleFirstPosition && position <= visibleLastPosition) {
                                View view = lv_express_list.getChildAt(position - visibleFirstPosition);
                                ExpressListAdapter.ViewHolder holder = (ExpressListAdapter.ViewHolder)view.getTag();
                                holder.tv_express_state.setText(state);
                                if (state.equals(getResources().getString(R.string.str_state_receiving))){
                                    holder.tv_express_state.setTextColor(getResources().getColor(R.color.state_receiving));
                                    holder.iv_state.setImageDrawable(getResources().getDrawable(R.drawable.receiving));
                                }else if (state.equals(getResources().getString(R.string.str_state_received))){
                                    holder.tv_express_state.setTextColor(getResources().getColor(R.color.state_received));
                                    holder.iv_state.setImageDrawable(getResources().getDrawable(R.drawable.received));
                                }else if (state.equals(getResources().getString(R.string.str_state_refused))){
                                    holder.tv_express_state.setTextColor(getResources().getColor(R.color.state_refused));
                                    holder.iv_state.setImageDrawable(getResources().getDrawable(R.drawable.refused));
                                }
                                ExpressInfos expressInfos = expressList.get(position);
                                expressInfos.setState(state);
                                expressList.set(position, expressInfos);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private void synchronize_express(final String url, String phone) {
        try {
            new MyHttpClient(url
                    , new JSONObject().put("type", "query_express_info").put("Content-Type", "application/json;charset=utf-8")
                    , new JSONObject().put("phone", phone)
                    , new MyHttpClient.ResponseListener() {
                @Override
                public void onResponse(String result) {
                    if (result == null) {
                        return;
                    }
                    try {
                        JSONObject js = new JSONObject(result);
                        int n = new Integer(js.getString("total"));
                        for (int i = 1; i < n + 1; i++) {
                            JSONObject sub = new JSONObject(js.getString(i + ""));
                            //barcode,company,location,code,deadline,state
                            expressList.add(new ExpressInfos(sub.toString()));
                        }
                        Message msg = handler.obtainMessage();
                        msg.what = WHAT_DATASETCHANED;
                        handler.sendMessage(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).post();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initExpressInfos() {
        int n = 10;
        for (int i = 0; i < n; i++) {
            try {
                expressList.add(new ExpressInfos(new JSONObject()
                        .put("company", "顺丰快递")
                        .put("location", "西安交通大学西十五快递助手")
                        .put("arrivetime", "2018-04-27 15:30:2")
                        .put("deadline", "2018-05-01 00:00:00")
                        .put("state", "待取货")
                        .put("code", "123456")
                        .put("barcode", "154687461687")
                        .toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
