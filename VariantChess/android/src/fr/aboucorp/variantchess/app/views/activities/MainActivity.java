package fr.aboucorp.variantchess.app.views.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.heroiclabs.nakama.api.User;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.utils.SignType;
import fr.aboucorp.variantchess.app.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.views.fragments.AccountFragment;
import fr.aboucorp.variantchess.app.views.fragments.HomeFragment;

import static fr.aboucorp.variantchess.app.multiplayer.SessionManager.SHARED_PREFERENCE_NAME;

public class MainActivity extends VariantChessActivity {
    private UserViewModel userViewModel;
    private Toolbar toolbar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        setToolbar();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        sessionManager = SessionManager.getInstance(this);
        SharedPreferences pref = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        userViewModel.getConnected().observe(this, user ->invalidateOptionsMenu());
        User user;
        try {
            user = this.sessionManager.restoreSessionIfPossible(pref);
            userIsConnected(user);
            if (user != null) {
                setFragment(HomeFragment.class, "home", savedInstanceState);
            } else {
                setFragment(AccountFragment.class, "account", savedInstanceState);
            }
        } catch (Exception e) {
            Log.e("fr.aboucorp.variantchess", e.getMessage());
            e.printStackTrace();
        }
    }


    private void setToolbar() {
        toolbar = findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
    }

    @Override
    public void setFragment(Class fragmentClass, String tag, Bundle args) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragmentClass, args, tag);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem disconnect = this.toolbar.getMenu().findItem(R.id.menu_action_disconnect);
        MenuItem profile = this.toolbar.getMenu().findItem(R.id.menu_action_profil);
        disconnect.setVisible(userViewModel.getConnected().getValue() != null);
        profile.setVisible(userViewModel.getConnected().getValue() != null);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_profil:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.menu_action_disconnect:
                this.sessionManager.destroySession();
                this.userViewModel.setConnected(null);
                this.setFragment(AccountFragment.class, "account", this.getIntent().getExtras());
                TextView userText = toolbar.findViewById(R.id.lbl_display_name);
                userText.setText(R.string.disconnect_message);
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
    protected void bindViews() {
    }

    @Override
    protected void bindListeners() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SignType.SIGNIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            this.sessionManager.handleSignInWithGoogleResult(task);
        } else if (requestCode == SignType.SIGNUP) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            this.sessionManager.handleSignUpWithGoogleResult(task);
        }
    }

    public void userIsConnected(User connected) {
        TextView userText = toolbar.findViewById(R.id.lbl_display_name);
        this.userViewModel.setConnected(connected);
        if (connected != null) {
            userText.setText(connected.getDisplayName());
            setFragment(HomeFragment.class, "home", getIntent().getExtras());
            Toast.makeText(this, R.string.connected, Toast.LENGTH_LONG).show();
        } else {
            setFragment(AccountFragment.class, "account", getIntent().getExtras());
        }
    }
}