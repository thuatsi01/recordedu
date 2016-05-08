package com.bkapp.recordedu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bkapp.recordedu.io.ReadData;

public class SplashActivity extends Activity {

    private int timeOut = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ReadData dataEcho = new ReadData();
                dataEcho.LoadJSON();
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                i.putExtra("dataEcho", dataEcho.jsonEcho.toString());
                startActivity(i);
                finish();
            }
        }, timeOut);
    }

}

