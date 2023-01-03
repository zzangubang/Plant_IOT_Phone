package com.example.plant_iot_phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlantListAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater mLayoutInflater = null;
    ArrayList<PlantListItem> items;

    public PlantListAdapter(Context context, ArrayList<PlantListItem> data) {
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
        View view = mLayoutInflater.inflate(R.layout.plant_list_item, viewGroup, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(items.get(position).getName());
        ImageView infoBTN = (ImageView) view.findViewById(R.id.infoBTN);
        infoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, items.get(position).getModel(), Toast.LENGTH_SHORT).show();
            }
        });
        ImageView editBTN = (ImageView) view.findViewById(R.id.editBTN);
        editBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PlantNameRevise.class);
                intent.putExtra("name", items.get(position).getName());
                intent.putExtra("model", items.get(position).getModel());
                ((Activity)mContext).startActivityForResult(intent, 1);
            }
        });

        return view;
    }
}
