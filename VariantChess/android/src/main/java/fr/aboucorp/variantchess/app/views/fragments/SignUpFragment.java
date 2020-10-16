package fr.aboucorp.variantchess.app.views.fragments;

import android.content.Context;
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

import com.basgeekball.awesomevalidation.AwesomeValidation;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.VariantUser;
import fr.aboucorp.variantchess.app.db.viewmodel.VariantUserViewModel;
import fr.aboucorp.variantchess.app.exceptions.AuthentificationException;
import fr.aboucorp.variantchess.app.exceptions.MailAlreadyRegistered;
import fr.aboucorp.variantchess.app.exceptions.UsernameAlreadyRegistered;
import fr.aboucorp.variantchess.app.multiplayer.NakamaManager;
import fr.aboucorp.variantchess.app.utils.AsyncHandler;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

public class SignUpFragment extends VariantChessFragment {

    private Button btn_register;

    private EditText txt_username;

    private EditText txt_mail;

    private EditText txt_pwd;

    private EditText txt_confirm_pwd;

    private ProgressBar progress_bar;

    @Inject
    public NakamaManager nakamaManager;

    private AwesomeValidation validator;

    private VariantUserViewModel variantUserViewModel;


    @Override
    protected void bindViews() {
        this.txt_username = this.getView().findViewById(R.id.signup_username);
        this.txt_mail = this.getView().findViewById(R.id.signup_mail);
        this.txt_pwd = this.getView().findViewById(R.id.signup_pwd);
        this.txt_confirm_pwd = this.getView().findViewById(R.id.signup_confirm_pwd);
        this.btn_register = this.getView().findViewById(R.id.register_btn_create_account);
        this.progress_bar = this.getView().findViewById(R.id.progress_bar);
    }

    @Override
    protected void bindListeners() {
        this.btn_register.setOnClickListener(v ->{
            if(this.validator.validate()) {
                this.progress_bar.setVisibility(View.VISIBLE);
                this.btn_register.setVisibility(View.GONE);
                AsyncHandler asyncHandler = new AsyncHandler() {
                    @Override
                    protected Object executeAsync() throws Exception {
                        VariantUser user = nakamaManager.signUpWithEmail(txt_mail.getText().toString(), txt_pwd.getText().toString(), txt_username.getText().toString());
                        variantUserViewModel.setConnected(user);
                        return user;
                    }

                    @Override
                    protected void callbackOnUI(Object arg) {
                        super.callbackOnUI(arg);
                        VariantUser user = (VariantUser) arg;
                        if (user.username != txt_username.getText().toString()) {
                            Toast.makeText(getContext(), R.string.warn_account_exists_with_different_username, Toast.LENGTH_LONG).show();
                        }
                        NavDirections action = SignUpFragmentDirections.actionSignUpFragmentToGameRulesFragment(user);
                        Navigation.findNavController(getView()).navigate(action);
                    }

                    @Override
                    protected void error(Exception ex) {
                        super.error(ex);
                        progress_bar.setVisibility(View.GONE);
                        btn_register.setVisibility(View.VISIBLE);
                        if (ex instanceof MailAlreadyRegistered) {
                            Toast.makeText(getContext(), R.string.err_mail_already_exists, Toast.LENGTH_LONG).show();
                        } else if (ex instanceof UsernameAlreadyRegistered) {
                            Toast.makeText(getContext(), R.string.err_username_already_exists, Toast.LENGTH_LONG).show();
                        } else if (ex instanceof AuthentificationException) {
                            Toast.makeText(getContext(), R.string.err_network, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getContext(), R.string.err_general, Toast.LENGTH_LONG).show();
                        }
                    }
                };
                asyncHandler.start();

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
        this.variantUserViewModel = new ViewModelProvider(this).get(VariantUserViewModel.class);
        this.bindViews();
        this.validator = new AwesomeValidation(COLORATION);
        this.validator.addValidation(getActivity(), R.id.signup_mail, android.util.Patterns.EMAIL_ADDRESS, R.string.err_email_invalid);
        // String regexPassword = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])";
        // TODO add a securized regex for complex password
        String regexPassword = "[a-z]{8}[a-z]*";
        this.validator.addValidation(getActivity(), R.id.signup_pwd, regexPassword, R.string.error_wrong_password);
        this.validator.addValidation(getActivity(), R.id.signup_confirm_pwd, input -> txt_pwd.getText().toString().equals(txt_confirm_pwd.getText().toString()), R.string.error_password_match);
        this.bindListeners();
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
