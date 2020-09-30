package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;

public class SettingsFragment extends PreferenceFragmentCompat {

    public static final String IS_TACTICAL_MODE_ON = "preference_switch_tactical";

    private SessionManager sessionManager;

    public SettingsFragment() {
        this.sessionManager = SessionManager.getInstance(getActivity());
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.setPreferencesFromResource(R.xml.settings, rootKey);
    }
}
