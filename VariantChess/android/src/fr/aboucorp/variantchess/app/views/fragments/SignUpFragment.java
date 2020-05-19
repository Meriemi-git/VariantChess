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
import fr.aboucorp.variantchess.app.views.activities.MainActivity;


public class SignUpFragment extends VariantChessFragment implements Validator.ValidationListener {
    @NotEmpty
    @Email
    public EditText txt_mail;

    @Password(min = 8, scheme = Password.Scheme.ALPHA_NUMERIC)
    public EditText txt_pwd;

    public Button btn_mail_connect;

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
        sessionManager = SessionManager.getInstance((MainActivity) getActivity());
        bindViews();
        bindListeners();
        validator = new Validator(this);
        validator.setValidationListener(this);

    }

    @Override
    protected void bindViews() {
        this.txt_mail = getView().findViewById(R.id.signup_txt_mail);
        this.txt_pwd = getView().findViewById(R.id.signup_txt_pwd);
        this.btn_mail_connect = getView().findViewById(R.id.signup_btn_mail_connect);
    }

    @Override
    protected void bindListeners() {
        this.btn_mail_connect.setOnClickListener(v -> validator.validate());
    }

    @Override
    public void onValidationSucceeded() {
        this.sessionManager.signUpWithEmail(this.txt_mail.getText().toString(), this.txt_pwd.getText().toString());
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
