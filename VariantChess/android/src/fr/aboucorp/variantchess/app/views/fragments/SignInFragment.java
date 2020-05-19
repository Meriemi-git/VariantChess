package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;
import java.util.concurrent.ExecutionException;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;

public class SignInFragment extends VariantChessFragment  implements Validator.ValidationListener {

    public Button btn_register;

    @Email
    @NotEmpty
    public EditText txt_mail;

    @NotEmpty
    @Password(min = 8, scheme = Password.Scheme.ALPHA_NUMERIC)
    public EditText txt_pwd;

    @NotEmpty
    @ConfirmPassword
    public EditText txt_condifm_pwd;

    private Validator validator;

    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        bindListeners();
        validator = new Validator(this);
        validator.setValidationListener(this);
        sessionManager = SessionManager.getInstance((MainActivity) getActivity());
    }

    @Override
    protected void bindViews() {
        this.txt_mail =  getView().findViewById(R.id.sign_in_mail);
        this.txt_pwd =  getView().findViewById(R.id.sign_in_pwd);
        this.txt_condifm_pwd =  getView().findViewById(R.id.sign_in_btn_confirm_pwd);
        this.btn_register =  getView().findViewById(R.id.register_btn_create_account);
    }

    @Override
    protected void bindListeners() {
        this.btn_register.setOnClickListener(v -> validator.validate());
    }

    @Override
    public void onValidationSucceeded() {
        try {
            this.sessionManager.signInWithEmail(txt_mail.getText().toString(),txt_pwd.getText().toString());
        } catch (ExecutionException e) {
            Log.i("fr.aboucorp.variantchess","ExecutionException during mail signin message : " + e.getMessage());
            Toast.makeText(getActivity(), R.string.error_during_signin, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.i("fr.aboucorp.variantchess","InterruptedException during mail signin message : " + e.getMessage());
            Toast.makeText(getActivity(), R.string.error_during_signin, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
