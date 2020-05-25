package fr.aboucorp.variantchess.app.views.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.PreferenceManager;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;

import fr.aboucorp.variantchess.libgdx.Board3dManager;

public class BoardFragment extends AndroidFragmentApplication {

    private Board3dManager board3dManager;
    private BoardFragmentListener boardFragmentListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return this.InitLibGdx();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.boardFragmentListener.onBoardFragmentLoaded();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean tacticalEnabled = pref.getBoolean("preference_switch_tactical", false);
        this.board3dManager.setTacticalViewEnabled(tacticalEnabled);
        pref.registerOnSharedPreferenceChangeListener((sharedPreferences, key) ->
        {
            if (key.equals("preference_switch_tactical")) {
                this.board3dManager.toogleTacticalView();
            }
        });
    }

    private View InitLibGdx() {
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        View view = this.initializeForView(this.board3dManager, config);
        return view;
    }

    public void setBoard3dManager(Board3dManager board3dManager) {
        this.board3dManager = board3dManager;
    }

    public void setBoardFragmentListener(BoardFragmentListener boardFragmentListener) {
        this.boardFragmentListener = boardFragmentListener;
    }

    @Override
    public void exit() {
        super.exit();
    }

    public interface BoardFragmentListener {
        void onBoardFragmentLoaded();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
