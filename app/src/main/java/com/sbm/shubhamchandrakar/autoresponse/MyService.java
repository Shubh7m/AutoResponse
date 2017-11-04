package com.sbm.shubhamchandrakar.autoresponse;


import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
    MyServiceReciver r1 = new MyServiceReciver();

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        Log.d("aa", "a1");
        Toast.makeText(this, "Service is start", Toast.LENGTH_SHORT).show();
        Log.d("asdq", "oncreate");
        IntentFilter filter = new IntentFilter(Intent.EXTRA_PHONE_NUMBER);
        filter.addAction("android.intent.action.PHONE_STATE");
        filter.addAction("android.media.RINGER_MODE_CHANGED");
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
//        filter.addAction("android.intent.action.MEDIA_BUTTON");
//        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(r1, filter);
        super.onCreate();
    }


    public void onDestroy() {
        Log.d("aa", "onDestroy: ");
        unregisterReceiver(r1);
        super.onDestroy();
    }
}
