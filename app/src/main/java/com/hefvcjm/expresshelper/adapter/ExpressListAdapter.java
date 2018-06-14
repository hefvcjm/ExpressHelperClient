package com.hefvcjm.expresshelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.hefvcjm.expresshelper.R;
import com.hefvcjm.expresshelper.express.ExpressInfos;

/**
 * Created by win10 on 2018/5/4.
 */

public class ExpressListAdapter extends ArrayAdapter<ExpressInfos> {

    private int resourceId;

    public ExpressListAdapter(Context context, int textViewResourceId, List<ExpressInfos> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ExpressInfos expressInfos = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_company_logo = view.findViewById(R.id.iv_company_logo);
            viewHolder.tv_express_code = view.findViewById(R.id.tv_express_code);
            viewHolder.tv_express_company = view.findViewById(R.id.tv_express_company);
            viewHolder.tv_express_location = view.findViewById(R.id.tv_express_location);
            viewHolder.tv_express_state = view.findViewById(R.id.tv_express_state);
            viewHolder.tv_express_time = view.findViewById(R.id.tv_express_time);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        //viewHolder.iv_company_logo;
        viewHolder.tv_express_code.setText(expressInfos.getCode());
        viewHolder.tv_express_company.setText(expressInfos.getCompany());
        viewHolder.tv_express_location.setText(expressInfos.getLocation());
        viewHolder.tv_express_state.setText(expressInfos.getState());
        String date = expressInfos.getArrivetime().split(" ")[0];
        int month = new Integer(date.split("-")[1]);
        int day = new Integer(date.split("-")[2]);
        viewHolder.tv_express_time.setText(month + "月" + day + "日");
        return view;
    }

    class ViewHolder {
        ImageView iv_company_logo;
        TextView tv_express_code;
        TextView tv_express_company;
        TextView tv_express_location;
        TextView tv_express_state;
        TextView tv_express_time;
    }
}
