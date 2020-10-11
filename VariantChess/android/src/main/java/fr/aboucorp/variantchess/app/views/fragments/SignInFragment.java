package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.exceptions.IncorrectCredentials;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.utils.AsyncHandler;


public class SignInFragment extends VariantChessFragment {
    private EditText txt_mail;
    private EditText txt_pwd;
    private Button btn_mail_connect;
    private ProgressBar progressBar;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    @Override
    protected void bindViews() {
        this.txt_mail = this.getView().findViewById(R.id.signin_txt_mail);
        this.txt_pwd = this.getView().findViewById(R.id.signin_txt_pwd);
        this.btn_mail_connect = this.getView().findViewById(R.id.signin_btn_mail_connect);
        this.progressBar = this.getView().findViewById(R.id.progressBar);
    }

    @Override
    protected void bindListeners() {
        btn_mail_connect.setOnClickListener(view -> {
            this.progressBar.setVisibility(View.VISIBLE);
            AsyncHandler handler = new AsyncHandler() {
                @Override
                protected Object executeAsync() throws Exception {
                    ChessUser user = sessionManager.signInWithEmail(txt_mail.getText().toString(), txt_pwd.getText().toString());
                    userViewModel.setConnected(user);
                    return user;
                }

                @Override
                protected void callbackOnUI(Object arg) {
                    super.callbackOnUI(arg);
                    NavDirections action = SignInFragmentDirections.actionSignInFragmentToGameRulesFragment((ChessUser) arg);
                    Navigation.findNavController(getView()).navigate(action);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                protected void error(Exception ex) {
                    super.error(ex);
                    progressBar.setVisibility(View.GONE);
                    if (ex instanceof IncorrectCredentials) {
                        Toast.makeText(getContext(), R.string.signin_credential_error, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), R.string.err_network, Toast.LENGTH_LONG).show();
                    }
                }
            };
            handler.start();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_layout, container, false);
        return view;
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
