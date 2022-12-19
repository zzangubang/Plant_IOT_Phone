package com.example.plant_iot_phone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HumiGraph#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HumiGraph extends Fragment {

    WebView humiGraph;
    WebSettings humiGraphSettings;
    String model = "";

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HumiGraph() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HumiGraph.
     */
    // TODO: Rename and change types and number of parameters
    public static HumiGraph newInstance(String param1, String param2) {
        HumiGraph fragment = new HumiGraph();
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
        View v = inflater.inflate(R.layout.fragment_humi_graph, container, false);
        model = ((MainActivity)getActivity()).model;
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        String today = mFormat.format(mDate);

        humiGraph = (WebView) v.findViewById(R.id.humiGraph);
        humiGraph.setWebViewClient(new WebViewClient());
        humiGraphSettings = humiGraph.getSettings();
        humiGraphSettings.setJavaScriptEnabled(true);
        humiGraphSettings.setSupportMultipleWindows(false);
        humiGraphSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        humiGraphSettings.setLoadWithOverviewMode(true);
        humiGraphSettings.setUseWideViewPort(true);
        humiGraphSettings.setSupportZoom(false);
        humiGraphSettings.setBuiltInZoomControls(false);
        humiGraphSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        humiGraphSettings.setCacheMode(humiGraphSettings.LOAD_NO_CACHE);
        humiGraphSettings.setDomStorageEnabled(true);
        humiGraph.loadUrl("http://aj3dlab.dothome.co.kr/Plant_humiGraph.php?date="+today+"&model="+model);

        return v;
    }
}