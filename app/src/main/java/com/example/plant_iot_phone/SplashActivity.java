package com.example.plant_iot_phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashActivity extends Activity {
    TextView title;
    Animation fadeIn_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        title = (TextView) findViewById(R.id.title);
        fadeIn_anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        title.startAnimation(fadeIn_anim);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }
        }, 1000);


    }
}
