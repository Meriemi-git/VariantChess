package fr.aboucorp.variantchess.app.views;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import fr.aboucorp.teamchess.R;
import fr.aboucorp.variantchess.app.multiplayer.NakamaSessionManager;

public class ConnexionActivity extends AbstractActivity {
    private NakamaSessionManager manager;
    public EditText txt_mail;
    public EditText txt_pwd;
    public Button btn_connexion_google;
    public Button btn_connexion_facebook;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.board);
        this.manager = new NakamaSessionManager(this);
        bindViews();
        bindListeners();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        GoogleSignInClient clientsignin = GoogleSignIn.getClient(this, gso);
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
        this.btn_connexion_facebook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
        this.btn_connexion_google.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            manager.stopClient();
            Log.i("fr.aboucorp.teamchess", "Stoping Client");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
