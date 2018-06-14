package com.hefvcjm.expresshelper.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hefvcjm.expresshelper.R;
import com.hefvcjm.expresshelper.barcode.Barcode;
import com.hefvcjm.expresshelper.express.ExpressInfos;

/**
 * Created by win10 on 2018/5/4.
 */

public class ExpressDetailActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_detail);

        TextView tv_detail_state = findViewById(R.id.tv_detail_state);
        TextView tv_detail_pickuptime = findViewById(R.id.tv_detail_pickuptime);
        TextView tv_detail_company = findViewById(R.id.tv_detail_company);
        TextView tv_detail_location = findViewById(R.id.tv_detail_location);
        TextView tv_detail_deadline = findViewById(R.id.tv_detail_deadline);
        TextView tv_detail_code = findViewById(R.id.tv_detail_code);
        ImageView iv_barcode = findViewById(R.id.iv_barcode);

        Intent intent = getIntent();
        String data = intent.getStringExtra("express_detail");
        System.out.println("express_detail:" + data);
        ExpressInfos expressInfos = new ExpressInfos(data);
        tv_detail_state.setText(expressInfos.getState());
        tv_detail_pickuptime.setText(expressInfos.getPickuptime());
        tv_detail_company.setText(expressInfos.getCompany());
        tv_detail_location.setText(expressInfos.getLocation());
        tv_detail_deadline.setText(expressInfos.getDeadline());
        tv_detail_code.setText(expressInfos.getCode());
        Bitmap barcode_bitmap = Barcode.BarcodeFormatCode(expressInfos.getBarcode());
        if (barcode_bitmap != null) {
            iv_barcode.setImageBitmap(Barcode.BarcodeFormatCode(expressInfos.getBarcode()));
        }

    }
}
