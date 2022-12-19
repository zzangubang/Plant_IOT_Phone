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
 * Use the {@link TempSetting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TempSetting extends Fragment {
    SeekBar tempMinSeek, tempMaxSeek;
    EditText tempMinEdit, tempMaxEdit;
    int tempMin_i = 0, tempMax_i = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TempSetting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TempSetting.
     */
    // TODO: Rename and change types and number of parameters
    public static TempSetting newInstance(String param1, String param2) {
        TempSetting fragment = new TempSetting();
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
        View v = inflater.inflate(R.layout.fragment_temp_setting, container, false);

        tempMinSeek = (SeekBar) v.findViewById(R.id.tempMinSeek);
        tempMinEdit = (EditText) v.findViewById(R.id.tempMinEdit);
        tempMinSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tempMinEdit.setText(String.valueOf(i));
                ((UserSetting)getActivity()).tempMin = i;
                autoValueSetText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tempMinEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!tempMinEdit.getText().toString().equals("") && tempMinEdit.isFocusable()) {
                    tempMinEdit.setSelection(tempMinEdit.length());
                    int num = Integer.valueOf(tempMinEdit.getText().toString());
                    if (num > 60) {
                        tempMinEdit.setText("60");
                        ((UserSetting)getActivity()).tempMin = 60;
                        tempMinSeek.setProgress(60);
                    } else if (num < 0) {
                        tempMinEdit.setText("0");
                        ((UserSetting)getActivity()).tempMin = 0;
                        tempMinSeek.setProgress(0);
                    }
                    else {
                        ((UserSetting)getActivity()).tempMin = num;
                        tempMinSeek.setProgress(num);
                    }
                    autoValueSetText();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tempMaxSeek = (SeekBar) v.findViewById(R.id.tempMaxSeek);
        tempMaxEdit = (EditText) v.findViewById(R.id.tempMaxEdit);
        tempMaxSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tempMaxEdit.setText(String.valueOf(i));
                ((UserSetting)getActivity()).tempMax = i;
                autoValueSetText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        tempMaxEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!tempMaxEdit.getText().toString().equals("") && tempMaxEdit.isFocusable()) {
                    int num = Integer.valueOf(tempMaxEdit.getText().toString());
                    tempMaxEdit.setSelection(tempMaxEdit.length());
                    if (num > 60) {
                        tempMaxEdit.setText("60");
                        ((UserSetting)getActivity()).tempMax = 60;
                        tempMaxSeek.setProgress(60);
                    } else if (num < 0) {
                        tempMaxEdit.setText("0");
                        ((UserSetting)getActivity()).tempMax = 0;
                        tempMaxSeek.setProgress(0);
                    }
                    else {
                        ((UserSetting)getActivity()).tempMax = num;
                        tempMaxSeek.setProgress(num);
                    }
                    autoValueSetText();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // 처음 세팅.
        tempMin_i = ((UserSetting)getActivity()).tempMin;
        tempMax_i = ((UserSetting)getActivity()).tempMax;
        tempMinSeek.setProgress(tempMin_i);
        tempMinEdit.setText(String.valueOf(tempMin_i));
        tempMaxSeek.setProgress(tempMax_i);
        tempMaxEdit.setText(String.valueOf(tempMax_i));
        return v;
    }

    public void autoValueSetText() {
        ((UserSetting)getActivity()).autoValue.setText(String.valueOf(((UserSetting)getActivity()).tempMin) + " ~ " + String.valueOf(((UserSetting)getActivity()).tempMax) + "°C");
    }
}