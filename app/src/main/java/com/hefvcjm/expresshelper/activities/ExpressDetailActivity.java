package com.hefvcjm.expresshelper.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hefvcjm.expresshelper.R;
import com.hefvcjm.expresshelper.barcode.Barcode;
import com.hefvcjm.expresshelper.express.ExpressInfos;
import com.hefvcjm.expresshelper.net.MyHttpClient;
import com.hefvcjm.expresshelper.user.UserInfos;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by win10 on 2018/5/4.
 */

public class ExpressDetailActivity extends Activity {

    private static final int WHAT_STATECHANED_REFUSED = 0;
    private static final int WHAT_STATECHANED_DELAYED = 1;

    private ExpressInfos expressInfos;
    LinearLayout ll_detail_pickuptime;
    TextView tv_detail_state;
    TextView tv_detail_pickuptime;
    TextView tv_detail_company;
    TextView tv_detail_location;
    TextView tv_detail_deadline;
    TextView tv_detail_code;
    ImageView iv_state;
    ImageView iv_company;
    ImageView iv_barcode;

    Button bn_delay;
    Button bn_refuse;

    Intent result_intent;
    int position;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_STATECHANED_REFUSED:
                    tv_detail_state.setText(getResources().getString(R.string.str_state_refused));
                    tv_detail_state.setTextColor(getResources().getColor(R.color.state_refused));
                    iv_state.setImageDrawable(getResources().getDrawable(R.drawable.refused));
                    bn_refuse.setEnabled(false);
                    bn_delay.setEnabled(false);
                    bn_refuse.setBackground(getResources().getDrawable(R.drawable.bn_disable));
                    bn_delay.setBackground(getResources().getDrawable(R.drawable.bn_disable));
                    break;
                case WHAT_STATECHANED_DELAYED:
                    bn_delay.setEnabled(false);
                    bn_delay.setBackground(getResources().getDrawable(R.drawable.bn_disable));
                    String oldtime = tv_detail_deadline.getText().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        Date newdate = dateFormat.parse(oldtime);
                        Calendar c = Calendar.getInstance();
                        c.setTime(newdate);
                        c.add(Calendar.DAY_OF_MONTH, 1);// +1天
                        Date newtime = c.getTime();
                        tv_detail_deadline.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newtime));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
        setContentView(R.layout.layout_detail);

        ll_detail_pickuptime = findViewById(R.id.ll_detail_pickuptime);
        tv_detail_state = findViewById(R.id.tv_detail_state);
        tv_detail_pickuptime = findViewById(R.id.tv_detail_pickuptime);
        tv_detail_company = findViewById(R.id.tv_detail_company);
        tv_detail_location = findViewById(R.id.tv_detail_location);
        tv_detail_deadline = findViewById(R.id.tv_detail_deadline);
        tv_detail_pickuptime = findViewById(R.id.tv_detail_pickuptime);
        tv_detail_code = findViewById(R.id.tv_detail_code);
        iv_state = findViewById(R.id.iv_detail_state);
        iv_company = findViewById(R.id.iv_detail_company);
        iv_barcode = findViewById(R.id.iv_barcode);

        bn_delay = findViewById(R.id.bn_delay);
        bn_refuse = findViewById(R.id.bn_refuse);

        result_intent = new Intent();
        Intent intent = getIntent();
        String data = intent.getStringExtra("express_detail");
        position = intent.getIntExtra("position", -1);
        System.out.println("express_detail:" + data);
        expressInfos = new ExpressInfos(data);
        tv_detail_state.setText(expressInfos.getState());
        if (expressInfos.getState().equals(getResources().getString(R.string.str_state_receiving))) {
            tv_detail_state.setTextColor(getResources().getColor(R.color.state_receiving));
            iv_state.setImageDrawable(getResources().getDrawable(R.drawable.receiving));
        } else if (expressInfos.getState().equals(getResources().getString(R.string.str_state_received))) {
            tv_detail_state.setTextColor(getResources().getColor(R.color.state_received));
            iv_state.setImageDrawable(getResources().getDrawable(R.drawable.received));
        } else if (expressInfos.getState().equals(getResources().getString(R.string.str_state_refused))) {
            tv_detail_state.setTextColor(getResources().getColor(R.color.state_refused));
            iv_state.setImageDrawable(getResources().getDrawable(R.drawable.refused));
        } else if (expressInfos.getState().equals(getResources().getString(R.string.str_state_overdue))) {
            tv_detail_state.setTextColor(getResources().getColor(R.color.state_refused));
            iv_state.setImageDrawable(getResources().getDrawable(R.drawable.refused));
        }
        if (!expressInfos.getPickuptime().equals("")) {
            Log.d("test","_"+expressInfos.getPickuptime()+"|");
            ll_detail_pickuptime.setVisibility(View.VISIBLE);
            tv_detail_pickuptime.setText(expressInfos.getPickuptime());
        }
        tv_detail_company.setText(expressInfos.getCompany());
        iv_company.setImageDrawable(matchLogo(expressInfos.getCompany()));
        tv_detail_location.setText(expressInfos.getLocation());
        tv_detail_deadline.setText(expressInfos.getDeadline());
        tv_detail_code.setText(expressInfos.getCode());
        Bitmap barcode_bitmap = Barcode.BarcodeFormatCode(expressInfos.getBarcode());
        if (barcode_bitmap != null) {
            iv_barcode.setImageBitmap(Barcode.BarcodeFormatCode(expressInfos.getBarcode()));
        }

        if (expressInfos.getState().equals(getResources().getString(R.string.str_state_refused)) ||
                expressInfos.getState().equals(getResources().getString(R.string.str_state_received))) {
            bn_refuse.setEnabled(false);
            bn_delay.setEnabled(false);
            bn_refuse.setBackground(getResources().getDrawable(R.drawable.bn_disable));
            bn_delay.setBackground(getResources().getDrawable(R.drawable.bn_disable));
        }

        if (expressInfos.getDelay().equals("1")) {
            bn_delay.setEnabled(false);
            bn_delay.setBackground(getResources().getDrawable(R.drawable.bn_disable));
        }

        bn_delay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String server_url = getResources().getString(R.string.str_server_url);
                try {
                    new MyHttpClient(server_url
                            , new JSONObject().put("type", "update_express").put("Content-Type", "application/json;charset=utf-8")
                            , new JSONObject().put("barcode", expressInfos.getBarcode())
                            .put("delay", "1")
                            , new MyHttpClient.ResponseListener() {
                        @Override
                        public void onResponse(String result) {
                            if (result == null) {
                                Toast.makeText(ExpressDetailActivity.this, "发信息给服务器出了点问题！", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    JSONObject rsp = new JSONObject(result);
                                    Toast.makeText(ExpressDetailActivity.this, rsp.getString("msg"), Toast.LENGTH_LONG).show();
                                    Message msg = handler.obtainMessage();
                                    msg.what = WHAT_STATECHANED_DELAYED;
                                    handler.sendMessage(msg);
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

        bn_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(ExpressDetailActivity.this);
                final View dialogView = LayoutInflater.from(ExpressDetailActivity.this).
                        inflate(R.layout.layout_dialog_warn, null);
                dialog.setView(dialogView);
                dialog.setCancelable(false);
                final AlertDialog dlg = dialog.show();
                Button ok = dialogView.findViewById(R.id.bt_dialog_ok);
                Button cancel = dialogView.findViewById(R.id.bt_dialog_cancel);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String server_url = getResources().getString(R.string.str_server_url);
                        try {
                            new MyHttpClient(server_url
                                    , new JSONObject().put("type", "update_express").put("Content-Type", "application/json;charset=utf-8")
                                    , new JSONObject().put("barcode", expressInfos.getBarcode())
                                    .put("state", getResources().getString(R.string.str_state_refused))
                                    , new MyHttpClient.ResponseListener() {
                                @Override
                                public void onResponse(String result) {
                                    if (result == null) {
                                        Toast.makeText(ExpressDetailActivity.this, "发信息给服务器出了点问题！", Toast.LENGTH_LONG).show();
                                    } else {
                                        try {
                                            JSONObject rsp = new JSONObject(result);
                                            Toast.makeText(ExpressDetailActivity.this, rsp.getString("msg"), Toast.LENGTH_LONG).show();
                                            Message msg = handler.obtainMessage();
                                            msg.what = WHAT_STATECHANED_REFUSED;
                                            handler.sendMessage(msg);
                                            try {
                                                result_intent.putExtra("result", new JSONObject().put("ischanged", true)
                                                        .put("position", position)
                                                        .put("state", getResources().getString(R.string.str_state_refused)).toString());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
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
                        dlg.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dlg.dismiss();
                    }
                });
            }
        });

    }

    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.iv_title_logo:
                break;
            default:
                break;
        }
    }

    private Drawable matchLogo(String company) {
        Drawable drawable;
        if (company.contains("顺丰")) {
            return getResources().getDrawable(R.drawable.logo_sf);
        } else if (company.contains("申通")) {
            return getResources().getDrawable(R.drawable.logo_sto);
        } else if (company.contains("韵达")) {
            return getResources().getDrawable(R.drawable.logo_yd);
        } else if (company.contains("中通")) {
            return getResources().getDrawable(R.drawable.logo_zto);
        } else if (company.contains("圆通")) {
            return getResources().getDrawable(R.drawable.logo_yt);
        }
        return getResources().getDrawable(R.drawable.company_logo_none);
    }

    @Override
    public void onBackPressed() {
//        if (position==0){
//            finish();
//        }
        if (result_intent.hasExtra("result")) {
            setResult(DrawerLayout_OneActivity.REQUESTCODE_STATE_CHAGED, result_intent);
        } else {
            try {
                result_intent.putExtra("result", new JSONObject().put("ischanged", false).toString());
                setResult(DrawerLayout_OneActivity.REQUESTCODE_STATE_CHAGED, result_intent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onBackPressed();
    }
}
