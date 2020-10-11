package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import fr.aboucorp.variantchess.R;

public class AuthentFragment extends VariantChessFragment {
    private Button btn_connect;
    private Button btn_create_account;
    private Button btn_continue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.authent_choice_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();
    }

    @Override
    protected void bindViews() {
        this.btn_connect = this.getView().findViewById(R.id.btn_connect);
        this.btn_create_account = this.getView().findViewById(R.id.btn_create_account);
        this.btn_continue = this.getView().findViewById(R.id.btn_continue);
    }

    @Override
    protected void bindListeners() {
        this.btn_connect.setOnClickListener(v -> {
            NavDirections action = AuthentFragmentDirections.actionAuthentFragmentToSignInFragment();
            Navigation.findNavController(getView()).navigate(action);
        });
        this.btn_create_account.setOnClickListener(v -> {
            NavDirections action = AuthentFragmentDirections.actionAuthentFragmentToSignUpFragment();
            Navigation.findNavController(getView()).navigate(action);
        });
        this.btn_continue.setOnClickListener(v -> {
            NavDirections action = AuthentFragmentDirections.actionAuthentFragmentToGameRulesFragment();
            Navigation.findNavController(getView()).navigate(action);
        });
    }



}
