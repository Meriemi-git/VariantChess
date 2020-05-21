package fr.aboucorp.variantchess.app.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.heroiclabs.nakama.api.User;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.utils.FragmentTag;
import fr.aboucorp.variantchess.app.views.fragments.AccountFragment;
import fr.aboucorp.variantchess.app.views.fragments.HomeFragment;
import fr.aboucorp.variantchess.app.views.fragments.PartyFragment;
import fr.aboucorp.variantchess.app.views.fragments.UsernameFragment;

import static fr.aboucorp.variantchess.app.multiplayer.SessionManager.SHARED_PREFERENCE_NAME;

public class MainActivity extends VariantChessActivity implements AndroidFragmentApplication.Callbacks {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private PartyFragment partyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        setToolbar();
        sessionManager = SessionManager.getInstance(this);
        SharedPreferences pref = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        User user;
        try {
            user = this.sessionManager.restoreSessionIfPossible(pref);
            userIsConnected(user);
        } catch (Exception e) {
            Log.e("fr.aboucorp.variantchess", e.getMessage());
            e.printStackTrace();
        }
    }


    private void setToolbar() {
        toolbar = findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(getString(R.string.app_name));
        toolbar.setNavigationIcon(R.drawable.ic_action_name);
        setActionBar(toolbar);
    }

    @Override
    public void setFragment(Class<? extends Fragment> fragmentClass, String fragmentTag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment existing = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        try {
            if (existing == null) {
                existing = fragmentClass.newInstance();
            }
            if (existing instanceof PartyFragment) {
                this.partyFragment = (PartyFragment) existing;
            }
            fragmentTransaction.replace(R.id.fragment_container, existing, fragmentTag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
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
        //disconnect.setVisible(userViewModel.getConnected().getValue() != null);
        //profile.setVisible(userViewModel.getConnected().getValue() != null);
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
                this.setFragment(AccountFragment.class, "account");
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

    public void userIsConnected(User connected) {
        TextView userText = toolbar.findViewById(R.id.lbl_display_name);
        if (connected != null) {
            if (TextUtils.isEmpty(connected.getDisplayName())) {
                setFragment(UsernameFragment.class, "username");
            } else {
                userText.setText(connected.getDisplayName());
                setFragment(HomeFragment.class, "home");
                Toast.makeText(this, R.string.connected, Toast.LENGTH_LONG).show();
            }
        } else {
            setFragment(AccountFragment.class, "account");
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        PartyFragment fragment = (PartyFragment) manager.findFragmentByTag(FragmentTag.PARTY);
        if (fragment != null && fragment.isVisible()) {
            fragment.confirmExit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void exit() {
        finish();
    }

    @Override
    protected void onDestroy() {
        if(partyFragment != null) {
            this.partyFragment.exit();
        }
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}