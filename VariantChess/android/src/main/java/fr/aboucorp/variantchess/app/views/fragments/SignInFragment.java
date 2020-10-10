package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.exceptions.AuthentificationException;
import fr.aboucorp.variantchess.app.exceptions.IncorrectCredentials;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;


public class SignInFragment extends VariantChessFragment  {
    private EditText txt_mail;
    private EditText txt_pwd;
    private Button btn_mail_connect;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    @Override
    protected void bindListeners() {
        btn_mail_connect.setOnClickListener(view -> {
            try {
                ChessUser user = this.sessionManager.signInWithEmail(this.txt_mail.getText().toString(), this.txt_pwd.getText().toString());
                userViewModel.setConnected(user);
                NavDirections action = SignInFragmentDirections.actionSignInFragmentToGameRulesFragment();
                Navigation.findNavController(getView()).navigate(action);
            } catch (IncorrectCredentials e) {
                Toast.makeText(getContext(), R.string.signin_credential_error, Toast.LENGTH_LONG).show();
            } catch (AuthentificationException e) {
                Toast.makeText(getContext(), R.string.err_general, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_layout, container, false);
        return view;
    }

    @Override
    protected void bindViews() {
        this.txt_mail = this.getView().findViewById(R.id.signin_txt_mail);
        this.txt_pwd = this.getView().findViewById(R.id.signin_txt_pwd);
        this.btn_mail_connect = this.getView().findViewById(R.id.signin_btn_mail_connect);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.sessionManager = SessionManager.getInstance();
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        this.bindViews();
        this.bindListeners();
    }


}
