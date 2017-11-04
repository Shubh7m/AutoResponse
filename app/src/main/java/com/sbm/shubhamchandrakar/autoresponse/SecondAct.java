package com.sbm.shubhamchandrakar.autoresponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import database.BaseData;

public class SecondAct extends Activity {
    Update u1 = null;
    Button b1, b2;
    EditText et1;
    Switch sw;
    int p_key;
    int setFocus;
    int unFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        overridePendingTransition(R.anim.right,R.anim.anim2);
        b1 = (Button) findViewById(R.id.save_button);
        b2 = (Button) findViewById(R.id.profile_btn);
        et1 = (EditText) findViewById(R.id.editText_sms);
        sw = (Switch) findViewById(R.id.pr_switch);
        refresh();
        final int active_pr = BaseData.active_profile(SecondAct.this);
        Log.d("pr", "active pr: "+active_pr+ " p-key : "+p_key);
        if(p_key== active_pr){
            sw.setChecked(true);
        }
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    BaseData.update_profile_db(false, active_pr + "", SecondAct.this);
                    BaseData.update_profile_db(true, p_key + "", SecondAct.this);
                }



            }
        });




        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseData.update_sms(et1.getText().toString(), p_key + "", SecondAct.this);
                Toast.makeText(SecondAct.this, "Update is sucessful", Toast.LENGTH_SHORT).show();
                SecondAct.this.finish();
                Intent i = new Intent(SecondAct.this,MainActivity.class);
                startActivity(i);

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> options = new ArrayList<String>();
                options.add("Silent");
                options.add("Vibrate");
                options.add("General");
                final CharSequence[] all_option = options.toArray(new String[options.size()]);
                AlertDialog.Builder alert_dialog_profile_mode = new AlertDialog.Builder(SecondAct.this);
                alert_dialog_profile_mode.setItems(all_option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int option_position) {
                        if (option_position == 0) {
                            BaseData.update_profile_mode(option_position,p_key+"",SecondAct.this);
                            refresh();
                            Log.d("PP"," : "+option_position+" : "+p_key);
                        }
                        if (option_position == 1) {
                            BaseData.update_profile_mode(option_position,p_key+"",SecondAct.this);
                            refresh();
                            Log.d("PP"," : "+option_position+" : "+p_key);
                        }
                        if (option_position == 2) {
                            BaseData.update_profile_mode(option_position,p_key+"",SecondAct.this);
                            refresh();
                            Log.d("PP"," : "+option_position+" : "+p_key);
                        }
                    }
                });
                alert_dialog_profile_mode.create();
                alert_dialog_profile_mode.show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        SecondAct.this.finish();
        Intent i = new Intent(SecondAct.this,MainActivity.class);
        startActivity(i);
        super.onBackPressed();
    }

    public void refresh(){
        try {
            p_key = getIntent().getIntExtra("p_key", -1);
            setFocus = getIntent().getIntExtra("setFocus", -1);
            setFocus = getIntent().getIntExtra("unFocus", -1);
            Log.d("PP", "......................................");
            Log.d("PP", "setfocus "+ setFocus);
            Log.d("PP", "unfocus "+ unFocus);
            Log.d("PP", "p_key "+ p_key);
            Log.d("PP", "......................................");
            u1 = BaseData.show_profile_db(this, p_key);
            Log.d("PP", "refresh: p mod "+u1.profile_mode);
            if (u1.profile_mode == 1) {
                b2.setText("Vibrate");
            } else if (u1.profile_mode == 0) {
                b2.setText("Silent");
            } else if (u1.profile_mode == 2) {
                b2.setText("General");
            }
            et1.setText(u1.sms_txt);
        } catch (Exception e) {
            Log.e("Exp", "Error: ", e);
        }
    }
}
