package com.example.plant_iot_phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Objects;

public class WifiPass extends Activity {
    TextView wifiName;
    EditText wifiPass;
    Button passEditNull, passEditV;
    Button cancelBTN, applyBTN;

    String name = "", pass = "";
    String editState = "invisible";

    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_wifipass);

        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Intent getIntent = getIntent();
        name = getIntent.getStringExtra("wifiName");

        wifiName = (TextView) findViewById(R.id.wifiName);
        wifiName.setText(name);
        wifiPass = (EditText) findViewById(R.id.passEdit);
        passEditNull = (Button) findViewById(R.id.passEditNull);
        passEditNull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiPass.setText("");
            }
        });
        passEditV = (Button) findViewById(R.id.passEditV);
        wifiPass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passEditV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editState.equals("invisible")) {
                    editState = "visible";
                    passEditV.setBackgroundResource(R.drawable.visible);
                    wifiPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                else {
                    editState = "invisible";
                    passEditV.setBackgroundResource(R.drawable.invisible);
                    wifiPass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
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
                pass = wifiPass.getText().toString();
                if(pass.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WifiPass.this, R.style.dialogTheme)
                            .setMessage("'" + name + "' 와이파이 비밀번호가 없습니까?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent();
                                    intent.putExtra("wifiName", name);
                                    intent.putExtra("wifiPass", "");
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    Intent intent = new Intent();
                    intent.putExtra("wifiName", name);
                    intent.putExtra("wifiPass", pass);
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
