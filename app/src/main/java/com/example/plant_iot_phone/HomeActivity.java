package com.example.plant_iot_phone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    Button addList;

    //RecyclerView.
    RecyclerView plantList;
    PlantListAdapter plantListAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<PlantListItem> mArrayList;
    ItemTouchHelper mItemTouchHelper;


    // 리스트 아이템 유지.
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    ArrayList<String> getModel, getName, getNoti;
    JSONArray modelJSON, nameJSON, notiJSON;

    Toast toast;
    public static Context mContext;
    MyService MyService;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;

        startService(new Intent(this, ForcedTerminationService.class));
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
        else {
            startService(intent);
        }

        // 블루투스 및 와이파이 권한 허용.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.CHANGE_NETWORK_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    1);
        }

        addList = (Button) findViewById(R.id.addList);
        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
                    toastShow("데이터 연결을 해주세요");
                } else {
                    Intent intent = new Intent(getApplicationContext(), PlantListAdd.class);
                    startActivityForResult(intent, 1);
                }
            }
        });


        // 리스트 유지.
        sharedPreferences = getSharedPreferences("PlantInform", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        getModel = new ArrayList<String>();
        getName = new ArrayList<String>();
        getNoti = new ArrayList<String>();
        modelJSON = new JSONArray();
        nameJSON = new JSONArray();
        notiJSON = new JSONArray();

        String jsonModel = sharedPreferences.getString("model", null);
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
                for (int i = 0; i < notiJSON.length(); i++) {
                    String notiJ = notiJSON.optString(i);
                    getNoti.add(notiJ);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // RecyclerView.
        plantList = (RecyclerView) findViewById(R.id.plantList);
        mArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        plantList.setLayoutManager(linearLayoutManager);
        plantListAdapter = new PlantListAdapter(mArrayList);
        plantList.setAdapter(plantListAdapter);
        plantList.addItemDecoration(new RecyclerViewDecoration(15)); // 세로 간격.
        mItemTouchHelper = new ItemTouchHelper(new PlantItemTouchHelperCallback(plantListAdapter));
        mItemTouchHelper.attachToRecyclerView(plantList);

        for (int i = 0; i < getModel.size(); i++) {
            PlantListItem item = new PlantListItem(getName.get(i), getModel.get(i), R.drawable.home_info, R.drawable.home_edit);
            mArrayList.add(item);
            plantListAdapter.notifyItemInserted(mArrayList.size());
        }

        plantListAdapter.setOnItemClickListener(new PlantListAdapter.OnItemClickEventListener() {
            @Override
            public void onItemClick(View view, int position) {
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_NOT_CONNECTED) {
                    toastShow("데이터 연결을 해주세요");
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("model", getModel.get(position));
                    intent.putExtra("name", getName.get(position));
                    startActivity(intent);
                }
            }
        });
    }

    // 메뉴에서 선택 후 돌아올 때.
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String model = data.getStringExtra("model");
                int cnt = 0;
                for (int i = 0; i < getModel.size(); i++) {
                    if (model.equals(getModel.get(i))) {
                        cnt++;
                    }
                }
                if (cnt > 0) { // 중복된 경우.
                    toast = Toast.makeText(getApplicationContext(), "이미 추가된 모델입니다", Toast.LENGTH_SHORT);
                    toast.cancel();
                    toast.show();
                } else {
                    PlantListItem item = new PlantListItem(model, model, R.drawable.home_info, R.drawable.home_edit);
                    mArrayList.add(item);
                    plantListAdapter.notifyItemInserted(mArrayList.size());

                    modelJSON.put(model);
                    getModel.add(model);
                    nameJSON.put(model);
                    getName.add(model);
                    notiJSON.put("0");
                    getNoti.add("0");

                    editor.putString("model", modelJSON.toString());
                    editor.putString("name", nameJSON.toString());
                    editor.putString("noti", notiJSON.toString());
                    editor.commit();
                }
            }
        }

        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                String modelRevise = data.getStringExtra("modelR");
                int listPosition = data.getIntExtra("position", 0);
                mArrayList.set(listPosition, new PlantListItem(modelRevise, getModel.get(listPosition), R.drawable.home_info, R.drawable.home_edit));
                plantListAdapter.notifyItemChanged(listPosition);

                try {
                    nameJSON.put(listPosition, modelRevise);
                    getName.set(listPosition, modelRevise);
                    editor.putString("name", nameJSON.toString());
                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void changeNameModel(int from_position, int to_position) throws JSONException {
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

        nameJSON.put(to_position, fromName);
        nameJSON.put(from_position, toName);
        modelJSON.put(to_position, fromModel);
        modelJSON.put(from_position, toModel);
        notiJSON.put(to_position, fromNoti);
        notiJSON.put(from_position, toNoti);

        editor.putString("name", nameJSON.toString());
        editor.putString("model", modelJSON.toString());
        editor.putString("noti", notiJSON.toString());
        editor.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void removeNameModel(int position) {
        getName.remove(position);
        getModel.remove(position);
        getNoti.remove(position);
        modelJSON.remove(position);
        nameJSON.remove(position);
        notiJSON.remove(position);

        editor.putString("name", nameJSON.toString());
        editor.putString("model", modelJSON.toString());
        editor.putString("noti", notiJSON.toString());
        editor.commit();
    }
    public class RecyclerViewDecoration extends RecyclerView.ItemDecoration {

        private final int divHeight;

        public RecyclerViewDecoration(int divHeight)
        {
            this.divHeight = divHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.top = divHeight;
        }
    }
    public void toastShow(String msg) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    // 뒤로가기.
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        // 액티비티 죽이고, 프로세스 계속 실행.
        moveTaskToBack(true);
        finishAndRemoveTask();
    }
}