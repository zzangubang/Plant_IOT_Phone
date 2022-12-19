package com.example.plant_iot_phone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class PlantNameRevise extends Activity {
    EditText nameEdit;
    Button nameEditNull;

    Button cancelBTN, applyBTN;

    String name = "";
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_plantrevise);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Intent getIntent = getIntent();
        int position = getIntent.getIntExtra("position", 0);
        name = getIntent.getStringExtra("name");

        nameEdit = (EditText) findViewById(R.id.nameEdit);
        nameEdit.setText(name);
        nameEditNull = (Button) findViewById(R.id.nameEditNull);
        nameEditNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameEdit.setText("");
            }
        });

        cancelBTN = (Button) findViewById(R.id.cancelBTN);
        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        applyBTN = (Button) findViewById(R.id.applyBTN);
        applyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameEdit.getText().toString();
                if(name.equals("")) {
                    toastShow("이름을 입력해주세요");
                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra("modelR", name);
                    intent.putExtra("position", position);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

    }

    public void toastShow(String msg) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    // 바깥 영역 터치해도 안닫히게.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }
}
