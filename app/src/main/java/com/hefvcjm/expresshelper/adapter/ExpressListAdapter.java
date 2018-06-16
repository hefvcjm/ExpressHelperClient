package com.hefvcjm.expresshelper.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
    private Context context;

    public ExpressListAdapter(Context context, int textViewResourceId, List<ExpressInfos> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        this.context = context;
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
            viewHolder.iv_state = view.findViewById(R.id.iv_state_img);
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
        viewHolder.tv_express_code.setText(expressInfos.getCode());
        viewHolder.tv_express_company.setText(expressInfos.getCompany());
        viewHolder.tv_express_location.setText(expressInfos.getLocation());
        viewHolder.tv_express_state.setText(expressInfos.getState());
        viewHolder.iv_company_logo.setImageDrawable(matchLogo(expressInfos.getCompany()));
        if (expressInfos.getState().equals(context.getResources().getString(R.string.str_state_receiving))) {
            viewHolder.tv_express_state.setTextColor(context.getResources().getColor(R.color.state_receiving));
            viewHolder.iv_state.setImageDrawable(context.getResources().getDrawable(R.drawable.receiving));
        } else if (expressInfos.getState().equals(context.getResources().getString(R.string.str_state_received))) {
            viewHolder.tv_express_state.setTextColor(context.getResources().getColor(R.color.state_received));
            viewHolder.iv_state.setImageDrawable(context.getResources().getDrawable(R.drawable.received));
        } else if (expressInfos.getState().equals(context.getResources().getString(R.string.str_state_refused))) {
            viewHolder.tv_express_state.setTextColor(context.getResources().getColor(R.color.state_refused));
            viewHolder.iv_state.setImageDrawable(context.getResources().getDrawable(R.drawable.refused));
        } else if (expressInfos.getState().equals(context.getResources().getString(R.string.str_state_overdue))) {
            viewHolder.tv_express_state.setTextColor(context.getResources().getColor(R.color.state_refused));
            viewHolder.iv_state.setImageDrawable(context.getResources().getDrawable(R.drawable.refused));
        }
        if (expressInfos.getArrivetime() != "") {
            String date = expressInfos.getArrivetime().split(" ")[0];
            int month = new Integer(date.split("-")[1]);
            int day = new Integer(date.split("-")[2]);
            viewHolder.tv_express_time.setText(month + "月" + day + "日");
        }
        return view;
    }

    private Drawable matchLogo(String company) {
        Drawable drawable;
        if (company.contains("顺丰")) {
            return context.getResources().getDrawable(R.drawable.logo_sf);
        } else if (company.contains("申通")) {
            return context.getResources().getDrawable(R.drawable.logo_sto);
        } else if (company.contains("韵达")) {
            return context.getResources().getDrawable(R.drawable.logo_yd);
        } else if (company.contains("中通")) {
            return context.getResources().getDrawable(R.drawable.logo_zto);
        } else if (company.contains("圆通")) {
            return context.getResources().getDrawable(R.drawable.logo_yt);
        }
        return context.getResources().getDrawable(R.drawable.company_logo_none);
    }


    public static class ViewHolder {
        public ImageView iv_company_logo;
        public ImageView iv_state;
        public TextView tv_express_code;
        public TextView tv_express_company;
        public TextView tv_express_location;
        public TextView tv_express_state;
        public TextView tv_express_time;
    }
}
