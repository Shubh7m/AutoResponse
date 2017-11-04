package com.sbm.shubhamchandrakar.autoresponse;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Shubham Chnadrakar on 12/09/2017.
 */

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
        Log.d("getd", "bln 1 : "+isFirstTimeLaunch());
        Log.d("getd", "bln 2 : "+pref.getBoolean(IS_FIRST_TIME_LAUNCH, true));
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}