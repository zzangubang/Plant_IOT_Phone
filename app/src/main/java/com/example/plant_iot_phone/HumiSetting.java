package com.example.plant_iot_phone;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HumiSetting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HumiSetting extends Fragment {
    SeekBar humiMinSeek, humiMaxSeek;
    EditText humiMinEdit, humiMaxEdit;
    int humiMin_i = 0, humiMax_i = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HumiSetting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HumiSetting.
     */
    // TODO: Rename and change types and number of parameters
    public static HumiSetting newInstance(String param1, String param2) {
        HumiSetting fragment = new HumiSetting();
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
        View v = inflater.inflate(R.layout.fragment_humi_setting, container, false);

        humiMinSeek = (SeekBar) v.findViewById(R.id.humiMinSeek);
        humiMinEdit = (EditText) v.findViewById(R.id.humiMinEdit);
        humiMinSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                humiMinEdit.setText(String.valueOf(i));
                ((UserSetting) getActivity()).humiMin = i;
                autoValueSetText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        humiMinEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!humiMinEdit.getText().toString().equals("") && humiMinEdit.isFocusable()) {
                    humiMinEdit.setSelection(humiMinEdit.length());
                    int num = Integer.valueOf(humiMinEdit.getText().toString());
                    if (num > 100) {
                        humiMinEdit.setText("100");
                        ((UserSetting) getActivity()).humiMin = 100;
                        humiMinSeek.setProgress(100);
                    } else if (num < 0) {
                        humiMinEdit.setText("0");
                        ((UserSetting) getActivity()).humiMin = 0;
                        humiMinSeek.setProgress(0);
                    } else {
                        ((UserSetting) getActivity()).humiMin = num;
                        humiMinSeek.setProgress(num);
                    }
                    autoValueSetText();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        humiMaxSeek = (SeekBar) v.findViewById(R.id.humiMaxSeek);
        humiMaxEdit = (EditText) v.findViewById(R.id.humiMaxEdit);
        humiMaxSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                humiMaxEdit.setText(String.valueOf(i));
                ((UserSetting) getActivity()).humiMax = i;
                autoValueSetText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        humiMaxEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!humiMaxEdit.getText().toString().equals("") && humiMaxEdit.isFocusable()) {
                    int num = Integer.valueOf(humiMaxEdit.getText().toString());
                    humiMaxEdit.setSelection(humiMaxEdit.length());
                    if (num > 100) {
                        humiMaxEdit.setText("100");
                        ((UserSetting) getActivity()).humiMax = 100;
                        humiMaxSeek.setProgress(100);
                    } else if (num < 0) {
                        humiMaxEdit.setText("0");
                        ((UserSetting) getActivity()).humiMax = 0;
                        humiMaxSeek.setProgress(0);
                    } else {
                        ((UserSetting) getActivity()).humiMax = num;
                        humiMaxSeek.setProgress(num);
                    }
                    autoValueSetText();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // 처음 세팅.
        humiMin_i = ((UserSetting) getActivity()).humiMin;
        humiMax_i = ((UserSetting) getActivity()).humiMax;
        humiMinSeek.setProgress(humiMin_i);
        humiMinEdit.setText(String.valueOf(humiMin_i));
        humiMaxSeek.setProgress(humiMax_i);
        humiMaxEdit.setText(String.valueOf(humiMax_i));

        return v;
    }

    public void autoValueSetText() {
        ((UserSetting) getActivity()).autoValue.setText(String.valueOf(((UserSetting) getActivity()).humiMin) + " ~ " + String.valueOf(((UserSetting) getActivity()).humiMax) + "%");
    }
}