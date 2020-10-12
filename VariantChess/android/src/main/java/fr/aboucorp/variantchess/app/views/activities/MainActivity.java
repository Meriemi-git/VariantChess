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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.heroiclabs.nakama.api.Notification;
import com.heroiclabs.nakama.api.NotificationList;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.db.viewmodel.UserViewModel;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.app.multiplayer.listeners.NotificationListener;
import fr.aboucorp.variantchess.app.utils.AsyncHandler;
import fr.aboucorp.variantchess.app.utils.JsonExtractor;
import fr.aboucorp.variantchess.app.utils.VariantVars;
import fr.aboucorp.variantchess.app.views.fragments.AuthentFragmentDirections;
import fr.aboucorp.variantchess.app.views.fragments.GameRulesFragmentDirections;
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
        manageUserConnection();
        this.userViewModel.getConnected().observe(this, connected -> {
            if (connected != null) {
                this.pref.edit().putString("nakama.authToken", connected.authToken).apply();
            } else {
                this.pref.edit().putString("nakama.authToken", null).apply();
            }
        });
    }

    @Override
    public void onNotifications(NotificationList notifications) {
        Log.i("fr.aboucorp.variantchess", "onNotifications " + notifications.getNotificationsCount());
        for (Notification notification : notifications.getNotificationsList()
        ) {
            if (notification.getCode() == 666) {
                String authToken = JsonExtractor.ectractAttributeByName(notification.getContent(), VariantVars.VARIANT_CHESS_TOKEN);
                if (!authToken.equals(this.sessionManager.getSession().getAuthToken())) {
                    Log.i("fr.aboucorp.variantchess", "Disconnection of user with authToken : " + authToken);
                    disconnectUser();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        NavDirections action;
        switch (item.getItemId()) {
            case R.id.menu_action_profil:
                return true;
            case R.id.menu_action_disconnect:
                this.sessionManager.disconnect();
                disconnectUser();
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
        getSupportActionBar().hide();
        AsyncHandler asyncHandler = new AsyncHandler() {
            @Override
            protected Object executeAsync() throws Exception {
                String authToken = pref.getString("nakama.authToken", null);
                if (!TextUtils.isEmpty(authToken)) {
                    ChessUser chessUser = sessionManager.tryReconnectUser(authToken);
                    return chessUser;
                }
                return null;
            }

            @Override
            protected void callbackOnUI(Object arg) {
                super.callbackOnUI(arg);
                ChessUser chessUser = (ChessUser) arg;
                getSupportActionBar().show();
                if (chessUser != null) {
                    Toast.makeText(MainActivity.this, R.string.connected, Toast.LENGTH_LONG).show();
                    NavDirections action = GameRulesFragmentDirections.actionGlobalGameRulesFragment(chessUser);
                    Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(action);
                } else {
                    NavDirections action = GameRulesFragmentDirections.actionGlobalAuthentFragment();
                    Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(action);
                }

            }

            @Override
            protected void error(Exception ex) {
                super.error(ex);
                disconnectUser();
                getSupportActionBar().show();
                NavDirections action = GameRulesFragmentDirections.actionGlobalAuthentFragment();
                Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment).navigate(action);
                Toast.makeText(MainActivity.this, R.string.err_reconnexion_error, Toast.LENGTH_LONG).show();
            }
        };
        asyncHandler.start();
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.userViewModel.getConnected().observe(this, MainActivity.this::updateConnectionUI);
        return true;
    }


    private void updateConnectionUI(ChessUser connected) {
        if (connected != null) {
            this.pref.edit().putString("nakama.authToken", connected.authToken).apply();
            this.toolbar.setSubtitle(connected.username);
        } else {
            this.pref.edit().putString("nakama.authToken", null).apply();
            this.toolbar.setSubtitle("Disconnected");
        }
        MenuItem disconnect = this.toolbar.getMenu().findItem(R.id.menu_action_disconnect);
        MenuItem profile = this.toolbar.getMenu().findItem(R.id.menu_action_profil);
        disconnect.setVisible(connected != null);
        profile.setVisible(connected != null);
    }

    private void disconnectUser() {
        sessionManager.disconnect();
        this.userViewModel.disconnectUser();
        this.pref.edit().putString("nk.authToken", null).apply();
        NavDirections action = AuthentFragmentDirections.actionGlobalAuthentFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(action);
    }
}