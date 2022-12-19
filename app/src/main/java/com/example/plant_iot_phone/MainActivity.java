package com.example.plant_iot_phone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    // 상단 바.
    Button homeBTN, menuBTN;
    TextView modelName;
    TextView lastUpdate;

    // 환경 값
    RelativeLayout tempLayout, humiLayout, illuLayout;
    TextView tempValue, humiValue, illuValue, levelValue;
    TextView errTempT, errHumiT;

    // 제어.
    String fan = "", fanE = "", ledL = "", ledR = "", water = "", pump = "", mode = "";
    ToggleButton modeBTN;
    LinearLayout fanBTN, fanEBTN, ledLBTN, ledRBTN, waterBTN, pumpBTN;
    TextView fanStepT, fanT, fanET, ledLT, ledRT, waterT, pumpT;
    ImageView fanI, fanEI, ledLI, ledRI, waterI, pumpI;

    // 그래프.
    PagerAdapter adapter;
    ViewPager main_viewpager;
    TabLayout main_tabLayout;

    String name = "", model = "";
    String getValueURL = "http://aj3dlab.dothome.co.kr/Plant_value_Android.php";
    String sendCommandURL = "http://aj3dlab.dothome.co.kr/Plant_command_Android.php";
    GetValue gValue;
    SendActive sActive;

    Toast toast;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent getIntent = getIntent();
        name = getIntent.getStringExtra("name");
        model = getIntent.getStringExtra("model");

        // 상단 바.
        homeBTN = (Button) findViewById(R.id.homeBTN);
        homeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        menuBTN = (Button) findViewById(R.id.menuBTN);
        menuBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                startActivityForResult(intent, 1);
            }
        });
        modelName = (TextView) findViewById(R.id.modelName);
        lastUpdate = (TextView) findViewById(R.id.lastUpdate);
        modelName.setText(name);

        // 환경 값.
        modeBTN = (ToggleButton) findViewById(R.id.modeBTN);
        modeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mode) {
                    case "auto":
                        mode = "manual";
                        modeBTN.setChecked(false);

                        fanBTN.setEnabled(true);
                        fanEBTN.setEnabled(true);
                        ledLBTN.setEnabled(true);
                        ledRBTN.setEnabled(true);
                        waterBTN.setEnabled(true);
                        pumpBTN.setEnabled(true);
                        break;
                    case "manual":
                        mode = "auto";
                        modeBTN.setChecked(true);

                        fanBTN.setEnabled(false);
                        fanEBTN.setEnabled(false);
                        ledLBTN.setEnabled(false);
                        ledRBTN.setEnabled(false);
                        waterBTN.setEnabled(false);
                        pumpBTN.setEnabled(false);
                        break;
                    default:
                        if (modeBTN.isChecked()) {
                            mode = "auto";
                            modeBTN.setChecked(true);

                            fanBTN.setEnabled(false);
                            fanEBTN.setEnabled(false);
                            ledLBTN.setEnabled(false);
                            ledRBTN.setEnabled(false);
                            waterBTN.setEnabled(false);
                            pumpBTN.setEnabled(false);
                        } else {
                            mode = "manual";
                            modeBTN.setChecked(false);

                            fanBTN.setEnabled(true);
                            fanEBTN.setEnabled(true);
                            ledLBTN.setEnabled(true);
                            ledRBTN.setEnabled(true);
                            waterBTN.setEnabled(true);
                            pumpBTN.setEnabled(true);
                        }
                        break;
                }
                SendCommand();
            }
        });

        tempLayout = (RelativeLayout) findViewById(R.id.tempLayout);
        tempValue = (TextView) findViewById(R.id.tempValue);
        errTempT = (TextView) findViewById(R.id.errTemp);
        tempLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Data.class);
                intent.putExtra("model", model);
                intent.putExtra("value", "temp");
                startActivity(intent);
            }
        });

        humiLayout = (RelativeLayout) findViewById(R.id.humiLayout);
        humiValue = (TextView) findViewById(R.id.humiValue);
        errHumiT = (TextView) findViewById(R.id.errHumi);
        humiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Data.class);
                intent.putExtra("model", model);
                intent.putExtra("value", "humi");
                startActivity(intent);
            }
        });

        illuLayout = (RelativeLayout) findViewById(R.id.illuLayout);
        illuValue = (TextView) findViewById(R.id.illuValue);
        illuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Data.class);
                intent.putExtra("model", model);
                intent.putExtra("value", "illu");
                startActivity(intent);
            }
        });

        levelValue = (TextView) findViewById(R.id.levelValue);

        // 제어.
        fanBTN = (LinearLayout) findViewById(R.id.fanBTN);
        fanT = (TextView) findViewById(R.id.fanT);
        fanI = (ImageView) findViewById(R.id.fanI);
        fanStepT = (TextView) findViewById(R.id.fanStepT);
        fanBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fan.equals("off")) {
                    PopupMenu fanPop = new PopupMenu(MainActivity.this, fanBTN);
                    getMenuInflater().inflate(R.menu.menu_fan, fanPop.getMenu());
                    fanPop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            fanI.setImageResource(R.drawable.fan_on);
                            switch (menuItem.getItemId()) {
                                case R.id.fan1:
                                    fan = "on1";
                                    fanStepT.setText("①");
                                    break;
                                case R.id.fan2:
                                    fan = "on2";
                                    fanStepT.setText("②");
                                    break;
                                case R.id.fan3:
                                    fan = "on3";
                                    fanStepT.setText("③");
                                    break;
                            }
                            SendCommand();
                            return false;
                        }
                    });
                    fanPop.show();
                } else {
                    fan = "off";
                    fanI.setImageResource(R.drawable.fan_off);
                    fanStepT.setText("");
                }
                SendCommand();
            }
        });
        fanBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (fan.equals("on1") || fan.equals("on2") || fan.equals("on3")) {
                    PopupMenu fanPop = new PopupMenu(MainActivity.this, fanBTN);
                    getMenuInflater().inflate(R.menu.menu_fan, fanPop.getMenu());
                    fanPop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.fan1:
                                    fan = "on1";
                                    fanStepT.setText("①");
                                    break;
                                case R.id.fan2:
                                    fan = "on2";
                                    fanStepT.setText("②");
                                    break;
                                case R.id.fan3:
                                    fan = "on3";
                                    fanStepT.setText("③");
                                    break;
                            }
                            SendCommand();
                            return false;
                        }
                    });
                    fanPop.show();
                }

                return true;
            }
        });

        fanEBTN = (LinearLayout) findViewById(R.id.fanEBTN);
        fanET = (TextView) findViewById(R.id.fanET);
        fanEI = (ImageView) findViewById(R.id.fanEI);
        fanEBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fanE.equals("off")) {
                    fanE = "on";
                    fanEI.setImageResource(R.drawable.fan_on);
                } else {
                    fanE = "off";
                    fanEI.setImageResource(R.drawable.fan_off);
                }
                SendCommand();
            }
        });

        ledLBTN = (LinearLayout) findViewById(R.id.ledLBTN);
        ledLT = (TextView) findViewById(R.id.ledLT);
        ledLI = (ImageView) findViewById(R.id.ledLI);
        ledLBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ledL.equals("off")) {
                    ledL = "on";
                    ledLI.setImageResource(R.drawable.led_on);
                } else {
                    ledL = "off";
                    ledLI.setImageResource(R.drawable.led_off);
                }
                SendCommand();
            }
        });

        ledRBTN = (LinearLayout) findViewById(R.id.ledRBTN);
        ledRT = (TextView) findViewById(R.id.ledRT);
        ledRI = (ImageView) findViewById(R.id.ledRI);
        ledRBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ledR.equals("off")) {
                    ledR = "on";
                    ledRI.setImageResource(R.drawable.led_on);
                } else {
                    ledR = "off";
                    ledRI.setImageResource(R.drawable.led_off);
                }
                SendCommand();
            }
        });

        waterBTN = (LinearLayout) findViewById(R.id.waterBTN);
        waterT = (TextView) findViewById(R.id.waterT);
        waterI = (ImageView) findViewById(R.id.waterI);
        waterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (water.equals("off")) {
                    water = "on";
                    waterI.setImageResource(R.drawable.water_on);
                } else {
                    water = "off";
                    waterI.setImageResource(R.drawable.water_off);
                }
                SendCommand();
            }
        });

        pumpBTN = (LinearLayout) findViewById(R.id.pumpBTN);
        pumpT = (TextView) findViewById(R.id.pumpT);
        pumpI = (ImageView) findViewById(R.id.pumpI);
        pumpBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pump.equals("off")) {
                    pump = "on";
                    pumpI.setImageResource(R.drawable.pump_on);
                } else {
                    pump = "off";
                    pumpI.setImageResource(R.drawable.pump_off);
                }
                SendCommand();
            }
        });

        // 그래프.
        main_viewpager = (ViewPager) findViewById(R.id.main_pager);
        main_tabLayout = (TabLayout) findViewById(R.id.main_tabLayout);
        adapter = new MainPagerAdapter(getSupportFragmentManager());

        main_viewpager.setAdapter(adapter);
        main_tabLayout.setupWithViewPager(main_viewpager);

        // 처음 실행.
        gValue = new GetValue();
        gValue.execute(getValueURL);
        timeTask();
    }

    public void timeTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                gValue = new GetValue();
                gValue.execute(getValueURL);
            }
        };
        timer = new Timer();
        timer.schedule(task, 25000, 25000); //25초마다 실행
    }

    // 메뉴에서 선택 후 돌아올 때.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String menu = data.getStringExtra("menu");
                switch (menu) {
                    case "data":
                        Intent intentD = new Intent(getApplicationContext(), Data.class);
                        intentD.putExtra("model", model);
                        intentD.putExtra("value", "temp");
                        startActivity(intentD);
                        break;
                    case "auto":
                        Intent intentA = new Intent(getApplicationContext(), UserSetting.class);
                        intentA.putExtra("model", model);
                        startActivity(intentA);
                        break;
                    case "wifi":
                        Intent intentW = new Intent(getApplicationContext(), WifiSetting.class);
                        intentW.putExtra("model", model);
                        startActivity(intentW);
                        break;
                    case "setting":
                        toastShow("업데이트 예정!");
                        break;
                    default:
                        break;
                }
            }
        }
        if (requestCode == 2) {
            if(resultCode == RESULT_OK) {
                SendCommand();
            }
        }
    }


    // 현재 상태 읽어오기.
    class GetValue extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();

            String serverURL = (String) params[0];
            String postParameters = "model=" + model;

            try {
                URL phpUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) phpUrl.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                        while (true) {
                            String line = br.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            String TAG_JSON = "aj3dlab";
            String date = "", time = "";
            String tempD = "", humiD = "", illuD = "", levelD = "";
            String fanD = "", fanED = "", ledLD = "", ledRD = "", waterD = "", pumpD = "", modeD = "";
            String errTemp = "", errHumi = "";
            try {
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    date = item.getString("date");
                    time = item.getString("time");

                    tempD = item.getString("temp");
                    humiD = item.getString("humi");
                    illuD = item.getString("illu");
                    levelD = item.getString("level");

                    fanD = item.getString("fan");
                    fanED = item.getString("fanE");
                    ledLD = item.getString("ledL");
                    ledRD = item.getString("ledR");
                    waterD = item.getString("water");
                    pumpD = item.getString("pump");
                    modeD = item.getString("mode").trim();

                    errTemp = item.getString("errTemp");
                    errHumi = item.getString("errHumi");
                }

                lastUpdate.setText(date + " " + time);
                errTempT.setText(errTemp);
                errHumiT.setText(errHumi);

                // 환경 값.
                tempValue.setText(String.format("%.2f", Float.valueOf(tempD)));
                humiValue.setText(String.format("%.2f", Float.valueOf(humiD)));
                illuValue.setText(illuD);
                if (levelD.equals("FULL")) {
                    levelValue.setText("적절");
                } else if (levelD.equals("EMPTY")) {
                    levelValue.setText("부족");
                } else {
                    levelValue.setText("에러");
                }

                // 내부팬.
                if (fanD.equals("FANOFF")) {
                    fan = "off";
                    fanI.setImageResource(R.drawable.fan_off);
                    fanStepT.setText("");
                } else {
                    fanI.setImageResource(R.drawable.fan_on);
                    switch (fanD) {
                        case "FANON1":
                            fan = "on1";
                            fanStepT.setText("①");
                            break;
                        case "FANON2":
                            fan = "on2";
                            fanStepT.setText("②");
                            break;
                        case "FANON3":
                            fan = "on3";
                            fanStepT.setText("③");
                            break;
                        default:
                            break;
                    }
                }

                // 외부팬.
                if (fanED.equals("EXTFOFF")) {
                    fanE = "off";
                    fanEI.setImageResource(R.drawable.fan_off);
                } else {
                    fanE = "on";
                    fanEI.setImageResource(R.drawable.fan_on);
                }
                // 전등(좌).
                if (ledLD.equals("LLAMPOFF")) {
                    ledL = "off";
                    ledLI.setImageResource(R.drawable.led_off);
                } else {
                    ledL = "on";
                    ledLI.setImageResource(R.drawable.led_on);
                }
                //전등(우).
                if (ledRD.equals("RLAMPOFF")) {
                    ledR = "off";
                    ledRI.setImageResource(R.drawable.led_off);
                } else {
                    ledR = "on";
                    ledRI.setImageResource(R.drawable.led_on);
                }
                // 연무기.
                if (waterD.equals("WATEROFF")) {
                    water = "off";
                    waterI.setImageResource(R.drawable.water_off);
                } else {
                    water = "on";
                    waterI.setImageResource(R.drawable.water_on);
                }
                // 펌프.
                if (pumpD.equals("PUMPOFF")) {
                    pump = "off";
                    pumpI.setImageResource(R.drawable.pump_off);
                } else {
                    pump = "on";
                    pumpI.setImageResource(R.drawable.pump_on);
                }

                // 자동/수동.
                if (modeD.equals("AUTO")) {
                    mode = "auto";
                    modeBTN.setChecked(true);

                    fanBTN.setEnabled(false);
                    fanEBTN.setEnabled(false);
                    ledLBTN.setEnabled(false);
                    ledRBTN.setEnabled(false);
                    waterBTN.setEnabled(false);
                    pumpBTN.setEnabled(false);
                } else if (modeD.equals("MANUAL")) {
                    mode = "manual";
                    modeBTN.setChecked(false);

                    fanBTN.setEnabled(true);
                    fanEBTN.setEnabled(true);
                    ledLBTN.setEnabled(true);
                    ledRBTN.setEnabled(true);
                    waterBTN.setEnabled(true);
                    pumpBTN.setEnabled(true);
                } else {
                    fanBTN.setEnabled(false);
                    fanEBTN.setEnabled(false);
                    ledLBTN.setEnabled(false);
                    ledRBTN.setEnabled(false);
                    waterBTN.setEnabled(false);
                    pumpBTN.setEnabled(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void SendCommand() {
        String command = fan + ":" + fanE + ":" + ledL + ":" + ledR + ":" + water + ":" + pump + ":" + mode;
        sActive = new SendActive();
        sActive.execute(sendCommandURL, command);
        timer.cancel();
        timeTask();
    }

    // 동작 제어.
    class SendActive extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();

            String serverURL = (String) params[0];
            String postParameters = "model=" + model + "&command=" + (String) params[1];

            try {
                URL phpUrl = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) phpUrl.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.connect();

                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(postParameters.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                        while (true) {
                            String line = br.readLine();
                            if (line == null)
                                break;
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonHtml.toString();
        }

        protected void onPostExecute(String str) {
            String TAG_JSON = "aj3dlab";
            try {
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}