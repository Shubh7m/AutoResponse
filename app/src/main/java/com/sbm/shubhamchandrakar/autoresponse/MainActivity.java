package com.sbm.shubhamchandrakar.autoresponse;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import database.BaseData;
import database.ExtraDB;

public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener {
    boolean isStarted = false;
    SharedPreferences sp;
    Button service_btn;
    Update u1 = null;
    Intent i_launch;
    private Button btn_unfocus;
    private Button[] btn = new Button[6];
    private int[] btn_id = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5};
    AudioManager m1;
    Context context;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_CONTACTS};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        request_permission();
        Log.d("poi", "v Code : "+Build.VERSION.SDK_INT);
//        boolean b = is_notification_access();
//        if(b){
//            b =is_notification_access();
//        }
//        NotificationManager notificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);





        overridePendingTransition(R.anim.left, R.anim.anim2);
        service_btn = (Button) findViewById(R.id.service_btn);
//        service_btn.setText("Start");
        isStarted = checkRunningService();
        if (isStarted) {
            service_btn.setText("Stop");
        }
        sp = getSharedPreferences("MYPREF", MainActivity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        int re = sp.getInt("I", 10);
        int ringer_mode = sp.getInt("ring_mode", 2);
        int ringer_volume = sp.getInt("ring_volume", 5);
        for (int i = 0; i < btn.length; i++) {
            btn[i] = (Button) findViewById(btn_id[i]);
            btn[i].setBackground(getDrawable(R.drawable.red));
            btn[i].setTextColor(Color.WHITE);
            btn[i].setOnClickListener(this);
            btn[i].setOnLongClickListener(this);
        }
        btn_unfocus = btn[0];
        if (re == 10) {
            editor = sp.edit();
            editor.putInt("I", 5);
            editor.apply();
            ExtraDB.open_or_create_repeat_db(MainActivity.this);
            BaseData.open_or_create_profile_db(MainActivity.this);
            BaseData.open_or_create_contactList_db(MainActivity.this);
            BaseData.insert_profile_db("Busy", "I am busy... I'll call you later...", 1, true, 1, MainActivity.this);
            BaseData.insert_profile_db("Driving", "I am Driving now.. I'll call you later... ", 1, false, 2, MainActivity.this);
            BaseData.insert_profile_db("Office", "I am in Meeting.. I'll call you later...", 1, false, 3, MainActivity.this);
            BaseData.insert_profile_db("Sleeping", "I am Sleeping Please call me later", 1, false, 4, MainActivity.this);
            BaseData.insert_profile_db("Colleg/School", "I am in Classroom can't talk now Pls call me later...", 1, false, 5, MainActivity.this);
            BaseData.insert_profile_db("Playing", "I am playing football.. Can't Talk now..", 1, false, 6, MainActivity.this);
//            u1 = BaseData.show_profile_db(MainActivity.this, 1);
        }
//        u1 = BaseData.show_profile_db(MainActivity.this, 1);
        int active_pr = BaseData.active_profile(MainActivity.this);
        Log.d("ACT", "active: " + active_pr);
        active_pr = active_pr - 1;
        if (7 != active_pr) {
            setFocus(btn_unfocus, btn[active_pr]);
        }
        m1 = (AudioManager) MainActivity.this.getSystemService(Context.AUDIO_SERVICE);

        service_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStarted) {
                    service_btn.setText("Start");
                    Intent i1 = new Intent(MainActivity.this, MyService.class);
                    stopService(i1);
                    Log.d("pp1", "p_mode 111: " + sp.getInt("ring_mode", 5) + " volume: " + sp.getInt("ring_volume", 8));
                    m1.setRingerMode(sp.getInt("ring_mode", 2));
                    m1.setStreamVolume(AudioManager.STREAM_RING, sp.getInt("ring_volume", 5), 0);
                    isStarted = false;
                    service_btn.setText("Start");
                } else {
                    Intent i1 = new Intent(MainActivity.this, MyService.class);
                    startService(i1);
                    int act_pr = BaseData.active_profile(MainActivity.this);
                    u1 = BaseData.show_profile_db(MainActivity.this, act_pr);
                    SharedPreferences.Editor editor = sp.edit();
                    editor = sp.edit();
                    editor.putInt("ring_mode", m1.getRingerMode());
                    editor.putInt("ring_volume", m1.getStreamVolume(AudioManager.STREAM_RING));
                    editor.apply();
                    Log.d("pp1", "p_mode 2222: " + sp.getInt("ring_mode", 5) + " volume: " + sp.getInt("ring_volume", 8));
                    m1.setRingerMode(u1.profile_mode);
                    isStarted = true;
//                    displayAlert(MainActivity.this);
                    service_btn.setText("Stop");

                }

            }
        });

        if (isStarted) {
            service_btn.setText("Stop");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!is_notification_access()){
            Toast.makeText(this, "Please allow DND Access", Toast.LENGTH_SHORT).show();
            boolean a = is_notification_access();
        }

    }

    public boolean is_notification_access(){

        NotificationManager n = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 24){
            if(n.isNotificationPolicyAccessGranted()) {

//                AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }else{
                // Ask the user to grant access
                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
                return false;
            }
        }
    return true;
    }



    public void request_permission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[2])) {
                //Show Information about why you need the permission

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Required Permissions...");
                builder.setMessage("This app needs Read Phone State, Read Contact and Send SMS permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
               AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Required Permissions...");
                builder.setMessage("This app needs Read Phone State, Read Contact and Send SMS permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Contacts, Phone and SMS", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

//            txtPermissions.setText("Permissions Required");

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissionsRequired[2])) {
//                txtPermissions.setText("Permissions Required");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Required Permissions...");
                builder.setMessage("This app needs Read Phone State, Read Contact and Send SMS permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {
//        txtPermissions.setText("We've got all permissions");
//        Toast.makeText(getBaseContext(), "We got All Permissions", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    public void displayAlert(final Context context) {

        List<String> options = new ArrayList<String>();
        options.add("For one hour....");
        options.add("For two hour....");
        options.add("For three hour....");
        options.add("For five hour....");
        options.add("custom....");
        final CharSequence[] all_option = options.toArray(new String[options.size()]);
        AlertDialog.Builder alert_dialog_profile_mode = new AlertDialog.Builder(context);
        alert_dialog_profile_mode.setItems(all_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int option_position) {

                String selectedtext = all_option[option_position].toString();
                Log.d("opt", "opt pos : " + option_position);
                Log.d("opt", "selected text : " + selectedtext);
                if (option_position == 0) {

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            stop_ser();
                            Log.d("hi", "this code will work fine.....");
                        }
                    }, 60 * 60 * 1000);

                } else if (option_position == 1) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            stop_ser();
                            Log.d("hi", "this code will work fine.....");
                        }
                    }, 2 * 60 * 60 * 1000);
                } else if (option_position == 2) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            stop_ser();
                            Log.d("hi", "this code will work fine.....");
                        }
                    }, 3 * 60 * 60 * 1000);
                } else if (option_position == 3) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            stop_ser();
                            Log.d("hi", "this code will work fine.....");
                        }
                    }, 5 * 60 * 60 * 1000);
                }
            }
        });
        alert_dialog_profile_mode.create();
        alert_dialog_profile_mode.setCancelable(false);
        alert_dialog_profile_mode.show();
    }


    @Override
    protected void onResume() {
        if (isStarted) {
            service_btn.setText("Stop");
        } else {
            service_btn.setText("Start");
        }
        super.onResume();
    }


    @Override
    public boolean onLongClick(View v) {

        switch (v.getId()) {
            case R.id.btn0:
                setFocus(btn_unfocus, btn[0]);
                Log.d("TAG1", "long click " + btn_unfocus + " : " + btn[0]);
                if (isStarted) {
                    u1 = BaseData.show_profile_db(MainActivity.this, 1);
                    m1.setRingerMode(u1.profile_mode);
                }
                return true;

            case R.id.btn1:
                setFocus(btn_unfocus, btn[1]);
                if (isStarted) {
                    u1 = BaseData.show_profile_db(MainActivity.this, 2);
                    m1.setRingerMode(u1.profile_mode);
                }
                return true;

            case R.id.btn2:
                setFocus(btn_unfocus, btn[2]);
                if (isStarted) {
                    u1 = BaseData.show_profile_db(MainActivity.this, 3);
                    m1.setRingerMode(u1.profile_mode);
                }
                return true;

            case R.id.btn3:
                setFocus(btn_unfocus, btn[3]);
                if (isStarted) {
                    u1 = BaseData.show_profile_db(MainActivity.this, 4);
                    m1.setRingerMode(u1.profile_mode);
                }
                return true;
            case R.id.btn4:
                setFocus(btn_unfocus, btn[4]);
                if (isStarted) {
                    u1 = BaseData.show_profile_db(MainActivity.this, 5);
                    m1.setRingerMode(u1.profile_mode);
                }
                return true;
            case R.id.btn5:
                setFocus(btn_unfocus, btn[5]);

                if (isStarted) {
                    u1 = BaseData.show_profile_db(MainActivity.this, 6);
                    m1.setRingerMode(u1.profile_mode);
                }
                return true;
        }
        return false;
    }

    private void stop_ser() {
        if (isStarted) {
//            service_btn.setText("Start");
            Intent i1 = new Intent(MainActivity.this, MyService.class);
            stopService(i1);

            Log.d("pp1", "p_mode 111: " + sp.getInt("ring_mode", 5) + " volume: " + sp.getInt("ring_volume", 8));
            m1.setRingerMode(sp.getInt("ring_mode", 2));
            m1.setStreamVolume(AudioManager.STREAM_RING, sp.getInt("ring_volume", 5), 0);
            isStarted = false;
            Log.d("hi", "servise is stoped.....");
//            service_btn.setText("Start");
        }
    }

    private int[] btn_name_to_id(Button btn_unfocus, Button btn_focus) {
        int res[] = new int[2];
        String s1 = btn_focus.toString();
        Log.d("btnid", "id : " + btn_focus);
        s1 = s1.substring(s1.length() - 2, s1.length() - 1);
        int a = Integer.parseInt(s1);
        a = a + 1;
        res[0] = a;
        Log.d("ACTQQQQQ", "setFocus " + a);
        String s2 = btn_unfocus.toString();
        s2 = s2.substring(s2.length() - 2, s2.length() - 1);
        int b = Integer.parseInt(s2);
        b = b + 1;
        res[1] = b;
        return res;
    }

    private void setFocus(Button btn_unfocus, Button btn_focus) {


        int a1[] = btn_name_to_id(btn_unfocus, btn_focus);
        Log.d("ACTQ", "a= " + a1[0] + " b= " + a1[1]);
//        String s1 = btn_focus.toString();
//        Log.d("btnid", "id : " + btn_focus);
//        s1 = s1.substring(s1.length() - 2, s1.length() - 1);
//        int a = Integer.parseInt(s1);
//        a = a + 1;
//        Log.d("ACTQQQQQ", "setFocus " + a);
//        String s2 = btn_unfocus.toString();
//        s2 = s2.substring(s2.length() - 2, s2.length() - 1);
//        int b = Integer.parseInt(s2);
//        b = b + 1;
//        Log.d("ACTQ", "A= " + a+" B= "+b);
//        Log.d("ACTQ", "setFocus: " + btn_focus + " : " + btn_unfocus);
        BaseData.update_profile_db(false, a1[1] + "", MainActivity.this);
        BaseData.update_profile_db(true, a1[0] + "", MainActivity.this);
        btn_unfocus.setTextColor(Color.WHITE);
        btn_unfocus.setBackground(getDrawable(R.drawable.red));
        btn_focus.setTextColor(Color.WHITE);
        btn_focus.setBackground(getDrawable(R.drawable.green));
        MainActivity.this.btn_unfocus = btn_focus;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn0:
                int a1[] = btn_name_to_id(btn_unfocus, btn[0]);
                i_launch = new Intent(this, SecondAct.class);
                i_launch.putExtra("p_key", 1);
                i_launch.putExtra("setFocus", a1[0]);
                i_launch.putExtra("unFocus", a1[1]);
                startActivity(i_launch);
                finish();
                break;

            case R.id.btn1:
                i_launch = new Intent(this, SecondAct.class);
                i_launch.putExtra("p_key", 2);
                int a2[] = btn_name_to_id(btn_unfocus, btn[1]);
                i_launch.putExtra("setFocus", a2[0]);
                i_launch.putExtra("unFocus", a2[1]);
                startActivity(i_launch);
                finish();
                break;

            case R.id.btn2:
                i_launch = new Intent(this, SecondAct.class);
                i_launch.putExtra("p_key", 3);
                int a3[] = btn_name_to_id(btn_unfocus, btn[2]);
                i_launch.putExtra("setFocus", a3[0]);
                i_launch.putExtra("unFocus", a3[1]);
                startActivity(i_launch);
                finish();
                break;

            case R.id.btn3:
                i_launch = new Intent(this, SecondAct.class);
                i_launch.putExtra("p_key", 4);
                int a4[] = btn_name_to_id(btn_unfocus, btn[3]);
                i_launch.putExtra("setFocus", a4[0]);
                i_launch.putExtra("unFocus", a4[1]);
                startActivity(i_launch);
                finish();
                break;
            case R.id.btn4:
                i_launch = new Intent(this, SecondAct.class);
                i_launch.putExtra("p_key", 5);
                int a5[] = btn_name_to_id(btn_unfocus, btn[4]);
                i_launch.putExtra("setFocus", a5[0]);
                i_launch.putExtra("unFocus", a5[1]);
                startActivity(i_launch);
                finish();
                break;
            case R.id.btn5:
                i_launch = new Intent(this, SecondAct.class);
                i_launch.putExtra("p_key", 6);
                int a6[] = btn_name_to_id(btn_unfocus, btn[5]);
                Log.d("PP", "send setfocus " + a6[0]);
                Log.d("PP", "send unfocus " + a6[1]);
                i_launch.putExtra("setFocus", a6[0]);
                i_launch.putExtra("unFocus", a6[1]);
                startActivity(i_launch);
                finish();
                break;
        }


    }

    public boolean checkRunningService() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> rsList = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo r1 : rsList) {
            if ("com.example.shubhamchandrakar.autoresponse.MyService".equals(r1.service.getClassName())) {
                Log.d("aa", "check");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.setting) {
            // go to setting pref
            startActivity(new Intent(this, SettingAct.class));
            finish();
            return true;
        }
        if (id == R.id.about) {
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }
}
