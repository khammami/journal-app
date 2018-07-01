package com.khammami.imerolium.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.khammami.imerolium.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set user shared preferences
        String userId = FirebaseAuth.getInstance().getUid();
        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(userId);
        prefMgr.setSharedPreferencesMode(Context.MODE_PRIVATE);

        addPreferencesFromResource(R.xml.pref_general);

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

    }
}
