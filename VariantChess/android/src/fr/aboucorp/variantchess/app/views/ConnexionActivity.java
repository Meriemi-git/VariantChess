package fr.aboucorp.variantchess.app.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.NakamaSessionManager;

public class ConnexionActivity extends AbstractActivity {
    private NakamaSessionManager manager;
    public EditText txt_mail;
    public EditText txt_pwd;
    public SignInButton btn_connexion_google;
    public Button btn_connexion_facebook;
    private GoogleSignInClient clientsignin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);
        this.manager = new NakamaSessionManager(this);
        bindViews();
        bindListeners();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        clientsignin = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null){
            Log.i("fr.aboucorp.variantchess",account.getDisplayName());
        }

    }

    @Override
    protected void bindViews() {
        this.txt_mail = findViewById(R.id.txt_mail);
        this.txt_pwd = findViewById(R.id.txt_pwd);
        this.btn_connexion_facebook = findViewById(R.id.btn_connexion_facebook);
        this.btn_connexion_google = findViewById(R.id.btn_connexion_google);
    }

    @Override
    protected void bindListeners() {
        this.btn_connexion_google.setOnClickListener(v -> signIn());
    }

    private void signIn() {
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
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("fr.aboucorp.variantchess", "signInResult:failed code=" + e.getStatusCode());
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            manager.stopClient();
            Log.i("fr.aboucorp.variantchess", "Stoping Client");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
