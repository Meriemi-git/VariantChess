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

    private Button btn_register;

    @Email
    @NotEmpty
    private EditText txt_mail;

    @NotEmpty
    @Password(min = 8, scheme = Password.Scheme.ALPHA_NUMERIC)
    private EditText txt_pwd;

    @NotEmpty
    @ConfirmPassword
    private EditText txt_condifm_pwd;

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
        this.bindViews();
        this.bindListeners();
        this.validator = new Validator(this);
        this.validator.setValidationListener(this);
        this.sessionManager = SessionManager.getInstance((MainActivity) this.getActivity());
    }

    @Override
    protected void bindViews() {
        this.txt_mail = this.getView().findViewById(R.id.sign_in_mail);
        this.txt_pwd = this.getView().findViewById(R.id.sign_in_pwd);
        this.txt_condifm_pwd = this.getView().findViewById(R.id.sign_in_btn_confirm_pwd);
        this.btn_register = this.getView().findViewById(R.id.register_btn_create_account);
    }

    @Override
    protected void bindListeners() {
        this.btn_register.setOnClickListener(v -> this.validator.validate());
    }

    @Override
    public void onValidationSucceeded() {
        try {
            this.sessionManager.signInWithEmail(this.txt_mail.getText().toString(), this.txt_pwd.getText().toString());
        } catch (ExecutionException e) {
            Log.i("fr.aboucorp.variantchess","ExecutionException during mail signin message : " + e.getMessage());
            Toast.makeText(this.getActivity(), R.string.error_during_signin, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.i("fr.aboucorp.variantchess","InterruptedException during mail signin message : " + e.getMessage());
            Toast.makeText(this.getActivity(), R.string.error_during_signin, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this.getActivity());
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
