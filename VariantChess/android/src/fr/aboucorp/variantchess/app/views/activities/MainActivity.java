package fr.aboucorp.variantchess.app.views.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.views.fragments.ConnexionFragment;
import fr.aboucorp.variantchess.app.views.fragments.HomeFragment;

public class MainActivity extends VariantChessActivity {


    private GoogleSignInClient clientsignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        this.bindViews();
        this.bindListeners();
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment  = null;
        if(account != null){
            Log.i("fr.aboucorp.variantchess",account.getDisplayName());
            fragment = new HomeFragment();
        }else{
            fragment = new ConnexionFragment(clientsignin);
        }
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    @Override
    public void bindViews() {

    }
    @Override
    public void bindListeners() {

    }
}