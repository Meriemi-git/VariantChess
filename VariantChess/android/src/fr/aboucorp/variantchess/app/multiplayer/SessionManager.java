package fr.aboucorp.variantchess.app.multiplayer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.DefaultSession;
import com.heroiclabs.nakama.PermissionRead;
import com.heroiclabs.nakama.PermissionWrite;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.StorageObjectWrite;
import com.heroiclabs.nakama.api.StorageObjectAcks;
import com.heroiclabs.nakama.api.User;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.utils.SignType;
import fr.aboucorp.variantchess.app.views.activities.MainActivity;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

public class SessionManager {
    public static final String SHARED_PREFERENCE_NAME = "nakama";
    private static SessionManager INSTANCE;
    private GoogleSignInClient googleSignInClient;
    private final MainActivity activity;
    public final Client client = new DefaultClient("defaultkey", "192.168.1.37", 7349, false);
    public Session session;

    private SessionManager(MainActivity activity) {
        this.activity = activity;
        String appId = activity.getResources().getString(R.string.server_client_id);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(appId)
                .requestEmail()
                .requestProfile()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public User authentWithEmail(String mail, String password, int signType) throws ExecutionException, InterruptedException {
        Metadata metadata = new Metadata();
        User connected;
        SharedPreferences pref = activity.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (signType == SignType.SIGNUP) {
            metadata.values.put("signType", "SIGNIN");
            connected = restoreSessionIfPossible(pref);
            if (connected == null) {
                this.session = client.authenticateEmail(mail, password, false).get();
                connected = this.client.getAccount(session).get().getUser();
            }
        } else {
            metadata.values.put("signType", "SIGNIN");
            this.session = client.authenticateEmail(mail, password, metadata.values).get();
            connected = this.client.getAccount(session).get().getUser();
        }
        pref.edit().putString("nk.session", session.getAuthToken()).apply();
        return connected;
    }


    public User authentWithGoogle(String idToken, String mail, int signType) throws ExecutionException, InterruptedException {
        Metadata metadata = new Metadata();
        metadata.values.put("mail", mail);
        User connected;
        SharedPreferences pref = activity.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (signType == SignType.SIGNUP) {
            metadata.values.put("signType", "SIGNUP");
            connected = restoreSessionIfPossible(pref);
            if (connected == null) {
                this.session = client.authenticateGoogle(idToken, false, "", metadata.values).get();
                connected = this.client.getAccount(session).get().getUser();
            }
        } else {
            metadata.values.put("signType", "SIGNIN");
            this.session = client.authenticateGoogle(idToken, metadata.values).get();
            registerGoogleMail(mail);
            connected = this.client.getAccount(session).get().getUser();
        }
        pref.edit().putString("nk.session", session.getAuthToken()).apply();
        return connected;
    }

    private void registerGoogleMail(String mail) throws ExecutionException, InterruptedException {
        String json = "{\"mail\":\"" + mail + "\"}";
        StorageObjectWrite googleMailObject = new StorageObjectWrite("user_infos", "mail", json, PermissionRead.OWNER_READ, PermissionWrite.OWNER_WRITE);
        StorageObjectAcks acks = client.writeStorageObjects(session, googleMailObject).get();
    }

    public User restoreSessionIfPossible(SharedPreferences pref) throws InterruptedException {
        // Lets check if we can restore a cached session.
        String sessionString = pref.getString("nk.session", null);
        if (sessionString != null && !sessionString.isEmpty()) {
            Session restoredSession = DefaultSession.restore(sessionString);
            if (!restoredSession.isExpired(new Date())) {
                // Session was valid and is restored now.
                this.session = restoredSession;
                try {
                    return this.client.getAccount(session).get().getUser();
                } catch (ExecutionException e) {
                    Log.i("fr.aboucorp.variantchess", e.getMessage());
                    return null;
                }
            }
        }
        return null;
    }

    public static SessionManager getInstance(MainActivity activity) {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager(activity);
        }
        return INSTANCE;
    }

    public void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, SignType.SIGNIN);
    }

    public void handleSignInWithGoogleResult(Task<GoogleSignInAccount> task) {
        GoogleSignInAccount account = null;
        try {
            account = task.getResult(ApiException.class);

            com.heroiclabs.nakama.api.User nakamaUser = authentWithGoogle(account.getIdToken(), account.getEmail(), SignType.SIGNIN);
            activity.userIsConnected(nakamaUser);
        } catch (ApiException e) {
            Log.e("fr.aboucorp.variantchess", "Exception message=" + e.getMessage());
            Toast.makeText(activity, R.string.failed_login, Toast.LENGTH_LONG).show();
        } catch (InterruptedException e) {
            Log.e("fr.aboucorp.variantchess", "Exception message=" + e.getMessage());
            Toast.makeText(activity, R.string.failed_login, Toast.LENGTH_LONG).show();
        } catch (ExecutionException e) {
            Log.e("fr.aboucorp.variantchess", "Exception message=" + e.getMessage());
            if (e.getCause() instanceof StatusRuntimeException) {
                Status.Code code = ((StatusRuntimeException) e.getCause()).getStatus().getCode();
                if (code == Status.Code.UNAUTHENTICATED) {
                    Toast.makeText(activity, "Can link account", Toast.LENGTH_LONG).show();

                } else if (code == Status.Code.ALREADY_EXISTS) {
                    Toast.makeText(activity, "Google account already exists", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, R.string.failed_login, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void signInWithEmail(String mail, String password, String username) throws ExecutionException, InterruptedException {
        authentWithEmail(mail, password, SignType.SIGNIN);
        client.updateAccount(this.session, username);
        User connected = this.client.getAccount(this.session).get().getUser();
        activity.userIsConnected(connected);
    }

    public void signUpWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, SignType.SIGNUP);
    }

    public void handleSignUpWithGoogleResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            com.heroiclabs.nakama.api.User nakamaUser = authentWithGoogle(account.getIdToken(), account.getEmail(), SignType.SIGNUP);
            activity.userIsConnected(nakamaUser);
        } catch (Exception e) {
            Toast.makeText(activity, R.string.failed_login, Toast.LENGTH_LONG).show();
            Log.e("fr.aboucorp.variantchess", "Exception message=" + e.getMessage());
        }
    }

    public void signUpWithEmail(String mail, String password) {
        User user = null;
        try {
            user = authentWithEmail(mail, password, SignType.SIGNUP);
            activity.userIsConnected(user);
        } catch (Exception e) {
            Toast.makeText(activity, R.string.failed_login, Toast.LENGTH_LONG).show();
            Log.e("fr.aboucorp.variantchess", "Exception message=" + e.getMessage());
        }

    }

    public void destroySession() {
    }

    public User makeJobOnSession() throws ExecutionException, InterruptedException {
        return this.client.getAccount(session).get().getUser();
    }
}
