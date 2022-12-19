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
 * Use the {@link TempGraph#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TempGraph extends Fragment {

    WebView tempGraph;
    WebSettings tempGraphSettings;
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

    public TempGraph() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TempGraph.
     */
    // TODO: Rename and change types and number of parameters
    public static TempGraph newInstance(String param1, String param2) {
        TempGraph fragment = new TempGraph();
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
        View v = inflater.inflate(R.layout.fragment_temp_graph, container, false);

        model = ((MainActivity)getActivity()).model;
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        String today = mFormat.format(mDate);

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
        tempGraph.loadUrl("http://aj3dlab.dothome.co.kr/Plant_tempGraph.php?date="+today+"&model="+model);

        return v;
    }
}