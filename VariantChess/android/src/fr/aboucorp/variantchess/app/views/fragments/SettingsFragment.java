package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import fr.aboucorp.variantchess.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }
}
