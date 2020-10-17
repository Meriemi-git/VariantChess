package fr.aboucorp.variantchess.app.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.heroiclabs.nakama.api.Notification;
import com.heroiclabs.nakama.api.NotificationList;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.db.entities.VariantUser;
import fr.aboucorp.variantchess.app.db.viewmodel.VariantUserViewModel;
import fr.aboucorp.variantchess.app.multiplayer.NakamaManager;
import fr.aboucorp.variantchess.app.multiplayer.listeners.NotificationListener;
import fr.aboucorp.variantchess.app.utils.AsyncHandler;
import fr.aboucorp.variantchess.app.utils.JsonExtractor;
import fr.aboucorp.variantchess.app.utils.VariantVars;
import fr.aboucorp.variantchess.app.views.fragments.AuthentFragmentDirections;
import fr.aboucorp.variantchess.app.views.fragments.GameRulesFragmentDirections;
import fr.aboucorp.variantchess.app.views.fragments.SettingsFragmentDirections;

public class MainActivity extends DaggerAppCompatActivity implements NotificationListener, AndroidFragmentApplication.Callbacks {
    public static final String SHARED_PREFERENCE_NAME = "nakama";
    private Toolbar toolbar;
    @Inject
    public NakamaManager nakamaManager;
    //@Inject
    public SharedPreferences sharedPreferences;
    private VariantUserViewModel variantUserViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        this.sharedPreferences = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.setContentView(R.layout.main_layout);
        this.setToolbar();
        this.nakamaManager.setNotificationListener(this);
        variantUserViewModel = new ViewModelProvider(this).get(VariantUserViewModel.class);
        manageUserConnection();
        this.variantUserViewModel.getConnected().observe(this, connected -> {
            if (connected != null) {
                this.sharedPreferences.edit().putString("nakama.authToken", connected.authToken).apply();
            } else {
                this.sharedPreferences.edit().putString("nakama.authToken", null).apply();
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
                if (!authToken.equals(this.nakamaManager.getSession().getAuthToken())) {
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
                this.nakamaManager.disconnect();
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
                String authToken = sharedPreferences.getString("nakama.authToken", null);
                if (!TextUtils.isEmpty(authToken)) {
                    VariantUser variantUser = nakamaManager.tryReconnectUser(authToken);
                    return variantUser;
                }
                return null;
            }

            @Override
            protected void callbackOnUI(Object arg) {
                super.callbackOnUI(arg);
                VariantUser variantUser = (VariantUser) arg;
                getSupportActionBar().show();
                if (variantUser != null) {
                    Toast.makeText(MainActivity.this, R.string.connected, Toast.LENGTH_LONG).show();
                    NavDirections action = GameRulesFragmentDirections.actionGlobalGameRulesFragment(variantUser);
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
        this.variantUserViewModel.getConnected().observe(this, MainActivity.this::updateConnectionUI);
        return true;
    }


    private void updateConnectionUI(VariantUser connected) {
        if (connected != null) {
            this.sharedPreferences.edit().putString("nakama.authToken", connected.authToken).apply();
            this.toolbar.setSubtitle(connected.username);
        } else {
            this.sharedPreferences.edit().putString("nakama.authToken", null).apply();
            this.toolbar.setSubtitle("Disconnected");
        }
        MenuItem disconnect = this.toolbar.getMenu().findItem(R.id.menu_action_disconnect);
        MenuItem profile = this.toolbar.getMenu().findItem(R.id.menu_action_profil);
        disconnect.setVisible(connected != null);
        profile.setVisible(connected != null);
    }

    private void disconnectUser() {
        nakamaManager.disconnect();
        this.variantUserViewModel.disconnectUser();
        this.sharedPreferences.edit().putString("nk.authToken", null).apply();
        NavDirections action = AuthentFragmentDirections.actionGlobalAuthentFragment();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(action);
    }

    @Override
    public void exit() {

    }
}