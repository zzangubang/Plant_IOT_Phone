package com.example.plant_iot_phone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserSetting extends AppCompatActivity {
    // 상단바.
    Button homeBTN, saveBTN;

    // 메뉴.
    Button tempBTN, humiBTN, fanBTN, ledLBTN, ledRBTN;

    // 설정 화면.
    TextView cautionT, autoValue;
    FrameLayout frameLayout;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Fragment TempSetting, HumiSetting, FanSetting, LedLSetting, LedRSetting;

    String model = "";

    public int tempMin = 0, tempMax = 60, humiMin = 0, humiMax = 100;
    public int fanWH = 24, fanWM = 0, fanSH = 24, fanSM = 0;
    public int ledLWH = 0, ledLWM = 0, ledLSH = 0, ledLSM = 0;
    public int ledRWH = 0, ledRWM = 0, ledRSH = 0, ledRSM = 0;
    String getAutoURL = "http://aj3dlab.dothome.co.kr/Plant_autoG_Android.php";
    GetAuto gAuto;
    String sendAutoURL = "http://aj3dlab.dothome.co.kr/Plant_autoS_Android.php";
    SendAuto sAuto;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);

        Intent getIntent = getIntent();
        model = getIntent.getStringExtra("model");

        // 상단바.
        homeBTN = (Button) findViewById(R.id.homeBTN);
        homeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        saveBTN = (Button) findViewById(R.id.saveBTN);
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tempMin > tempMax || humiMin > humiMax) {
                    toastShow("온도 및 습도의 최소/최대 범위를 제대로 설정해주세요");
                } else {
                    if (fanWH == fanSH && fanWM == fanSM) {
                        fanWH = 0;
                        fanWM = 0;
                        fanSH = 0;
                        fanSM = 0;
                    }
                    if (ledLWH == ledLSH && ledLWM == ledLSM) {
                        ledLWH = 0;
                        ledLWM = 0;
                        ledLSH = 0;
                        ledLSM = 0;
                    }
                    if (ledRWH == ledRSH && ledRWM == ledRSM) {
                        ledRWH = 0;
                        ledRWM = 0;
                        ledRSH = 0;
                        ledRSM = 0;
                    }
                    String fanW = String.valueOf(fanWH) + ":" + String.valueOf(fanWM) + "0";
                    String fanS = String.valueOf(fanSH) + ":" + String.valueOf(fanSM) + "0";
                    String ledLW = String.valueOf(ledLWH) + ":" + String.valueOf(ledLWM) + "0";
                    String ledLS = String.valueOf(ledLSH) + ":" + String.valueOf(ledLSM) + "0";
                    String ledRW = String.valueOf(ledRWH) + ":" + String.valueOf(ledRWM) + "0";
                    String ledRS = String.valueOf(ledRSH) + ":" + String.valueOf(ledRSM) + "0";

                    String command = String.valueOf(tempMin) + "." + String.valueOf(tempMax) + "." + String.valueOf(humiMin) + "." + String.valueOf(humiMax) + "." + fanW + "." + fanS + "." + ledLW + "." + ledLS + "." + ledRW + "." + ledRS;
                    sAuto = new SendAuto();
                    sAuto.execute(sendAutoURL, command);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                    toastShow("저장되었습니다");
                }
            }
        });

        // 메뉴.
        tempBTN = (Button) findViewById(R.id.tempBTN);
        tempBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempBTN.setTextColor(Color.parseColor("#ffffff"));
                tempBTN.setBackgroundResource(R.drawable.setting_btn_click);

                humiBTN.setTextColor(Color.parseColor("#000000"));
                fanBTN.setTextColor(Color.parseColor("#000000"));
                ledLBTN.setTextColor(Color.parseColor("#000000"));
                ledRBTN.setTextColor(Color.parseColor("#000000"));
                humiBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                fanBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                ledLBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                ledRBTN.setBackgroundResource(R.drawable.setting_btn_selector);

                cautionT.setText("※ 0 ~ 60°C 까지 설정 가능합니다 ※");
                autoValue.setText(String.valueOf(tempMin) + " ~ " + String.valueOf(tempMax) + "°C");

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.setFrame, TempSetting).commitAllowingStateLoss();
            }
        });
        humiBTN = (Button) findViewById(R.id.humiBTN);
        humiBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                humiBTN.setTextColor(Color.parseColor("#ffffff"));
                humiBTN.setBackgroundResource(R.drawable.setting_btn_click);

                tempBTN.setTextColor(Color.parseColor("#000000"));
                fanBTN.setTextColor(Color.parseColor("#000000"));
                ledLBTN.setTextColor(Color.parseColor("#000000"));
                ledRBTN.setTextColor(Color.parseColor("#000000"));
                tempBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                fanBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                ledLBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                ledRBTN.setBackgroundResource(R.drawable.setting_btn_selector);

                cautionT.setText("※ 0 ~ 100% 까지 설정 가능합니다 ※");
                autoValue.setText(String.valueOf(humiMin) + " ~ " + String.valueOf(humiMax) + "%");

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.setFrame, HumiSetting).commitAllowingStateLoss();
            }
        });
        fanBTN = (Button) findViewById(R.id.fanBTN);
        fanBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String work = "", stop = "";
                fanBTN.setTextColor(Color.parseColor("#ffffff"));
                fanBTN.setBackgroundResource(R.drawable.setting_btn_click);

                humiBTN.setTextColor(Color.parseColor("#000000"));
                tempBTN.setTextColor(Color.parseColor("#000000"));
                ledLBTN.setTextColor(Color.parseColor("#000000"));
                ledRBTN.setTextColor(Color.parseColor("#000000"));
                humiBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                tempBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                ledLBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                ledRBTN.setBackgroundResource(R.drawable.setting_btn_selector);

                cautionT.setText("※ 계속 가동을 원할 경우, 같은 시간으로 설정하면 됩니다 ※");
                if (fanWH == fanSH && fanWM == fanSM) {
                    autoValue.setText("계속 가동");
                } else {
                    if (0 <= fanWH && fanWH < 12) { // 가동 시간.
                        if (fanWH == 0) {
                            work = "AM 12:" + String.valueOf(fanWM) + "0 ~ ";
                        } else {
                            work = "AM " + String.valueOf(fanWH) + ":" + String.valueOf(fanWM) + "0 ~ ";
                        }
                    } else {
                        if (fanWH == 12) {
                            work = "PM 12:" + String.valueOf(fanWM) + "0 ~ ";
                        } else {
                            work = "PM " + String.valueOf(fanWH - 12) + ":" + String.valueOf(fanWM) + "0 ~ ";
                        }
                    }
                    if (0 <= fanSH && fanSH < 12) { // 중단 시간.
                        if (fanSH == 0) {
                            stop = "AM 12:" + String.valueOf(fanSM) + "0";
                        } else {
                            stop = "AM " + String.valueOf(fanSH) + ":" + String.valueOf(fanSM) + "0";
                        }
                    } else {
                        if (fanSH == 12) {
                            stop = "PM 12:" + String.valueOf(fanSM) + "0";
                        } else {
                            stop = "PM " + String.valueOf(fanSH - 12) + ":" + String.valueOf(fanSM) + "0";
                        }
                    }

                    autoValue.setText(work + stop);
                }

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.setFrame, FanSetting).commitAllowingStateLoss();
            }
        });
        ledLBTN = (Button) findViewById(R.id.ledLBTN);
        ledLBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String work = "", stop = "";
                ledLBTN.setTextColor(Color.parseColor("#ffffff"));
                ledLBTN.setBackgroundResource(R.drawable.setting_btn_click);

                humiBTN.setTextColor(Color.parseColor("#000000"));
                fanBTN.setTextColor(Color.parseColor("#000000"));
                tempBTN.setTextColor(Color.parseColor("#000000"));
                ledRBTN.setTextColor(Color.parseColor("#000000"));
                humiBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                fanBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                tempBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                ledRBTN.setBackgroundResource(R.drawable.setting_btn_selector);

                cautionT.setText("※ 계속 가동을 원할 경우, 같은 시간으로 설정하면 됩니다 ※");
                if (ledLWH == ledLSH && ledLWM == ledLSM) {
                    autoValue.setText("계속 가동");
                } else {
                    if (0 <= ledLWH && ledLWH < 12) { // 가동 시간.
                        if (ledLWH == 0) {
                            work = "AM 12:" + String.valueOf(ledLWM) + "0 ~ ";
                        } else {
                            work = "AM " + String.valueOf(ledLWH) + ":" + String.valueOf(ledLWM) + "0 ~ ";
                        }
                    } else {
                        if (ledLWH == 12) {
                            work = "PM 12:" + String.valueOf(ledLWM) + "0 ~ ";
                        } else {
                            work = "PM " + String.valueOf(ledLWH - 12) + ":" + String.valueOf(ledLWM) + "0 ~ ";
                        }
                    }
                    if (0 <= ledLSH && ledLSH < 12) { // 중단 시간.
                        if (ledLSH == 0) {
                            stop = "AM 12:" + String.valueOf(ledLSM) + "0";
                        } else {
                            stop = "AM " + String.valueOf(ledLSH) + ":" + String.valueOf(ledLSM) + "0";
                        }
                    } else {
                        if (ledLSH == 12) {
                            stop = "PM 12:" + String.valueOf(ledLSM) + "0";
                        } else {
                            stop = "PM " + String.valueOf(ledLSH - 12) + ":" + String.valueOf(ledLSM) + "0";
                        }
                    }

                    autoValue.setText(work + stop);
                }

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.setFrame, LedLSetting).commitAllowingStateLoss();
            }
        });
        ledRBTN = (Button) findViewById(R.id.ledRBTN);
        ledRBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String work = "", stop = "";
                ledRBTN.setTextColor(Color.parseColor("#ffffff"));
                ledRBTN.setBackgroundResource(R.drawable.setting_btn_click);

                humiBTN.setTextColor(Color.parseColor("#000000"));
                fanBTN.setTextColor(Color.parseColor("#000000"));
                ledLBTN.setTextColor(Color.parseColor("#000000"));
                tempBTN.setTextColor(Color.parseColor("#000000"));
                humiBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                fanBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                ledLBTN.setBackgroundResource(R.drawable.setting_btn_selector);
                tempBTN.setBackgroundResource(R.drawable.setting_btn_selector);

                cautionT.setText("※ 계속 가동을 원할 경우, 같은 시간으로 설정하면 됩니다 ※");
                if(ledRWH == ledRSH && ledRWM == ledRSM) {
                    autoValue.setText("계속 가동");
                }
                else {
                    if (0 <= ledRWH && ledRWH < 12) { // 가동 시간.
                        if (ledRWH == 0) {
                            work = "AM 12:" + String.valueOf(ledRWM) + "0 ~ ";
                        } else {
                            work = "AM " + String.valueOf(ledRWH) + ":" + String.valueOf(ledRWM) + "0 ~ ";
                        }
                    } else {
                        if (ledRWH == 12) {
                            work = "PM 12:" + String.valueOf(ledRWM) + "0 ~ ";
                        } else {
                            work = "PM " + String.valueOf(ledRWH - 12) + ":" + String.valueOf(ledRWM) + "0 ~ ";
                        }
                    }
                    if (0 <= ledRSH && ledRSH < 12) { // 중단 시간.
                        if (ledRSH == 0) {
                            stop = "AM 12:" + String.valueOf(ledRSM) + "0";
                        } else {
                            stop = "AM " + String.valueOf(ledRSH) + ":" + String.valueOf(ledRSM) + "0";
                        }
                    } else {
                        if (ledRSH == 12) {
                            stop = "PM 12:" + String.valueOf(ledRSM) + "0";
                        } else {
                            stop = "PM " + String.valueOf(ledRSH - 12) + ":" + String.valueOf(ledRSM) + "0";
                        }
                    }

                    autoValue.setText(work + stop);
                }

                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.setFrame, LedRSetting).commitAllowingStateLoss();
            }
        });

        // 설정 화면.
        autoValue = (TextView) findViewById(R.id.autoValue);
        cautionT = (TextView) findViewById(R.id.cautionT);
        frameLayout = (FrameLayout) findViewById(R.id.setFrame);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        TempSetting = new TempSetting();
        HumiSetting = new HumiSetting();
        FanSetting = new FanSetting();
        LedLSetting = new LedLSetting();
        LedRSetting = new LedRSetting();

        gAuto = new GetAuto();
        gAuto.execute(getAutoURL);

        new Handler().postDelayed(new Runnable()
        {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run()
            {
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.setFrame, TempSetting).commitAllowingStateLoss();
            }
        }, 300);// 0.3초 정도 딜레이를 준 후 시작

    }

    // 이전 자동 모드일 때의 사용자 값 가져오기.
    class GetAuto extends AsyncTask<String, Integer, String> {
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
            String tempMinT = "", tempMaxT = "", humiMinT = "", humiMaxT = "", fanWT = "", fanST = "", ledLWT = "", ledLST = "", ledRWT = "", ledRST = "";
            try {
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    tempMinT = item.getString("tempMin");
                    tempMaxT = item.getString("tempMax");
                    humiMinT = item.getString("humiMin");
                    humiMaxT = item.getString("humiMax");
                    fanWT = item.getString("fanW");
                    fanST = item.getString("fanS");
                    ledLWT = item.getString("ledLW");
                    ledLST = item.getString("ledLS");
                    ledRWT = item.getString("ledRW");
                    ledRST = item.getString("ledRS");
                }
                String fanW[] = fanWT.split(":");
                String fanS[] = fanST.split(":");
                String ledLW[] = ledLWT.split(":");
                String ledLS[] = ledLST.split(":");
                String ledRW[] = ledRWT.split(":");
                String ledRS[] = ledRST.split(":");

                tempMin = Integer.valueOf(tempMinT);
                tempMax = Integer.valueOf(tempMaxT);
                humiMin = Integer.valueOf(humiMinT);
                humiMax = Integer.valueOf(humiMaxT);

                fanWH = Integer.valueOf(fanW[0]);
                fanWM = Integer.valueOf(fanW[1])/10;
                fanSH = Integer.valueOf(fanS[0]);
                fanSM = Integer.valueOf(fanS[1])/10;
                ledLWH = Integer.valueOf(ledLW[0]);
                ledLWM = Integer.valueOf(ledLW[1])/10;
                ledLSH = Integer.valueOf(ledLS[0]);
                ledLSM = Integer.valueOf(ledLS[1])/10;
                ledRWH = Integer.valueOf(ledRW[0]);
                ledRWM = Integer.valueOf(ledRW[1])/10;
                ledRSH = Integer.valueOf(ledRS[0]);
                ledRSM = Integer.valueOf(ledRS[1])/10;

                autoValue.setText(String.valueOf(tempMin) + " ~ " + String.valueOf(tempMax) + "°C");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 동작 제어.
    class SendAuto extends AsyncTask<String, Integer, String> {
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
}