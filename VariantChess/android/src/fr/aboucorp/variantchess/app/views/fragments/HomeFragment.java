package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fr.aboucorp.variantchess.R;

public class HomeFragment extends VariantChessFragment {
    public Button btn_online_game;
    public Button btn_local_game;

    public HomeFragment() {
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);
        return view;
    }

    @Override
    protected void bindViews() {
        this.btn_local_game =  getView().findViewById(R.id.btn_local_game);
        this.btn_online_game =  getView().findViewById(R.id.btn_online_game);
    }

    @Override
    protected void bindListeners() {

    }
}
