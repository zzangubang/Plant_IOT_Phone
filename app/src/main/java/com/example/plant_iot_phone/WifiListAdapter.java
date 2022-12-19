package com.example.plant_iot_phone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class WifiListAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<WifiListItem> items;

    public WifiListAdapter(Context context, ArrayList<WifiListItem> data) {
        mContext = context;
        items = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() { return items.size(); }
    @Override
    public Object getItem(int position) { return  items.get(position); }
    @Override
    public long getItemId(int position) { return position; }
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = mLayoutInflater.inflate(R.layout.wifi_list_item, null);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(items.get(position).getName());

        return view;
    }
}
