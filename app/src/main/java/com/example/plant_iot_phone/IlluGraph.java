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
 * Use the {@link IlluGraph#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IlluGraph extends Fragment {

    WebView illuGraph;
    WebSettings illuGraphSettings;
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

    public IlluGraph() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IlluGraph.
     */
    // TODO: Rename and change types and number of parameters
    public static IlluGraph newInstance(String param1, String param2) {
        IlluGraph fragment = new IlluGraph();
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
        View v = inflater.inflate(R.layout.fragment_illu_graph, container, false);

        model = ((MainActivity)getActivity()).model;
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        String today = mFormat.format(mDate);

        illuGraph = (WebView) v.findViewById(R.id.illuGraph);
        illuGraph.setWebViewClient(new WebViewClient());
        illuGraphSettings = illuGraph.getSettings();
        illuGraphSettings.setJavaScriptEnabled(true);
        illuGraphSettings.setSupportMultipleWindows(false);
        illuGraphSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        illuGraphSettings.setLoadWithOverviewMode(true);
        illuGraphSettings.setUseWideViewPort(true);
        illuGraphSettings.setSupportZoom(false);
        illuGraphSettings.setBuiltInZoomControls(false);
        illuGraphSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        illuGraphSettings.setCacheMode(illuGraphSettings.LOAD_NO_CACHE);
        illuGraphSettings.setDomStorageEnabled(true);
        illuGraph.loadUrl("http://aj3dlab.dothome.co.kr/Plant_illuGraph.php?date="+today+"&model="+model);

        return v;
    }
}