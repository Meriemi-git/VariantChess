package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;

public class AccountFragment extends VariantChessFragment {

    private Button btn_create;
    private Button btn_connect;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();

    }

    @Override
    protected void bindViews() {
        this.btn_create = this.getView().findViewById(R.id.btn_create);
        this.btn_connect = this.getView().findViewById(R.id.btn_connect);
    }

    @Override
    protected void bindListeners() {
        this.btn_connect.setOnClickListener(v -> ((MainActivity) this.getActivity()).setFragment(SignUpFragment.class, "signup", null));
        this.btn_create.setOnClickListener(v -> ((MainActivity) this.getActivity()).setFragment(SignInFragment.class, "signin", null));
    }
}
