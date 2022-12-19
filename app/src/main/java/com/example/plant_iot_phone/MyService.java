package com.example.plant_iot_phone;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    private static final String PRIMARY_CHANNEL_ID = "AJPLANTs";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String> getModel, getName, getNoti;
    JSONArray modelJSON, nameJSON, notiJSON;

    GetValue gValue;
    String getValueURL = "http://aj3dlab.dothome.co.kr/Plant_value_Android.php";

    Timer timer;
    public static Context mContext;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 포그라운드 실행. (알림 띄우기)
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
        Notification notification = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("AJPLANTs")
                .setContentText("포그라운드 서비스 실행중..")
                .setSmallIcon(R.mipmap.background)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        // 수위 알림 띄우기.
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences("PlantInform", MODE_MULTI_PROCESS); // 문제 가능성 있음.
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

                for (int i = 0; i < getModel.size(); i++) {
                    gValue = new GetValue();
                    gValue.execute(getValueURL, getModel.get(i), getName.get(i), getNoti.get(i), String.valueOf(i));
                }
            }
        };
        timer.schedule(task, 1000, 25000); //25초마다 실행

        return START_STICKY;
    }

    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(String model, String name) {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("model", model);
        intent.putExtra("name", name);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        notifyBuilder
                .setContentTitle(name)
                .setContentText("수위가 낮습니다")
                .setSmallIcon(R.mipmap.background)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        return notifyBuilder;
    }

    public void sendNotification(String model, String name, int position) {
        NotificationCompat.Builder builder = getNotificationBuilder(model, name);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(position + 2, builder.build());
    }


    // 현재 상태 읽어오기.
    class GetValue extends AsyncTask<String, Integer, String> {
        String model, name, noti;
        int position;

        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();
            model = params[1];
            name = params[2];
            noti = params[3];
            position = Integer.valueOf((String) params[4]);

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
            String levelD = "";
            try {
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    levelD = item.getString("level");
                }

                if (levelD.equals("EMPTY")) {
                    if (noti.equals("0")) {
                        sendNotification(model, name, position);
                        getNoti.set(position, "1");
                        notiJSON.put(position, "1");
                        editor.putString("noti", notiJSON.toString());
                        editor.commit();
                    }
                } else {
                    getNoti.set(position, "0");
                    notiJSON.put(position, "0");
                    editor.putString("noti", notiJSON.toString());
                    editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}