package fr.aboucorp.variantchess.app.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.SignInButton;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;

public class LinkFragment extends VariantChessFragment implements Validator.ValidationListener{

    @Password(min = 8, scheme = Password.Scheme.ALPHA_NUMERIC)
    public EditText link_txt_pwd;
    @Email
    public EditText link_txt_mail;
    public Button link_btn_mail_connect;
    public SignInButton link_btn_signup_google;
    public Button link_btn_cancel;
    public LinearLayout link_mail_signup;
    public LinearLayout link_google_signup;

    private String mail;
    private String googleToken;

    private Validator validator;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.link_layout, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validator = new Validator(this);
        validator.setValidationListener(this);
        this.bindViews();
        this.bindListeners();
        Bundle args = getArguments();
        mail = args.getString("mail");
        link_txt_mail.setText(mail);
        googleToken = args.getString("googleToken");
        this.sessionManager = SessionManager.getInstance((MainActivity) getActivity());
        if(googleToken != null){
            link_mail_signup.setVisibility(View.VISIBLE);
            link_google_signup.setVisibility(View.INVISIBLE);
        }else{
            link_mail_signup.setVisibility(View.INVISIBLE);
            link_google_signup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onValidationSucceeded() {
        ((VariantChessActivity) getActivity()).confirmLinkAccount(mail, link_txt_pwd.getText().toString(),this.googleToken);
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

    @Override
    protected void bindViews() {
        link_txt_pwd = getView().findViewById(R.id.link_txt_pwd);
        link_txt_mail = getView().findViewById(R.id.link_txt_mail);
        link_btn_mail_connect = getView().findViewById(R.id.link_btn_mail_connect);
        link_btn_signup_google = getView().findViewById(R.id.link_btn_signup_google);
        link_btn_cancel = getView().findViewById(R.id.link_btn_cancel);
        link_mail_signup = getView().findViewById(R.id.link_layout_mail_singup);
        link_google_signup = getView().findViewById(R.id.link_layout_google_signup);
    }

    @Override
    protected void bindListeners() {
        link_btn_mail_connect.setOnClickListener(v -> this.validator.validate());
        link_btn_signup_google.setOnClickListener(v -> sessionManager.signInWithGoogle());
        link_btn_cancel.setOnClickListener(v -> ((VariantChessActivity)getActivity()).setFragment(HomeFragment.class,"home",getArguments()));
    }
}
