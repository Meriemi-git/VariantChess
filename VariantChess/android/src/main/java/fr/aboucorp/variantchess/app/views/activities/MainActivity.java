package fr.aboucorp.variantchess.app.views.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.heroiclabs.nakama.api.Notification;
import com.heroiclabs.nakama.api.NotificationList;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.multiplayer.listeners.NotificationListener;
import fr.aboucorp.variantchess.app.utils.JsonExtractor;
import fr.aboucorp.variantchess.app.utils.VariantVars;
import fr.aboucorp.variantchess.app.views.fragments.AuthentFragmentDirections;
import fr.aboucorp.variantchess.app.views.fragments.SettingsFragmentDirections;

public class MainActivity extends AppCompatActivity implements NotificationListener {
    public static final String SHARED_PREFERENCE_NAME = "nakama";
    private Toolbar toolbar;
    private SessionManager sessionManager;
    private UserViewModel userViewModel;
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.pref = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.setContentView(R.layout.main_layout);
        this.setToolbar();
        this.sessionManager = SessionManager.getInstance();
        this.sessionManager.setNotificationListener(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        this.userViewModel.getConnected().observe(this, connected -> this.displayConnectedUser(connected));
        manageUserConnection();
    }

    @Override
    public void onNotifications(NotificationList notifications) {
        Log.i("fr.aboucorp.variantchess", "onNotifications " + notifications.getNotificationsCount());
        for (Notification notification : notifications.getNotificationsList()
        ) {
            if (notification.getCode() == 666) {
                String authToken = JsonExtractor.ectractAttributeByName(notification.getContent(), VariantVars.VARIANT_CHESS_TOKEN);
                if (!authToken.equals(this.sessionManager.getSession().getAuthToken())) {
                    Log.i("fr.aboucorp.variantchess", "Disconnection of user with sessionID : " + authToken);
                    sessionManager.disconnect();
                    displayConnectedUser(null);
                    this.pref.edit().putString("nk.authToken", null).apply();
                    NavDirections action = AuthentFragmentDirections.actionGlobalAuthentFragment();
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(action);
                }
            }
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

    private void manageUserConnection() {
        ChessUser chessUser = null;
        String authToken = this.pref.getString("nakama.authToken", null);
        if (!TextUtils.isEmpty(authToken)) {
            chessUser = this.sessionManager.tryReconnectUser(authToken);
        }
        NavHostFragment navHost = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHost.getNavController();
        NavInflater navInflater = navController.getNavInflater();
        NavGraph graph = navInflater.inflate(R.navigation.nav_graph);
        if (chessUser == null) {
            graph.setStartDestination(R.id.authentFragment);
        } else {
            graph.setStartDestination(R.id.gameRulesFragment);
        }
        navController.setGraph(graph);
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

    public void displayConnectedUser(ChessUser connected) {
        if (connected != null) {
            Toast.makeText(this, R.string.connected, Toast.LENGTH_LONG).show();
            this.pref.edit().putString("nakama.authToken", connected.authToken).apply();
        } else {
            this.pref.edit().putString("nakama.authToken", null).apply();
        }
    }
}