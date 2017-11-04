package com.sbm.shubhamchandrakar.autoresponse;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import database.BaseData;
import database.ExtraDB;



public class MyServiceReciver extends BroadcastReceiver {
    SharedPreferences sp1;
    static int c = 0;
    Timer t1 = new Timer();
    Update u = null;
    String num;
    AudioManager audioMng;
    Context context;

    @Override
    public void onReceive(Context ctx, Intent revice_intent) {
        this.context=context;
        sp1 = ctx.getApplicationContext().getSharedPreferences("MYPREF", MainActivity.MODE_PRIVATE);
        if (c == 0) {
            TimerTask tt = new TimerTask() {
                public void run() {
                    c = 0;
                }
            };
            t1.schedule(tt, 60000);
        }
        Log.d("onrecive", revice_intent.getAction());

        if (revice_intent.getAction().equalsIgnoreCase("android.media.RINGER_MODE_CHANGED")) {
            audioMng = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            int a = audioMng.getStreamVolume(AudioManager.STREAM_RING);
//            Toast.makeText(ctx, "Volume Changed vol level: "+a, Toast.LENGTH_SHORT).show();
        }
        if (revice_intent.getAction().equalsIgnoreCase("android.media.VOLUME_CHANGED_ACTION")) {

//            displayAlert(ctx);
//            Intent i=new Intent(ctx,AleartAct.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            ctx.startActivity(i);


        }
        if (revice_intent.getAction().equalsIgnoreCase("android.intent.action.PHONE_STATE")) {
            Toast.makeText(ctx, "call aya", Toast.LENGTH_SHORT).show();
            Log.d("onring", "aaya");
            String state = revice_intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            Log.d("onring", "state: " + state);
            audioMng = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                c++;
                Log.d("onring", "ringing c==" + c);
                Log.d("onring", "call aaya");

                num = revice_intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                u = ExtraDB.show_repeat_db(ctx, num);
                if (u.count >= 4) {
                    Log.d("onring", "change ringing mode 1");
                    audioMng.setRingerMode(2);
                    audioMng.setStreamVolume(AudioManager.STREAM_RING, audioMng.getStreamMaxVolume(AudioManager.STREAM_RING), 0);

                    Log.d("onring", "change ringing mode 2");
                }
                check_phone_no_Entry(num, ctx);
                u = null;

            } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                num = revice_intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                u = ExtraDB.show_repeat_db(ctx, num);
                if (u.count >= 4) {
                    ExtraDB.update_repeat_db(num, 1, ctx);
                    int act_pr = BaseData.active_profile(ctx);
                    u = BaseData.show_profile_db(ctx, act_pr);
                    audioMng.setRingerMode(u.profile_mode);
                    u = null;
                }
                u = null;
                c = c + 5;
                Log.d("onring", "Off Hook c==" + c);

            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                c++;
                Log.d("onring", "idle c==" + c);
//                int a = u.profile_mode;

                if (c == 2) {
                    Log.d("KK", "msg kra do " + c);
                    num = revice_intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    int act_pr = BaseData.active_profile(ctx);
                    u = BaseData.show_profile_db(ctx, act_pr);
                    Log.d("KK", num + " : " + u.sms_txt + " ::  " + u.profile_mode);
                    checkSetting(num, u.sms_txt, ctx);
                    u = null;
//                    Log.d("KK", "profile mode::: " + u.profile_mode);
//                    sendSMS(num, u.sms_txt, ctx);
                }
                c = 0;
//                audioMng.setRingerMode(a);

            }
        }
    }

    public void check_phone_no_Entry(String num, Context ctx) {
        boolean rpt_value = sp1.getBoolean("rpt_sw", false);
        if (rpt_value) {
            u = ExtraDB.show_repeat_db(ctx, num);
            if (u.phone_no == null) {
                Log.d("chk", "if part 1: " + u.phone_no + " : " + u.time + " : " + u.count);
                ExtraDB.insert_repeat_db(num, 1, ctx);
                u = ExtraDB.show_repeat_db(ctx, num);
                Log.d("chk", "first entry: " + u.phone_no + " : " + u.time + " : " + u.count);
            } else {
                int cmp = ExtraDB.cmp(num, ctx);
                Log.d("chk", "else part 1: " + u.phone_no + " : " + u.time + " : " + u.count);
                if (u.count == 1) {
                    if (cmp <= 15) {
                        ExtraDB.update_repeat_db(num, u.count + 1, ctx);
                        u = ExtraDB.show_repeat_db(ctx, num);
                        Log.d("chk", "update 1: " + u.phone_no + " : " + u.time + " : " + u.count);
                    } else if (cmp > 15) {
                        ExtraDB.update_repeat_db(num, 1, ctx);
                        u = ExtraDB.show_repeat_db(ctx, num);
                        Log.d("chk", "update 2: " + u.phone_no + " : " + u.time + " : " + u.count);
                    }
                } else {
                    if (cmp <= 10) {
                        ExtraDB.update_repeat_db(num, u.count + 1, ctx);
                        u = ExtraDB.show_repeat_db(ctx, num);
                        Log.d("chk", "update last: " + u.phone_no + " : " + u.time + " : " + u.count);
                    } else if (cmp > 10) {
                        ExtraDB.update_repeat_db(num, 1, ctx);
                        u = ExtraDB.show_repeat_db(ctx, num);
                        Log.d("chk", "update 2: " + u.phone_no + " : " + u.time + " : " + u.count);
                    }
                }

                u = ExtraDB.show_repeat_db(ctx, num);
                Log.d("chk", "num: " + u.phone_no + " : " + u.time + " : " + u.count);
            }
            u = null;

        }

    }

    public void sendSMS(String no, String msg, Context ctx) {
        Log.d("smm", "sendSMS: method called");
        boolean rpt_value = sp1.getBoolean("rpt_sw", false);
        SmsManager sms = SmsManager.getDefault();
        Log.d("smm", "sendSMS: " + rpt_value);
        if (rpt_value) {
            u = ExtraDB.show_repeat_db(ctx, num);
            Log.d("chk", "num: " + u.phone_no + " : " + u.time + " : " + u.count);
            if (u.count == 1) {
                try {
//                    Log.d("kk1", "1: ");
//                    Uri smsuri = Uri.parse("smsto:+"+no);
//                    Intent i1 = new Intent(Intent.ACTION_SENDTO, smsuri);
//                    i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    i1.putExtra("sms_body",msg);
//                    i1.putExtra("com.android.phone.extra.slot", 1);
////                    ctx.startActivity(i1);
//
//                    Log.d("kk1", "2: ");
//                    Toast.makeText(ctx, "SMS sent through intent=" + rpt_value, Toast.LENGTH_SHORT).show();
                    sms.sendTextMessage(no, "+918109715099", msg, null, null);
                    Toast.makeText(ctx, "SMS ho gya sw=" + rpt_value, Toast.LENGTH_SHORT).show();
//            Toast.makeText(ctx, "sw"+rpt_value, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ctx, "Please Allow SMS Permission", Toast.LENGTH_SHORT).show();
                    Log.e("error", "Error:", e);
                }
            } else if (u.count == 2) {
                // show notification and also alert after 5/10 minute unless the user make response and also provide profile mode setting for that caller
                try {
                    sms.sendTextMessage(no, null, "If you are any emergency than give two missed call within 10 minute... I will try to reach you...", null, null);
                    Toast.makeText(ctx, "SMS ho gya sw=" + rpt_value + " count =" + u.count, Toast.LENGTH_SHORT).show();
//            Toast.makeText(ctx, "sw"+rpt_value, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(ctx, "Please Allow SMS Permission", Toast.LENGTH_SHORT).show();
                    Log.e("error", "Error:", e);
                }
            } else if (u.count == 4) {
                Toast.makeText(ctx, "Count is 4 This " + no + " was an emergency pls contact him/her", Toast.LENGTH_SHORT).show();
                Log.d("chk", "Count is 4" + u.count);
            }

            u = null;
        } else {
            try {
//                Log.d("kk1", "1: ");
//
//                Intent smsIntent = new Intent("android.intent.action.RESPOND_VIA_MESSAGE");
////                smsIntent.setType("vnd.android-dir/mms-sms");
//                smsIntent.putExtra("address", no);
//                smsIntent.putExtra("sms_body","your desired message");
//                ctx.startActivity(smsIntent);
//                Toast.makeText(ctx, "SMS sent through intent=" + rpt_value, Toast.LENGTH_SHORT).show();
//                Log.d("kk1", "2: ");
                sms.sendTextMessage(no, null, msg, null, null);
                Toast.makeText(ctx, "SMS ho gya sw=" + rpt_value, Toast.LENGTH_SHORT).show();
//            Toast.makeText(ctx, "sw"+rpt_value, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(ctx, "Please Allow SMS Permission", Toast.LENGTH_SHORT).show();
                Log.e("error", "Error:", e);
            }
        }


    }

    public boolean contactExists(Context context, String number) {
/// number is the phone number

        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cur = context.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
    }

    public void checkSetting(String no, String msg, Context ctx) {
        int pref_salected = sp1.getInt("pref", -1);
        int sim_salected = sp1.getInt("sim", -1);
        switch (pref_salected) {
            case 0:
                boolean b1 = contactExists(ctx, no);
                if (b1) {
                    sendSMS(no, msg, ctx);
                }
                break;
            case 1:
                boolean b2 = BaseData.check_salected_list(ctx, no);
                if (b2) {
                    sendSMS(no, msg, ctx);
                }
                break;
            case 2:
                Log.d("aac", "111");
                sendSMS(no, msg, ctx);
                Log.d("aac", "sms send successfully done!!!!!");
                break;
            case 3:
                boolean b3 = contactExists(ctx, no);
                if (!b3) {
                    sendSMS(no, msg, ctx);
                }
                break;
        }
    }
}
