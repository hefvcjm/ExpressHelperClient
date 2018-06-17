package com.hefvcjm.expresshelper.activities.nav_activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hefvcjm.expresshelper.R;

public class SettingActivity extends Activity {

    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_nav_setting);
        title = findViewById(R.id.tv_nav_title);
        title.setText("设置");
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
