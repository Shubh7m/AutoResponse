package com.sbm.shubhamchandrakar.autoresponse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingAct extends Activity {
    SharedPreferences sp1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        overridePendingTransition(R.anim.right, R.anim.anim2);
        //  Button b1 = (Button) findViewById(R.id.sim_btn);
        Button b2 = (Button) findViewById(R.id.pref_btn);
        Switch sw = (Switch) findViewById(R.id.switch1);
        sp1 = getApplicationContext().getSharedPreferences("MYPREF", MainActivity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp1.edit();
        boolean repeat_sw = sp1.getBoolean("rpt_sw", true);
        sw.setChecked(repeat_sw);
//        final NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
//        np.setMaxValue(100);
//        np.setValue(25);
//        np.setMinValue(1);
//        np.setWrapSelectorWheel(false);
//
//        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                int newValue = newVal ;
//            }
//        });

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("rpt_sw", isChecked);
                editor.apply();
                Log.d("onc", "onCheckedChanged: " + sp1.getBoolean("rpt_sw", false));
            }
        });
       /* b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingAct.this, ""+np.getValue(), Toast.LENGTH_SHORT).show();
                final int sim_salected = sp1.getInt("sim", 0);
                final AlertDialog.Builder builder = new AlertDialog.Builder(SettingAct.this);
                builder.setTitle("Salect SIM").setSingleChoiceItems(R.array.sim_setting_items, sim_salected, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("wh", "w:" + which);
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        editor.putInt("sim", selectedPosition);
                        editor.commit();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog ad = builder.create();
                        ad.cancel();
                    }
                }).create().show();
            }
        });*/
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pref_selected = sp1.getInt("pref", 0);
                final AlertDialog.Builder builder = new AlertDialog.Builder(SettingAct.this);
                final AlertDialog ad = builder.setTitle("Select Prefrence").setSingleChoiceItems(R.array.prefrences, pref_selected, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("wh", "w:" + which);
                        switch (which) {
                            case 0:
                                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setText("OK");
                                break;
                            case 1:
                                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setText("Configure");
                                break;
                            case 2:
                                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setText("OK");
                                break;
                            case 3:
                                ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setText("OK");
                                break;

                        }

                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        editor.putInt("pref", selectedPosition);
                        editor.commit();
                        if (selectedPosition == 1) {
                            Intent i = new Intent(SettingAct.this, SelectedContact.class);
                            startActivity(i);
                        }
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog ad = builder.create();
                        ad.cancel();
                    }
                }).create();
                ad.show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.anim, R.anim.left);
        startActivity(new Intent(this, MainActivity.class));
        finish();
        super.onBackPressed();
    }
}
