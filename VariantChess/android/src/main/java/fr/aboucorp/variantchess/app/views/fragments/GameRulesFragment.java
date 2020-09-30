package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fr.aboucorp.variantchess.R;

public class GameRulesFragment extends VariantChessFragment {
    private Button btn_online;
    private Button btn_offline;

    @Override
    protected void bindViews() {
        this.btn_online = this.getView().findViewById(R.id.btn_online);
        this.btn_offline = this.getView().findViewById(R.id.btn_offline);
    }

    @Override
    protected void bindListeners() {
        this.btn_online.setOnClickListener(v -> {
            // TODO implement proper initialisation for BoardActicity
            /*NavDirections action = GameRulesFragmentDirections.actionGameRulesFragmentToBoardActivity();
            Navigation.findNavController(getView()).navigate(action);*/
        });
        this.btn_offline.setOnClickListener(v -> {
            // TODO implement proper initialisation for BoardActicity
            /*NavDirections action = GameRulesFragmentDirections.actionGameRulesFragmentToBoardActivity();
            Navigation.findNavController(getView()).navigate(action);*/
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_rules_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();
    }


}
