package com.bkapp.recordedu;

import java.io.File;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private final String ECHOLIST_FOLDER = "/EchoList";
    private final String EXTERNAL_STORAGE = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath();

    private Animation animationZoomIn;
    private ImageView mIVLogo;
    private ImageButton mIBEchoNow, mICMakeEcho;
    private TranslateAnimation aminationTranslateMakeEcho,
            amitionTranslateEchoNow;
    private float widthScreen;
    private String getDataEcho;
    private BroadcastReceiver receiverDataEcho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this, "OnCreate", Toast.LENGTH_LONG).show();
        File file = new File(EXTERNAL_STORAGE + ECHOLIST_FOLDER);
        if (!file.exists()) {
            file.mkdir();
        }
        getDataEcho = getIntent().getStringExtra("dataEcho");
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        widthScreen = displaymetrics.widthPixels / 2;
        // ------------------------------------------------------------
        mIVLogo = (ImageView) findViewById(R.id.logo);
        animationZoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.animator.zoomin);
        animationZoomIn.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (animationZoomIn == arg0) {
                    mIBEchoNow.setVisibility(View.VISIBLE);
                    mICMakeEcho.setVisibility(View.VISIBLE);
                    mIBEchoNow.startAnimation(aminationTranslateMakeEcho);
                    mICMakeEcho.startAnimation(amitionTranslateEchoNow);
                }
            }
        });
        // ------------------------------------------------------------
        mIBEchoNow = (ImageButton) findViewById(R.id.echonow);
        mIBEchoNow.setVisibility(View.INVISIBLE);
        aminationTranslateMakeEcho = new TranslateAnimation(0, widthScreen,
                mIBEchoNow.getPivotY(), mIBEchoNow.getPivotY());
        aminationTranslateMakeEcho.setDuration(500);
        aminationTranslateMakeEcho
                .setAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (animation == aminationTranslateMakeEcho) {
                            mIBEchoNow.setX(widthScreen);
                        }
                    }
                });
        // ------------------------------------------------------------
        mICMakeEcho = (ImageButton) findViewById(R.id.makeecho);
        mICMakeEcho.setVisibility(View.INVISIBLE);
        amitionTranslateEchoNow = new TranslateAnimation(widthScreen * 2,
                widthScreen, mICMakeEcho.getPivotY(), mICMakeEcho.getPivotY());
        amitionTranslateEchoNow.setDuration(500);
        amitionTranslateEchoNow.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (animation == amitionTranslateEchoNow) {
                    mICMakeEcho.setX(widthScreen);
                }
            }
        });
        // ------------------------------------------------------------
        mIVLogo.startAnimation(animationZoomIn);
        // ------------------------------------------------------------
        mICMakeEcho.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(MainActivity.this, MakeEchoActivity.class);
                i.putExtra("dataEcho", getDataEcho);
                startActivity(i);
                overridePendingTransition(R.animator.right_in,
                        R.animator.left_out);
            }
        });
        mIBEchoNow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EchoNowActivity.class);
                i.putExtra("dataEcho", getDataEcho);
                startActivity(i);
                overridePendingTransition(R.animator.right_in,
                        R.animator.left_out);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(getPackageName() + ".dataEcho");
        receiverDataEcho = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle bundle = intent.getExtras();
                getDataEcho = bundle.getString("dataEcho");
            }
        };
        registerReceiver(receiverDataEcho, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiverDataEcho);
    }
}
