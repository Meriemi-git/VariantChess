package fr.aboucorp.variantchess.app.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.entities.UserViewModel;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.utils.FragmentTag;
import fr.aboucorp.variantchess.app.views.fragments.AuthentFragment;
import fr.aboucorp.variantchess.app.views.fragments.SettingsFragment;

import static fr.aboucorp.variantchess.app.utils.ArgsKey.CHESS_USER;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private UserViewModel userViewModel;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_layout);
        this.setToolbar();
        this.sessionManager = SessionManager.getInstance(this);
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        try {
            this.sessionManager.tryReconnectUser();
            this.userIsConnected(this.sessionManager.getChessUser());
            this.setFragment(AuthentFragment.class, FragmentTag.AUTHENT, null);
        } catch (Exception e) {
            Log.e("fr.aboucorp.variantchess", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.userViewModel.getConnected().observe(this, MainActivity.this::updateConnectionUI);
        MenuItem disconnect = this.toolbar.getMenu().findItem(R.id.menu_action_disconnect);
        MenuItem profile = this.toolbar.getMenu().findItem(R.id.menu_action_profil);
        disconnect.setVisible(this.userViewModel.getConnected().getValue() != null);
        profile.setVisible(this.userViewModel.getConnected().getValue() != null);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_profil:
                return true;
            case R.id.menu_action_disconnect:
                this.userViewModel.disconnectUser();
                this.sessionManager.destroySession();
                this.userViewModel.setConnected(null);
                this.setFragment(AuthentFragment.class, FragmentTag.AUTHENT, null);
                return true;
            case R.id.menu_action_settings:
                this.setFragment(SettingsFragment.class, FragmentTag.SETTINGS, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setFragment(Class<? extends Fragment> fragmentClass, String fragmentTag, Bundle args) {
        FragmentTransaction fragmentTransaction = this.getSupportFragmentManager().beginTransaction();
        Fragment existing = this.getSupportFragmentManager().findFragmentByTag(fragmentTag);
        try {
            if (existing == null) {
                existing = fragmentClass.newInstance();
            }
            if (args != null) {
                args.putSerializable(CHESS_USER, this.userViewModel.getConnected().getValue());
                existing.setArguments(args);
            }
            fragmentTransaction.replace(R.id.fragment_container, existing, fragmentTag);
            fragmentTransaction.commit();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void userIsConnected(ChessUser connected) {
        if (connected != null) {
            Toast.makeText(this, R.string.connected, Toast.LENGTH_LONG).show();
        }
        this.userViewModel.setConnected(connected);
    }

    private void setToolbar() {
        this.toolbar = this.findViewById(R.id.main_toolbar);
        this.toolbar.setTitle(this.getString(R.string.app_name));
        this.toolbar.setSubtitle("Disconnected");
        this.setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationOnClickListener(v -> this.onBackPressed());
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void updateConnectionUI(ChessUser user) {
        if (user != null) {
            this.toolbar.setSubtitle(user.username);

        } else {
            this.toolbar.setSubtitle("Disconnected");
        }
        MenuItem disconnect = this.toolbar.getMenu().findItem(R.id.menu_action_disconnect);
        MenuItem profile = this.toolbar.getMenu().findItem(R.id.menu_action_profil);
        disconnect.setVisible(user != null);
        profile.setVisible(user != null);
    }

}