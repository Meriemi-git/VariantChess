package fr.aboucorp.variantchess.app.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.lifecycle.ViewModelProvider;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;

public class HomeFragment extends VariantChessFragment {
    private Button home_btn_online_game;
    private SessionManager sessionManager;
    private UserViewModel userViewModel;

    public HomeFragment() {
    }

    @Override
    protected void bindViews() {
        this.home_btn_online_game = this.getView().findViewById(R.id.home_btn_online_game);
    }

    @Override
    protected void bindListeners() {
        this.home_btn_online_game.setOnClickListener(v -> {
            ((VariantChessActivity) this.getActivity()).setFragment(GameFragment.class, "newGame", null);
        });
    }

    @Override
    public void onAttach(@androidx.annotation.NonNull Context context) {
        super.onAttach(context);
        this.userViewModel = new ViewModelProvider(this.getActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();
        this.sessionManager = SessionManager.getInstance(this.getActivity());
    }
}
