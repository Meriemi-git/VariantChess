package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.QuickRule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.VariantChessDatabase;
import fr.aboucorp.variantchess.app.managers.AccountManager;
import fr.aboucorp.variantchess.app.multiplayer.NakamaSessionManager;

public class RegisterFragment extends VariantChessFragment  implements Validator.ValidationListener {

    public Button btn_register;
    public Button btn_signin_google;
    @NotEmpty
    public EditText txt_username;
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
    private QuickRule<EditText> uniqueUsernameRule;
    private VariantChessDatabase db;
    private NakamaSessionManager sessionManager;
    private AccountManager accountManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        bindListeners();
        validator = new Validator(this);
        validator.setValidationListener(this);
        db = VariantChessDatabase.getDatabase(getActivity());
        sessionManager = NakamaSessionManager.getInstance(getActivity());
      /*  this.uniqueUsernameRule = new QuickRule<EditText>() {

            @Override
            public boolean isValid(EditText editText) {
                return db.userDao().findByDisplayName(editText.getText().toString()) == null;
            }

            @Override
            public String getMessage(Context context) {
                return context.getString(R.string.error_duplicate_displayname);
            }
        };
        validator.put(this.txt_username,uniqueUsernameRule);*/
    }

    @Override
    protected void bindViews() {
        this.txt_username = getView().findViewById(R.id.register_username);
        this.txt_mail =  getView().findViewById(R.id.register_mail);
        this.txt_pwd =  getView().findViewById(R.id.register_pwd);
        this.txt_condifm_pwd =  getView().findViewById(R.id.register_confirm_pwd);
        this.btn_register =  getView().findViewById(R.id.register_btn_create_account);
        this.btn_signin_google = getView().findViewById(R.id.btn_signin_google);
    }

    @Override
    protected void bindListeners() {
        this.btn_register.setOnClickListener(v -> this.authentWithMail());
        this.btn_signin_google.setOnClickListener(v -> this.authentWithGoogle());
    }

    private void authentWithGoogle(){
        accountManager.authentWithGoogle();
        accountManager.signInWithGoogle();
    }

    private void authentWithMail(){
        validator.validate(true);
    }

    @Override
    public void onValidationSucceeded() {
        this.accountManager.signInWithEmail(txt_mail.getText().toString(),txt_pwd.getText().toString(),txt_username.getText().toString());
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

    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }
}
