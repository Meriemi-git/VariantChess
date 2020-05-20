package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;

public class HomeFragment extends VariantChessFragment {
    public Button home_btn_online_game;
    private SessionManager sessionManager;

    public HomeFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();
        this.sessionManager = SessionManager.getInstance((MainActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout, container, false);
        return view;
    }

    @Override
    protected void bindViews() {
        this.home_btn_online_game =  getView().findViewById(R.id.home_btn_online_game);
    }

    @Override
    protected void bindListeners() {
        this.home_btn_online_game.setOnClickListener(v -> {
            ((VariantChessActivity)getActivity()).setFragment(new GameFragment(),"newGame");
        });
    }
}
