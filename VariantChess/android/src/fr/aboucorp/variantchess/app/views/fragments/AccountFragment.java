package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;

public class AccountFragment extends VariantChessFragment {

    public Button btn_create;
    public Button btn_connect;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        bindListeners();

    }
    @Override
    protected void bindViews() {
        this.btn_create = getView().findViewById(R.id.btn_create);
        this.btn_connect =  getView().findViewById(R.id.btn_connect);
    }

    @Override
    protected void bindListeners() {
        this.btn_connect.setOnClickListener(v -> ((MainActivity)getActivity()).setFragment(SignUpFragment.class,"signup"));
        this.btn_create.setOnClickListener(v -> ((MainActivity)getActivity()).setFragment(SignInFragment.class,"signin"));
    }
}
