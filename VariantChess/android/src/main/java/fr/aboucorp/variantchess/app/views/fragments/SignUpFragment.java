package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.exceptions.AuthentificationException;
import fr.aboucorp.variantchess.app.exceptions.MailAlreadyRegistered;
import fr.aboucorp.variantchess.app.exceptions.UsernameAlreadyRegistered;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

public class SignUpFragment extends VariantChessFragment {

    private Button btn_register;

    private EditText txt_username;

    private EditText txt_mail;

    private EditText txt_pwd;

    private EditText txt_confirm_pwd;

    private SessionManager sessionManager;

    private AwesomeValidation validator;

    @Override
    protected void bindViews() {
        this.txt_username = this.getView().findViewById(R.id.signup_username);
        this.txt_mail = this.getView().findViewById(R.id.signup_mail);
        this.txt_pwd = this.getView().findViewById(R.id.signup_pwd);
        this.txt_confirm_pwd = this.getView().findViewById(R.id.signup_confirm_pwd);
        this.btn_register = this.getView().findViewById(R.id.register_btn_create_account);
    }

    @Override
    protected void bindListeners() {
        this.btn_register.setOnClickListener(v ->{
            if(this.validator.validate()) {
                try {
                    ChessUser user = this.sessionManager.signUpWithEmail(this.txt_mail.getText().toString(), this.txt_pwd.getText().toString(),this.txt_username.getText().toString());
                    if(user.username != this.txt_username.getText().toString()){
                        Toast.makeText(getContext(), R.string.warn_account_exists_with_different_username, Toast.LENGTH_LONG).show();
                    }
                    ((MainActivity)this.getActivity()).userIsConnected(user);
                    // TODO redirect on gamemode fragment
                } catch (MailAlreadyRegistered e) {
                    Toast.makeText(getContext(), R.string.err_mail_already_exists, Toast.LENGTH_LONG).show();
                } catch (UsernameAlreadyRegistered e) {
                    Toast.makeText(getContext(), R.string.err_username_already_exists, Toast.LENGTH_LONG).show();
                } catch(AuthentificationException e1){
                    Toast.makeText(getContext(), R.string.err_general, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_up_layout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.bindViews();
        this.bindListeners();
        this.sessionManager = SessionManager.getInstance(this.getActivity());
        this.validator = new AwesomeValidation(COLORATION);
        this.validator.addValidation(getActivity(), R.id.signup_mail, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email_invalid);
        // String regexPassword = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])";
        // TODO add a securized regex for complex password
        String regexPassword = "[a-z]{8}[a-z]*";
        this.validator.addValidation(getActivity(), R.id.signup_pwd, regexPassword, R.string.error_wrong_password);
        this.validator.addValidation(getActivity(), R.id.signup_confirm_pwd, input -> txt_pwd.getText().toString().equals(txt_confirm_pwd.getText().toString()), R.string.error_password_match);
    }
}
