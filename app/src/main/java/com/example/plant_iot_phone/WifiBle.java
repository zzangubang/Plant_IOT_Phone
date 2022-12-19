package com.example.plant_iot_phone;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class WifiBle extends Activity {
    Button newBTN, regBTN;
    ListView newList, regList;
    BleViewAdapter newAdapter, regAdapter;
    ArrayList<String> newDevice, regDevice, checkDevice;
    ArrayList<BleViewItem> newItem, regItem;
    Set<BluetoothDevice> device;

    BluetoothAdapter bluetoothAdapter;
    BluetoothSocket bluetoothSocket = null;

    Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_wifible);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) { // 기기가 블루투스 지원을 하지 않는 경우.
            toastShow("블루투스 지원을 하지 않는 기기입니다");
            try {
                unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            finish();
        }
        else { // 기기가 블루투스 지원을 하는 경우.
            if(!bluetoothAdapter.isEnabled()) { // 블루투스가 비활성인 경우.
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(intent);
            }
        }

        // 새로운 기기.
        newDevice = new ArrayList<>();
        checkDevice = new ArrayList<>();
        newItem = new ArrayList<BleViewItem>();
        newAdapter = new BleViewAdapter(this, newItem);
        newBTN = (Button) findViewById(R.id.newBTN);
        newBTN.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                else {
                    if(bluetoothAdapter.isEnabled()) {
                        bluetoothAdapter.startDiscovery();
                        newItem.clear();
                        if(newDevice!=null && newDevice.isEmpty()) {
                            newDevice.clear();
                            checkDevice.clear();
                        }
                        try{
                            unregisterReceiver(receiver);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                        registerReceiver(receiver, filter);
                    }
                    else {
                        toastShow("블루투스가 켜져 있지 않습니다");
                    }
                }
            }
        });
        newList = (ListView) findViewById(R.id.newList);
        newList.setAdapter(newAdapter);
        newList.setOnItemClickListener(new newItemClickListener());

        if(bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        else {
            if(bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.startDiscovery();
                newItem.clear();
                if(newDevice!=null && newDevice.isEmpty()) {
                    newDevice.clear();
                    checkDevice.clear();
                }
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(receiver, filter);
            }
            else {
                toastShow("블루투스가 켜져 있지 않습니다");
            }
        }

        // 등록된 기기.
        regDevice = new ArrayList<>();
        regItem = new ArrayList<BleViewItem>();
        regAdapter = new BleViewAdapter(this, regItem);
        regBTN = (Button) findViewById(R.id.regBTN);
        regBTN.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(regDevice!=null && regDevice.isEmpty()) {
                    regDevice.clear();
                }
                device = bluetoothAdapter.getBondedDevices();
                if(device.size() > 0) {
                    for(BluetoothDevice device : device) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress();
                        regItem.add(new BleViewItem(deviceName, deviceHardwareAddress));
                        regDevice.add(deviceHardwareAddress);
                    }
                }

            }
        });
        regList = (ListView) findViewById(R.id.regList);
        regList.setAdapter(regAdapter);
        regList.setOnItemClickListener(new regItemClickListener());

        if(regDevice!=null && regDevice.isEmpty()) {
            regDevice.clear();
        }
        device = bluetoothAdapter.getBondedDevices();
        if(device.size() > 0) {
            for(BluetoothDevice device : device) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                regItem.add(new BleViewItem(deviceName, deviceHardwareAddress));
                regDevice.add(deviceHardwareAddress);
            }
        }
    }
    // ACTION_FOUND를 위한 BroadcastReceiver.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int cnt = 0;
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                // deviceName이 NULL값이면 그냥 넘기기.
                if(deviceName == null || deviceName.equals("")) {
                }
                else { // 그렇지 않다면 중복 체크.
                    for(String check : checkDevice) {
                        if(check.equals(deviceName))
                            cnt += 1;
                    }
                    if(cnt > 0) { // 중복값 존재.
                    }
                    else { // 중복 존재X.
                        newItem.add(new BleViewItem(deviceName, deviceHardwareAddress));
                        checkDevice.add(deviceName);
                        newDevice.add(deviceHardwareAddress);
                        newAdapter.notifyDataSetChanged();
                    }
                }


            }
        }
    };


    // ListView 아이템 클릭 시, 연결시켜주도록 MainActivity에 정보 넘기도록. -- 등록된 기기 中
    public class regItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            toastShow(regItem.get(position).getName() + " 연결 시도");

            final String name = regItem.get(position).getName(); // get name
            final String address = regItem.get(position).getAddress(); // get address
            boolean flag = true;

            Intent intent = new Intent();
            intent.putExtra("name", name);
            intent.putExtra("address", address);
            setResult(RESULT_OK, intent);
            ((WifiSetting)WifiSetting.mContext).dialog.show();
            try {
                unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            finish();

        }
    }
    // -- 새로운 기기 中
    public class newItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            toastShow(newItem.get(position).getName()+"연결 시도");

            final String name = newItem.get(position).getName(); // get name
            final String address = newItem.get(position).getAddress(); // get address
            boolean flag = true;

            Intent intent = new Intent();
            intent.putExtra("name", name);
            intent.putExtra("address", address);
            setResult(RESULT_OK, intent);
            ((WifiSetting)WifiSetting.mContext).dialog.show();
            try {
                unregisterReceiver(receiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            finish();

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

}
