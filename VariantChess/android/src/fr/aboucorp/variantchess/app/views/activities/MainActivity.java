package fr.aboucorp.variantchess.app.views.activities;

import android.content.Intent;
import android.os.Bundle;
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
import fr.aboucorp.variantchess.app.views.fragments.HomeFragment;
import fr.aboucorp.variantchess.app.views.fragments.SignInFragment;
import fr.aboucorp.variantchess.app.views.fragments.SignUpFragment;

public class MainActivity extends VariantChessActivity {
    private UserViewModel userViewModel;
    private AccountManager accountManager;
    private Toolbar toolbar;
    private boolean menuIsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        setToolbar();
        this.accountManager = new AccountManager(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getConnected().observe(this, user -> MainActivity.this.updateConnectionUI(user));
        this.accountManager.cheskConnectedUser();
    }

    private void updateConnectionUI(User user){
        if(menuIsReady) {
            MenuItem disconnect = this.toolbar.getMenu().findItem(R.id.menu_action_disconnect);
            MenuItem profile = this.toolbar.getMenu().findItem(R.id.menu_action_profil);
            disconnect.setVisible(user != null);
            profile.setVisible(user != null);
        }
        if(user != null){
            setFragment(HomeFragment.class,"home",this.getIntent().getExtras());
        }
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void setFragment(Class fragmentClass,String tag, Bundle args){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentClass,args,tag);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menuIsReady = true;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_profil:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.menu_action_disconnect:
               this.accountManager.disconnectUser(userViewModel.getConnected().getValue());
               this.userViewModel.setConnected(null);
               this.setFragment(AccountFragment.class,"account",this.getIntent().getExtras());
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
        if (fragment instanceof SignUpFragment) {
            SignUpFragment connectionfragment = (SignUpFragment) fragment;
            connectionfragment.setAccountManager(this.accountManager);
        }else if (fragment instanceof SignInFragment) {
            SignInFragment connectionfragment = (SignInFragment) fragment;
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
            this.setFragment(HomeFragment.class,"home",this.getIntent().getExtras());
        }
    }

    public void userIsConnected(User connected) {
        if(connected != null){
            this.userViewModel.setConnected(connected);
            setFragment(HomeFragment.class,"home",getIntent().getExtras());
        }else{
            setFragment(AccountFragment.class,"account",getIntent().getExtras());
        }
    }
}