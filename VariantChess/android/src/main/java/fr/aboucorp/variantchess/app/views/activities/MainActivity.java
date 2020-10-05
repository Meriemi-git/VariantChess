package fr.aboucorp.variantchess.app.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.views.fragments.AuthentFragmentDirections;
import fr.aboucorp.variantchess.app.views.fragments.SettingsFragmentDirections;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_layout);
        this.setToolbar();
        this.sessionManager = SessionManager.getInstance(this);
        this.userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        try {
            ChessUser user = this.sessionManager.tryReconnectUser();
            this.userIsConnected(user);
            NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            NavController navController = navHost.getNavController();

            NavInflater navInflater = navController.getNavInflater();
            NavGraph graph = navInflater.inflate(R.navigation.nav_graph);
            if (user != null) {
                graph.setStartDestination(R.id.gameRulesFragment);
            } else {
                graph.setStartDestination(R.id.authentFragment);
            }
            navController.setGraph(graph);
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
        NavDirections action;
        switch (item.getItemId()) {
            case R.id.menu_action_profil:
                return true;
            case R.id.menu_action_disconnect:
                this.sessionManager.disconnect();
                this.userViewModel.disconnectUser();
                action = AuthentFragmentDirections.actionGlobalAuthentFragment();
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(action);
                return true;
            case R.id.menu_action_settings:
                action = SettingsFragmentDirections.actionGlobalSettingsFragment();
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(action);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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