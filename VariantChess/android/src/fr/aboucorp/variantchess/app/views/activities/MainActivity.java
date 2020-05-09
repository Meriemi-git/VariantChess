package fr.aboucorp.variantchess.app.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.user.User;
import fr.aboucorp.variantchess.app.managers.AccountManager;
import fr.aboucorp.variantchess.app.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.views.fragments.AccountFragment;
import fr.aboucorp.variantchess.app.views.fragments.ConnectFragment;
import fr.aboucorp.variantchess.app.views.fragments.HomeFragment;
import fr.aboucorp.variantchess.app.views.fragments.RegisterFragment;

public class MainActivity extends VariantChessActivity {
    private UserViewModel userViewModel;
    private AccountManager accountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        setToolbar();
        this.accountManager = new AccountManager(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    private void setToolbar() {
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        manageAuthentification();
    }

    private void manageAuthentification() {
        User connected = userViewModel.getConnectedUser();
        if(connected != null){
            if(TextUtils.isEmpty(connected.password)){

            }
            setFragment(new HomeFragment());
        }else{
            setFragment(new AccountFragment());
        }
    }

    @Override
    public void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

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
               this.accountManager.disconnectUser();
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

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof ConnectFragment) {
            ConnectFragment connectionfragment = (ConnectFragment) fragment;
            connectionfragment.setAccountManager(this.accountManager);
        }else if (fragment instanceof RegisterFragment) {
            RegisterFragment connectionfragment = (RegisterFragment) fragment;
            connectionfragment.setAccountManager(this.accountManager);
        }
    }

    @Override
    protected void bindViews() {}

    @Override
    protected void bindListeners() {}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            this.accountManager.handleAuthentWithGoogleResult(task);
            this.setFragment(new HomeFragment());
        }
    }

    public void userIsConnected(User user) {
        setFragment(new HomeFragment());
        this.userViewModel.setConnected(user);
    }
}