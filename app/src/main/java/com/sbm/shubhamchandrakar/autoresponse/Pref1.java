package com.sbm.shubhamchandrakar.autoresponse;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Pref1 extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref1);
    }
}
