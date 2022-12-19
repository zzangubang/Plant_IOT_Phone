package com.example.plant_iot_phone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;

public class Data extends AppCompatActivity {
    Button homeBTN, menuBTN;

    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Fragment TempData, HumiData, IlluData;

    String value = "", model = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        Intent getIntent = getIntent();
        value = getIntent.getStringExtra("value");
        model = getIntent.getStringExtra("model");

        homeBTN = (Button) findViewById(R.id.homeBTN);
        homeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        menuBTN = (Button) findViewById(R.id.menuBTN);
        menuBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu dataPop = new PopupMenu(Data.this, menuBTN);
                getMenuInflater().inflate(R.menu.menu_data, dataPop.getMenu());
                dataPop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.temp:
                                transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.dataFrame, TempData).commitAllowingStateLoss();
                                break;
                            case R.id.humi:
                                transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.dataFrame, HumiData).commitAllowingStateLoss();
                                break;
                            case R.id.illu:
                                transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.dataFrame, IlluData).commitAllowingStateLoss();
                                break;
                        }
                        return false;
                    }
                });
                dataPop.show();
            }
        });

        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        TempData = new TempData();
        HumiData = new HumiData();
        IlluData = new IlluData();
        if(value.equals("temp") || value.equals("")) {
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.dataFrame, TempData).commitAllowingStateLoss();
        }
        else if(value.equals("humi")) {
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.dataFrame, HumiData).commitAllowingStateLoss();
        }
        else if(value.equals("illu")) {
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.dataFrame, IlluData).commitAllowingStateLoss();
        }
    }
}