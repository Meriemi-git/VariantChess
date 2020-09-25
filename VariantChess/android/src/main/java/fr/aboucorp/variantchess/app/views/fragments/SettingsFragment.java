package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import fr.aboucorp.variantchess.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String IS_TACTICAL_MODE_ON = "preference_switch_tactical";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.setPreferencesFromResource(R.xml.settings, rootKey);
    }
}
