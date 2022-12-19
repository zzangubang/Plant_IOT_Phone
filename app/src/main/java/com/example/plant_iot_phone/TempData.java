package com.example.plant_iot_phone;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TempData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TempData extends Fragment {
    TextView dataFirst, dataLast;
    LinearLayout lastBTN, anotherBTN;
    TextView dataDate;

    WebView tempGraph;
    WebSettings tempGraphSettings;

    TableLayout tempTable;

    String model = "";
    String firstDate = "", lastDate = "";

    String getValueTableURL = "http://aj3dlab.dothome.co.kr/Plant_valueTable_Android.php";
    GetValueTable gValueT;
    String getValueTableDURL = "http://aj3dlab.dothome.co.kr/Plant_valueTableD_Android.php";
    GetValueTableD gValueTD;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TempData() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TempData.
     */
    // TODO: Rename and change types and number of parameters
    public static TempData newInstance(String param1, String param2) {
        TempData fragment = new TempData();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_temp_data, container, false);

        model = ((Data)getActivity()).model;

        dataFirst = (TextView) v.findViewById(R.id.dataFirst);
        dataLast = (TextView) v.findViewById(R.id.dataLast);

        lastBTN = (LinearLayout) v.findViewById(R.id.lastBTN);
        lastBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataDate.setText(lastDate);
                gValueT = new GetValueTable();
                gValueT.execute(getValueTableURL, lastDate);
                tempGraph.loadUrl("http://aj3dlab.dothome.co.kr/Plant_tempGraph.php?model=" + model + "&date=" + lastDate);
            }
        });
        anotherBTN = (LinearLayout) v.findViewById(R.id.anotherBTN);
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String monthS = String.valueOf(month+1);
                String dayOfMonthS = String.valueOf(dayOfMonth);
                if(month+1 < 10)
                    monthS = "0" + String.valueOf(month+1);
                if(dayOfMonth < 10)
                    dayOfMonthS = "0" +String.valueOf(dayOfMonth);
                String selectDate = year + "-" + monthS + "-" + dayOfMonthS;

                gValueT = new GetValueTable(); // 선택한 날짜의 데이터 구하기.
                gValueT.execute(getValueTableURL, selectDate);
                dataDate.setText(selectDate);
                tempGraph.loadUrl("http://aj3dlab.dothome.co.kr/Plant_tempGraph.php?model=" + model + "&date=" + selectDate);
            }
        }, mYear, mMonth, mDay);
        anotherBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        dataDate = (TextView) v.findViewById(R.id.dataDate);

        tempTable = (TableLayout) v.findViewById(R.id.tempTable);
        tempGraph = (WebView) v.findViewById(R.id.tempGraph);
        tempGraph.setWebViewClient(new WebViewClient());
        tempGraphSettings = tempGraph.getSettings();
        tempGraphSettings.setJavaScriptEnabled(true);
        tempGraphSettings.setSupportMultipleWindows(false);
        tempGraphSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        tempGraphSettings.setLoadWithOverviewMode(true);
        tempGraphSettings.setUseWideViewPort(true);
        tempGraphSettings.setSupportZoom(false);
        tempGraphSettings.setBuiltInZoomControls(false);
        tempGraphSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        tempGraphSettings.setCacheMode(tempGraphSettings.LOAD_NO_CACHE);
        tempGraphSettings.setDomStorageEnabled(true);

        gValueTD = new GetValueTableD();
        gValueTD.execute(getValueTableDURL);
        return v;
    }


    // 상태 값 읽어오기.
    class GetValueTable extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            StringBuilder jsonHtml = new StringBuilder();

            String serverURL = (String) params[0];
            String postParameters = "model=" + model + "&date=" + params[1];

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

        @RequiresApi(api = Build.VERSION_CODES.O)
        protected void onPostExecute(String str) {
            String TAG_JSON = "aj3dlab";
            String date = "", time = "", value = "";
            tempTable.removeViews(1, tempTable.getChildCount()-1); // 테이블 비우기.

            try {
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    date = item.getString("date");
                    time = item.getString("time");
                    value = item.getString("temp");

                    TableRow tableRow = new TableRow(getContext());
                    tableRow.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));

                    // 날짜 값 넣기.
                    TextView DTextView = new TextView(getContext());
                    DTextView.setText(date);
                    DTextView.setPadding(2, 2, 2, 2);
                    DTextView.setGravity(Gravity.CENTER);
                    tableRow.addView(DTextView);

                    // 시간 값 넣기.
                    TextView TtextView = new TextView(getContext());
                    TtextView.setText(time);
                    TtextView.setPadding(2, 2, 2, 2);
                    TtextView.setGravity(Gravity.CENTER);
                    tableRow.addView(TtextView);

                    // 센서 값 넣기.
                    TextView V1textView = new TextView(getContext());
                    V1textView.setText(String.format("%.2f", Float.valueOf(value)));
                    V1textView.setPadding(2, 2, 2, 2);
                    V1textView.setGravity(Gravity.CENTER);
                    tableRow.addView(V1textView);

                    tempTable.addView(tableRow);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // 날짜 읽어오기.
    class GetValueTableD extends AsyncTask<String, Integer, String> {
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
            String date = "";

            try {
                JSONObject jsonObject = new JSONObject(str);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);

                    date = item.getString("date");

                    if(i == 0) {
                        lastDate = date;
                        dataLast.setText(date);
                        gValueT = new GetValueTable();
                        gValueT.execute(getValueTableURL, date);
                        dataDate.setText(date);
                        tempGraph.loadUrl("http://aj3dlab.dothome.co.kr/Plant_tempGraph.php?model=" + model + "&date=" + date);
                    }
                    else {
                        firstDate = date;
                        dataFirst.setText(date);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}