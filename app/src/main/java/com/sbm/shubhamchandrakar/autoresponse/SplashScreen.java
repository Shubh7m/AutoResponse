package com.sbm.shubhamchandrakar.autoresponse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends Activity {
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        overridePendingTransition(R.anim.left, R.anim.anim2);
        prefManager = new PrefManager(this);
        Log.d("getd", "splash ");
        Log.d("getd", "bln: " + prefManager.isFirstTimeLaunch());
        if (prefManager.isFirstTimeLaunch()) {
            Timer t1 = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    prefManager.setFirstTimeLaunch(false);
                    Intent intent = new Intent(SplashScreen.this, IntroActivity.class);
                    startActivity(intent);
                    SplashScreen.this.finish();
                }
            };

            t1.schedule(tt, 2000);

        } else {
            Timer t1 = new Timer();
            TimerTask tt = new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    SplashScreen.this.finish();
                }
            };
            t1.schedule(tt, 2000);

        }

    }
}
