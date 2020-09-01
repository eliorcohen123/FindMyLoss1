package com.elior.findmyloss.PagesPackage;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.elior.findmyloss.R;

// Activity of Setting of distance
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_xml);

    }

}
