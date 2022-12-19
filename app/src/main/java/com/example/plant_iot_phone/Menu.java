package com.example.plant_iot_phone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import java.util.Objects;

public class Menu extends Activity {
    LinearLayout totalData, userSetting, wifiSetting, setting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_menu);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        totalData = (LinearLayout) findViewById(R.id.totalData);
        totalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("menu", "data");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        userSetting = (LinearLayout) findViewById(R.id.userSetting);
        userSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("menu", "auto");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        wifiSetting = (LinearLayout) findViewById(R.id.wifiSetting);
        wifiSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("menu", "wifi");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        setting = (LinearLayout) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("menu", "setting");
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }
}
