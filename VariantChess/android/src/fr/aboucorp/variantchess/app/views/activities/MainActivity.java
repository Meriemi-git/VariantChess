package fr.aboucorp.variantchess.app.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.views.fragments.AccountFragment;
import fr.aboucorp.variantchess.app.views.fragments.ConnectFragment;
import fr.aboucorp.variantchess.app.views.fragments.HomeFragment;

public class MainActivity extends VariantChessActivity implements ConnectFragment.OnConnectListener{


    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setToolbar();
        this.bindViews();
        this.bindListeners();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        this.googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        Fragment fragment  = null;
        if(account != null){
            Log.i("fr.aboucorp.variantchess",account.getDisplayName());
            setFragment(new HomeFragment());
        }else{
            setFragment(new AccountFragment());
        }

    }

    public void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void bindViews() { }
    @Override
    public void bindListeners() { }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_profil:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.menu_action_disconnect:
               disconnectUser();
                return true;
            case R.id.menu_action_settings:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void disconnectUser() {
        this.googleSignInClient.signOut()
                .addOnCompleteListener(this, task -> Toast.makeText(MainActivity.this,R.string.disconnect_message,Toast.LENGTH_LONG).show());
        this.setFragment(new AccountFragment());
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ConnectFragment) {
            ConnectFragment connectionfragment = (ConnectFragment) fragment;
            connectionfragment.setClientsignin(this.googleSignInClient);
            connectionfragment.setCallback(this);
        }
    }

    @Override
    public void OnConnect() {
        this.setFragment(new HomeFragment());
    }
}