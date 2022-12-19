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

public class PlantListAdapter extends RecyclerView.Adapter<PlantListAdapter.ItemViewHolder> implements PlantItemTouchHelperCallback.ItemTouchHelperListener {

    private ArrayList<PlantListItem> listData;
    private OnItemClickEventListener mItemClickListener;
    private Context context;

    // SharedPrefrences.
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    ArrayList<String> getModel, getName, getNoti;
    JSONArray modelJSON, nameJSON, notiJSON;

    Toast toast;

    public PlantListAdapter(ArrayList<PlantListItem> listData) {
        this.listData = listData;
    }

    public interface OnItemClickEventListener {
        void onItemClick(View view, int position);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plant_list_item, parent, false);
        getNameModel(view);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.name.setText(listData.get(position).getName());
        holder.information.setBackgroundResource(R.drawable.home_info);
        holder.revise.setBackgroundResource(R.drawable.home_edit);
    }

    @Override
    public int getItemCount() {
        return (null != listData ? listData.size() : 0);
    }

    // 리스트 재배열.
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onItemMove(int from_position, int to_position) throws JSONException {
        PlantListItem item = listData.get(from_position);
        listData.remove(from_position);
        listData.add(to_position, item);

        String fromName = getName.get(from_position);
        String fromModel = getModel.get(from_position);
        String toName = getName.get(to_position);
        String toModel = getModel.get(to_position);
        String fromNoti = getNoti.get(from_position);
        String toNoti = getNoti.get(to_position);

        getName.set(from_position, toName);
        getName.set(to_position, fromName);
        getModel.set(from_position, toModel);
        getModel.set(to_position, fromModel);
        getNoti.set(from_position, toNoti);
        getNoti.set(to_position, fromNoti);

        ((HomeActivity)HomeActivity.mContext).changeNameModel(from_position, to_position);
        notifyItemMoved(from_position, to_position);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemSwipe(int position) throws JSONException {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("'" + getName.get(position) + "'을 리스트에서 삭제하시겠습니까?");
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                listData.remove(position);
                getName.remove(position);
                getModel.remove(position);
                getNoti.remove(position);
                ((HomeActivity)HomeActivity.mContext).removeNameModel(position);
                notifyItemRemoved(position);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                notifyDataSetChanged();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void setOnItemClickListener(OnItemClickEventListener listener) {
        mItemClickListener = listener;
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private Button information, revise;

        ItemViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            information = (Button) itemView.findViewById(R.id.infoBTN);
            revise = (Button) itemView.findViewById(R.id.editBTN);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        mItemClickListener.onItemClick(view, position);
                    }
                }
            });
            information.setOnClickListener(new View.OnClickListener() { // 모델명 정보.
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        toast = Toast.makeText(context, getModel.get(position), Toast.LENGTH_SHORT);
                        toast.cancel();
                        toast.show();
                    }
                }
            });

            revise.setOnClickListener(new View.OnClickListener() { // 모델명 별칭 수정.
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, PlantNameRevise.class);
                        intent.putExtra("name", name.getText().toString());
                        intent.putExtra("position", position);
                        ((Activity) context).startActivityForResult(intent, 2);
                    }
                }
            });


        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getNameModel(View view) {
        context = view.getContext();
        sharedPreferences = context.getSharedPreferences("PlantInform", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        getModel = new ArrayList<String>();
        getName = new ArrayList<String>();
        getNoti = new ArrayList<String>();
        modelJSON = new JSONArray();
        nameJSON = new JSONArray();
        notiJSON = new JSONArray();

        String jsonModel = sharedPreferences.getString("model", null); // 값 로드.
        if (jsonModel != null) {
            try {
                modelJSON = new JSONArray(jsonModel);
                for (int i = 0; i < modelJSON.length(); i++) {
                    String modelJ = modelJSON.optString(i);
                    getModel.add(modelJ);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String jsonName = sharedPreferences.getString("name", null);
        if (jsonName != null) {
            try {
                nameJSON = new JSONArray(jsonName);
                for (int i = 0; i < nameJSON.length(); i++) {
                    String nameJ = nameJSON.optString(i);
                    getName.add(nameJ);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String jsonNoti = sharedPreferences.getString("noti", null);
        if (jsonNoti != null) {
            try {
                notiJSON = new JSONArray(jsonNoti);
                for(int i=0; i<notiJSON.length(); i++) {
                    String notiJ = notiJSON.optString(i);
                    getNoti.add(notiJ);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeNameModel(int position) {

    }
}
