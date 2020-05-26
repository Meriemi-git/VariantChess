package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;


public class SignUpFragment extends VariantChessFragment implements Validator.ValidationListener {
    @NotEmpty
    @Email
    private EditText txt_mail;

    @Password(min = 8, scheme = Password.Scheme.ALPHA_NUMERIC)
    private EditText txt_pwd;

    private Button btn_mail_connect;

    private Validator validator;


    private SessionManager sessionManager;

    public SignUpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.sessionManager = SessionManager.getInstance(this.getActivity());
        this.bindViews();
        this.bindListeners();
        this.validator = new Validator(this);
        this.validator.setValidationListener(this);

    }

    @Override
    protected void bindViews() {
        this.txt_mail = this.getView().findViewById(R.id.signup_txt_mail);
        this.txt_pwd = this.getView().findViewById(R.id.signup_txt_pwd);
        this.btn_mail_connect = this.getView().findViewById(R.id.signup_btn_mail_connect);
    }

    @Override
    protected void bindListeners() {
        this.btn_mail_connect.setOnClickListener(v -> this.validator.validate());
    }

    @Override
    public void onValidationSucceeded() {
        this.sessionManager.signUpWithEmail(this.txt_mail.getText().toString(), this.txt_pwd.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this.getActivity());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
