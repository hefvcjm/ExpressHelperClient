package com.hefvcjm.expresshelper.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hefvcjm.expresshelper.R;
import com.hefvcjm.expresshelper.adapter.ExpressListAdapter;
import com.hefvcjm.expresshelper.express.ExpressInfos;
import com.hefvcjm.expresshelper.net.MyHttpClient;
import com.hefvcjm.expresshelper.user.UserInfos;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 通过模板生成的DrawerLayout+NavigationView布局,也是最常见的APP展示方式
 * */
public class DrawerLayout_OneActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUESTCODE_STATE_CHAGED = 1;

    private List<ExpressInfos> expressList = new ArrayList<ExpressInfos>();
    private String url;
    private String phone;
    private ExpressListAdapter adapter;

    private ListView lv_express_list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_express_list);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        url = getResources().getString(R.string.str_server_url);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        UserInfos.getInstance().setPhone(phone);
        adapter = new ExpressListAdapter(DrawerLayout_OneActivity.this, R.layout.item_list_express, expressList);
        lv_express_list = (ListView) findViewById(R.id.lv_express_list);
        lv_express_list.setAdapter(adapter);
        synchronize_express(url, phone);


        lv_express_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExpressInfos expressInfos = expressList.get(position);
                Intent intent = new Intent(DrawerLayout_OneActivity.this, ExpressDetailActivity.class);
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
                                ExpressListAdapter.ViewHolder holder = (ExpressListAdapter.ViewHolder) view.getTag();
                                holder.tv_express_state.setText(state);
                                if (state.equals(getResources().getString(R.string.str_state_receiving))) {
                                    holder.tv_express_state.setTextColor(getResources().getColor(R.color.state_receiving));
                                    holder.iv_state.setImageDrawable(getResources().getDrawable(R.drawable.receiving));
                                } else if (state.equals(getResources().getString(R.string.str_state_received))) {
                                    holder.tv_express_state.setTextColor(getResources().getColor(R.color.state_received));
                                    holder.iv_state.setImageDrawable(getResources().getDrawable(R.drawable.received));
                                } else if (state.equals(getResources().getString(R.string.str_state_refused))) {
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
                    new AsyncTask<String, Integer, List<ExpressInfos>>() {

                        @Override
                        protected List<ExpressInfos> doInBackground(String... strings) {
                            String result = strings[0];
                            List<ExpressInfos> temp = new ArrayList<ExpressInfos>();
                            try {
                                JSONObject js = new JSONObject(result);
                                int n = new Integer(js.getString("total"));
                                Log.d("test", n + "");
                                for (int i = 1; i < n + 1; i++) {
                                    JSONObject sub = new JSONObject(js.getString(i + ""));
                                    Log.d("test", sub.toString());
                                    //barcode,company,location,code,deadline,state
                                    temp.add(new ExpressInfos(sub.toString()));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            return temp;
                        }

                        @Override
                        protected void onPostExecute(List<ExpressInfos> temp) {
                            if (temp != null) {
                                expressList.addAll(temp);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }.execute(result);
                }
            }).post();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer_layout_one, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
