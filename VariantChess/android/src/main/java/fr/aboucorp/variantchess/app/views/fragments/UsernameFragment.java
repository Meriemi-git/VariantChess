package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.exceptions.UsernameAlreadyRegistered;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;

public class UsernameFragment extends VariantChessFragment  {
    private Button username_btn_validate;
    private EditText username_txt_username;
    private SessionManager sessionManager;

    @Override
    protected void bindViews() {
        this.username_btn_validate = this.getView().findViewById(R.id.username_btn_validate);
        this.username_txt_username = this.getView().findViewById(R.id.username_txt_username);
    }

    @Override
    protected void bindListeners() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.username_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();
        this.sessionManager = SessionManager.getInstance(this.getActivity());
    }

    public void onValidationSucceeded() {
        try {
            ChessUser user = this.sessionManager.updateDisplayName(this.username_txt_username.getText().toString());
            ((MainActivity) this.getActivity()).userIsConnected(user);
        } catch (UsernameAlreadyRegistered e) {
            Toast.makeText(this.getActivity(), R.string.err_username_already_exists, Toast.LENGTH_LONG).show();
        }
    }
}
