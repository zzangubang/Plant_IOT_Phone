package com.example.plant_iot_phone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
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

public class LoginActivity extends AppCompatActivity {
    // 입력칸.
    EditText idEdit, passEdit;
    ImageView idEdit_null, passEdit_null;

    // 로그인.
    TextView editWrongT;
    Button loginBTN;

    String getLoginURL = "http://aj3dlab.dothome.co.kr/Plant_loginCheck_Android.php";
    GetLogin gLogin;
    String id = "", pass = "";
    ProgressDialog dialog;
    InputMethodManager imm;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 로딩창.
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("로그인 중입니다");

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); // 키보드.
        sharedPreferences = getSharedPreferences("PlantUser", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // 로그인 유지.
        id = sharedPreferences.getString("id", "");
        pass = sharedPreferences.getString("pass", "");

        if(!id.equals("") && !pass.equals("")) { // 로그인 정보가 있는 상황. (자동 로그인)
            gLogin = new GetLogin();
            gLogin.execute(getLoginURL);
        }

        // 입력칸.
        idEdit_null = (ImageView) findViewById(R.id.idEdit_null);
        idEdit = (EditText) findViewById(R.id.idEdit);
        idEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals("")) {
                    idEdit_null.setImageResource(0);
                }
                else {
                    idEdit_null.setImageResource(R.drawable.login_edit_null);
                }
            }
        });
        idEdit_null.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                idEdit.setText(null);
            }
        });

        passEdit_null = (ImageView) findViewById(R.id.passEdit_null);
        passEdit = (EditText) findViewById(R.id.passEdit);
        passEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals("")) {
                    passEdit_null.setImageResource(0);
                }
                else {
                    passEdit_null.setImageResource(R.drawable.login_edit_null);
                }
            }
        });
        passEdit_null.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passEdit.setText(null);
            }
        });

        // 로그인.
        editWrongT = (TextView) findViewById(R.id.editWrongT);
        loginBTN = (Button) findViewById(R.id.loginBTN);
        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = idEdit.getText().toString();
                pass = passEdit.getText().toString();
                if(id.equals("") || pass.equals("")) {
                    if(!id.equals("") && pass.equals("")) { // 아이디는 입력했지만, 비밀번호가 입력되지 않았을 경우.
                        editWrongT.setText("비밀번호를 입력해주세요.");
                        passEdit.setFocusableInTouchMode(true);
                        passEdit.requestFocus();
                        imm.showSoftInput(passEdit, 0);
                    }
                    else { // 아이디, 비밀번호 둘 다 입력이 안되어있거나, 아이디만 입력이 안되어 있을 경우.
                        editWrongT.setText("아이디를 입력해주세요");
                        idEdit.setFocusableInTouchMode(true);
                        idEdit.requestFocus();
                        imm.showSoftInput(idEdit, 0);
                    }
                }
                else { // 다 입력했을 경우.
                    dialog.show();
                    gLogin = new GetLogin();
                    gLogin.execute(getLoginURL);
                }
            }
        });
    }

    // 로그인 정보 얻어오기.
    class GetLogin extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();

            String serverURL = (String) params[0];
            String postParameters = "id=" + id + "&pass=" + pass;

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

                    check = item.getString("check");
                }

                if (check.equals("OK")) {
                    editor.putString("id", id);
                    editor.putString("pass", pass);
                    editor.commit();

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                editWrongT.setText("아이디 또는 비밀번호를 잘못 입력하셨습니다.\n입력하신 내용을 다시 확인해주세요.");
            }
            dialog.dismiss();
        }
    }
}