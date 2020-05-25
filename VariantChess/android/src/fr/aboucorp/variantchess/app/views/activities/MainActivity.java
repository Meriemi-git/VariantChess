package fr.aboucorp.variantchess.app.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.heroiclabs.nakama.api.User;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.parcelables.MatchP;
import fr.aboucorp.variantchess.app.utils.FragmentTag;
import fr.aboucorp.variantchess.app.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.views.fragments.AccountFragment;
import fr.aboucorp.variantchess.app.views.fragments.HomeFragment;
import fr.aboucorp.variantchess.app.views.fragments.MatchFragment;
import fr.aboucorp.variantchess.app.views.fragments.SettingsFragment;
import fr.aboucorp.variantchess.app.views.fragments.UsernameFragment;
import fr.aboucorp.variantchess.entities.Match;

import static fr.aboucorp.variantchess.app.multiplayer.SessionManager.SHARED_PREFERENCE_NAME;

public class MainActivity extends VariantChessActivity implements AndroidFragmentApplication.Callbacks {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_layout);
        this.setToolbar();
        this.sessionManager = SessionManager.getInstance(this);
        SharedPreferences pref = this.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        User user;
        try {
            user = this.sessionManager.restoreSessionIfPossible(pref);
            this.userIsConnected(user);
        } catch (Exception e) {
            Log.e("fr.aboucorp.variantchess", e.getMessage());
            e.printStackTrace();
        }
    }

    private void setToolbar() {
        this.toolbar = this.findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(this.getString(R.string.app_name));
        this.toolbar.setSubtitle("Disconnected");
        this.setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationOnClickListener(v -> this.onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
    }

    private void updateConnectionUI(User user) {
        if(user != null){
            this.toolbar.setSubtitle(user.getDisplayName());
        }else{
            this.toolbar.setSubtitle("Disconnected");
        }
        MenuItem disconnect = this.toolbar.getMenu().findItem(R.id.menu_action_disconnect);
        MenuItem profile = this.toolbar.getMenu().findItem(R.id.menu_action_profil);
        disconnect.setVisible(user != null);
        profile.setVisible(user != null);
    }

    @Override
    public void setFragment(Class<? extends Fragment> fragmentClass, String fragmentTag, Bundle args) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        Fragment existing = this.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        try {
            if (existing == null) {
                existing = fragmentClass.newInstance();
            }
            if (args != null) {
                existing.setArguments(args);
            }
            fragmentTransaction.replace(R.id.fragment_container, existing, fragmentTag);
            fragmentTransaction.addToBackStack(fragmentTag);
            fragmentTransaction.commit();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void userIsConnected(User connected) {
        if (connected != null) {
            if (TextUtils.isEmpty(connected.getDisplayName())) {
                this.setFragment(UsernameFragment.class, "username", null);
            } else {
                userViewModel.setConnected(connected);
                this.setFragment(HomeFragment.class, "home", null);
                Toast.makeText(this, R.string.connected, Toast.LENGTH_LONG).show();
            }
        } else {
            this.setFragment(AccountFragment.class, "account", null);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        MatchFragment fragment = (MatchFragment) this.getSupportFragmentManager().findFragmentByTag(FragmentTag.MATCH);
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            Bundle args = new Bundle();
            MatchP matchP = fragment.getMatchP();
            args.putParcelable("matchP", matchP);
            fragmentTransaction.remove(fragment).commit();
            fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, MatchFragment.class, args, FragmentTag.MATCH).commit();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        userViewModel.getConnected().observe(this, connected -> MainActivity.this.updateConnectionUI(connected));
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
                return true;
            case R.id.menu_action_disconnect:
                this.sessionManager.destroySession();
                this.userViewModel.setConnected(null);
                this.setFragment(AccountFragment.class, "account", null);
                return true;
            case R.id.menu_action_settings:
                setFragment(SettingsFragment.class, FragmentTag.SETTINGS, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = this.getSupportFragmentManager();
        MatchFragment matchFragment = (MatchFragment) manager.findFragmentByTag(FragmentTag.MATCH);
        if (matchFragment != null && matchFragment.isVisible()) {
            matchFragment.confirmExit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void exit() {
        this.finish();
    }


}