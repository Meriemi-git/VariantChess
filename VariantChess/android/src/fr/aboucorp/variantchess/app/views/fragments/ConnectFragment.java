package fr.aboucorp.variantchess.app.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import fr.aboucorp.variantchess.R;

public class ConnectFragment extends VariantChessFragment {
    public EditText txt_mail;
    public EditText txt_pwd;
    public SignInButton btn_connexion_google;
    public Button btn_mail_connect;
    private GoogleSignInClient clientsignin;
    OnConnectListener callback;

    public ConnectFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connexion_f, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews();
        bindListeners();
    }


    @Override
    protected void bindViews() {
        this.txt_mail = getView().findViewById(R.id.txt_mail);
        this.txt_pwd = getView().findViewById(R.id.txt_pwd);
        this.btn_mail_connect = getView().findViewById(R.id.btn_mail_connect);
        this.btn_connexion_google = getView().findViewById(R.id.btn_connexion_google);
    }

    @Override
    protected void bindListeners() {
        this.btn_connexion_google.setOnClickListener(v -> signInWithGoogle());
        this.btn_mail_connect.setOnClickListener(v -> signInWithMail());
    }

    private void signInWithMail() {

    }

    private void signInWithGoogle() {
        Intent signInIntent = clientsignin.getSignInIntent();
        startActivityForResult(signInIntent, 200);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 200) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Log.i("fr.aboucorp.variantchess","Successfully signed");
            this.callback.OnConnect();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("fr.aboucorp.variantchess", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    public void setCallback(OnConnectListener callback) {
        this.callback = callback;
    }

    public interface OnConnectListener{
        void OnConnect();
    }

    public void setClientsignin(GoogleSignInClient clientsignin) {
        this.clientsignin = clientsignin;
    }
}
