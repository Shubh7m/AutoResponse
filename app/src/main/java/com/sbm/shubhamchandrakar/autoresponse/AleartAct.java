package com.sbm.shubhamchandrakar.autoresponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import database.BaseData;

public class AleartAct extends Activity {
    Update u1;
    AudioManager am1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //hide activity title
        setContentView(R.layout.activity_aleart);
        am1 = (AudioManager) AleartAct.this.getSystemService(Context.AUDIO_SERVICE);
        AlertDialog.Builder Builder = new AlertDialog.Builder(this)
                .setMessage("Auto Response is runing. Do You Want to STOP there service?")
                .setTitle("Auto Response")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int act_pr = BaseData.active_profile(AleartAct.this);
                        u1 = BaseData.show_profile_db(AleartAct.this, act_pr);
                        Log.d("hil", "pmod : " + u1.profile_mode);
                        am1.setRingerMode(u1.profile_mode);
//                        startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        AleartAct.this.finish();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        finish();
                    }
                });
        AlertDialog alertDialog = Builder.create();
        alertDialog.show();
    }
}



