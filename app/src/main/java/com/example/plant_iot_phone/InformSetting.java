package com.example.plant_iot_phone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InformSetting extends AppCompatActivity {
    Button homeBTN;

    // 현재 비밀번호.
    EditText nowPassEdit;
    ImageView nowPassNull;
    TextView nowWrongT;
    String getInformURL = "http://aj3dlab.dothome.co.kr/Plant_passG_Android.php";
    GetInfo  gInfo;

    // 변경할 비밀번호.
    EditText revisePassEdit, revisePassCheckEdit;
    ImageView revisePassNull, revisePassCheckNull;
    TextView reviseWrongT;

    // 바꾸기.
    Button reviseBTN;
    String sendInformURL = "http://aj3dlab.dothome.co.kr/Plant_passS_Android.php";
    SendInfo sInfo;

    String id = "", pass = "";
    Toast toast;
    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform_setting);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // 키보드.
        Intent getIntent = getIntent();
        id = getIntent.getStringExtra("id");

        gInfo = new GetInfo();
        gInfo.execute(getInformURL);

        homeBTN = (Button) findViewById(R.id.homeBTN);
        homeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 현재 비밀번호.
        nowPassEdit = (EditText) findViewById(R.id.nowPassEdit);
        nowPassEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals("")) {
                    nowPassNull.setImageResource(0);
                }
                else {
                    nowPassNull.setImageResource(R.drawable.login_edit_null);
                }
            }
        });
        nowPassNull = (ImageView) findViewById(R.id.nowPassNull);
        nowPassNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nowPassEdit.setText("");
            }
        });
        nowWrongT = (TextView) findViewById(R.id.nowWrongT);

        // 변경할 비밀번호.
        revisePassEdit = (EditText) findViewById(R.id.revisePassEdit);
        revisePassEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals("")) {
                    revisePassNull.setImageResource(0);
                }
                else {
                    revisePassNull.setImageResource(R.drawable.login_edit_null);
                }
            }
        });
        revisePassNull = (ImageView) findViewById(R.id.revisePassNull);
        revisePassNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revisePassEdit.setText("");
            }
        });
        revisePassCheckEdit = (EditText) findViewById(R.id.revisePassCheckEdit);
        revisePassCheckNull = (ImageView) findViewById(R.id.revisePassCheckNull);
        revisePassCheckNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                revisePassCheckEdit.setText("");
            }
        });
        revisePassCheckEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String check = revisePassCheckEdit.getText().toString();
                if(editable.equals("")) {
                    revisePassCheckNull.setImageResource(0);
                }
                else {
                    revisePassCheckNull.setImageResource(R.drawable.login_edit_null);
                }
            }
        });
        reviseWrongT = (TextView) findViewById(R.id.reviseWrongT);

        // 바꾸기
        reviseBTN = (Button) findViewById(R.id.reviseBTN);
        reviseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String now = nowPassEdit.getText().toString();
                String revise = revisePassEdit.getText().toString();
                String reviseCheck = revisePassCheckEdit.getText().toString();

                if(!now.equals("") && !revise.equals("") && !reviseCheck.equals("")) { // 모든 입력칸에 입력되어 있는 경우.
                    if(!now.equals(pass)) { // 현재 비밀번호가 틀린 경우.
                        nowWrongT.setText("비밀번호가 틀렸습니다");
                        nowPassEdit.setFocusableInTouchMode(true);
                        nowPassEdit.requestFocus();
                        imm.showSoftInput(nowPassEdit, 0);
                    }
                    else {
                        if(!revise.equals(reviseCheck)) { // 변경할 비밀번호 및 체크 입력이 일치하지 않은 경우.
                            reviseWrongT.setText("비밀번호가 일치하지 않습니다");
                            revisePassCheckEdit.setFocusableInTouchMode(true);
                            revisePassCheckEdit.requestFocus();
                            imm.showSoftInput(revisePassCheckEdit, 0);
                        }
                        else { // 조건이 다 만족한 경우!!
                            sInfo = new SendInfo();
                            sInfo.execute(sendInformURL, revise);
                            toastShow("비밀번호가 변경되었습니다");
                            finish();
                        }
                    }
                }
                else { // 빈칸이 있는 경우.
                    if(now.equals("")) {
                        nowPassEdit.setFocusableInTouchMode(true);
                        nowPassEdit.requestFocus();
                        imm.showSoftInput(nowPassEdit, 0);
                    }
                    else {
                        if(revise.equals("")) {
                            revisePassEdit.setFocusableInTouchMode(true);
                            revisePassEdit.requestFocus();
                            imm.showSoftInput(revisePassEdit, 0);
                        }
                        else {
                            if(reviseCheck.equals("")) {
                                revisePassCheckEdit.setFocusableInTouchMode(true);
                                revisePassCheckEdit.requestFocus();
                                imm.showSoftInput(revisePassCheckEdit, 0);
                            }
                        }
                    }
                    toastShow("모든 입력 칸에 입력을 해주세요");
                }
            }
        });
    }

    // 사용자 정보 얻어오기.
    class GetInfo extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();

            String serverURL = (String) params[0];
            String postParameters = "id=" + id;

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
            String check = "";
            try {
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    pass = item.getString("pass");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 변경할 비밀번호 보내기.
    class SendInfo extends AsyncTask<String, Integer, String> {
        String revisePass = "";
        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();

            revisePass = (String) params[1];

            String serverURL = (String) params[0];
            String postParameters = "id=" + id + "&pass=" + revisePass;

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