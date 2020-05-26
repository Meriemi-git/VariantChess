package fr.aboucorp.variantchess.app.multiplayer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.DefaultSession;
import com.heroiclabs.nakama.MatchmakerTicket;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.api.Rpc;
import com.heroiclabs.nakama.api.User;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import fr.aboucorp.variantchess.R;
import fr.aboucorp.variantchess.app.exceptions.UsernameDuplicateException;
import fr.aboucorp.variantchess.app.utils.ExceptionCauseCode;
import fr.aboucorp.variantchess.app.utils.ResultType;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;
import fr.aboucorp.variantchess.entities.GameMode;

public class SessionManager {
    public static final String SHARED_PREFERENCE_NAME = "nakama";
    private static SessionManager INSTANCE;
    private final Activity activity;
    private final Client client = new DefaultClient("defaultkey", "192.168.1.37", 7349, false);
    private Session session;
    private User user;

    private SessionManager(Activity activity) {
        this.activity = activity;
    }

    private void authentWithEmail(String mail, String password, int signType) throws ExecutionException, InterruptedException {
        Metadata<String> metadata = new Metadata();

        SharedPreferences pref = this.activity.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (signType == ResultType.SIGNUP) {
            metadata.put("signType", "SIGNUP");
            this.restoreSessionIfPossible(pref);
            if (this.user == null) {
                this.session = this.client.authenticateEmail(mail, password, false).get();
                this.user = this.client.getAccount(this.session).get().getUser();
            }
        } else {
            metadata.put("signType", "SIGNIN");
            this.session = this.client.authenticateEmail(mail, password, metadata).get();
            this.user = this.client.getAccount(this.session).get().getUser();
        }
        pref.edit().putString("nk.session", this.session.getAuthToken()).apply();
    }


    public void restoreSessionIfPossible(SharedPreferences pref) throws InterruptedException {
        // Lets check if we can restore a cached session.
        String sessionString = pref.getString("nk.session", null);
        if (sessionString != null && !sessionString.isEmpty()) {
            Session restoredSession = DefaultSession.restore(sessionString);
            if (!restoredSession.isExpired(new Date())) {
                // Session was valid and is restored now.
                this.session = restoredSession;
                try {
                    this.user =  this.client.getAccount(this.session).get().getUser();
                } catch (ExecutionException e) {
                    Log.i("fr.aboucorp.variantchess", e.getMessage());
                }
            }
        }
    }

    public static SessionManager getInstance(Activity activity) {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager(activity);
        }
        return INSTANCE;
    }

    private boolean userExist(String email, boolean searchGoogleAccount) {
        Metadata metadata = new Metadata();
        metadata.put("email",email);
        metadata.put("searchGoogleAccount",Boolean.toString(searchGoogleAccount));
        String rpcid = "user_exists";
        Rpc userExistsRpc = null;
        try {
            userExistsRpc = this.client.rpc(this.session, rpcid, metadata.getJsonFromMetadata()).get();
        } catch (ExecutionException e) {
            if(ExceptionCauseCode.getCodeValueFromCause(e.getCause()) == ExceptionCauseCode.ALREADY_EXISTS){
                Toast.makeText(this.activity, R.string.username_already_exists, Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("fr.aboucorp.variantchess",String.format("UserExists return: %s", userExistsRpc.getPayload()));
        return Boolean.parseBoolean(userExistsRpc.getPayload());
    }

    public void signInWithEmail(String mail, String password) throws ExecutionException, InterruptedException {
        this.authentWithEmail(mail, password, ResultType.SIGNIN);
        User connected = this.client.getAccount(this.session).get().getUser();
        ((VariantChessActivity)this.activity).userIsConnected(connected);
    }

    public void signUpWithEmail(String mail, String password) {
        try {
            this.authentWithEmail(mail, password, ResultType.SIGNUP);
            ((VariantChessActivity)this.activity).userIsConnected(this.user);
        } catch (Exception e) {
            Toast.makeText(this.activity, R.string.failed_login, Toast.LENGTH_LONG).show();
            Log.e("fr.aboucorp.variantchess", "Exception message=" + e.getMessage());
        }
    }

    public void destroySession() {
        this.client.disconnect();
        SharedPreferences pref = this.activity.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString("nk.session", null).apply();
    }

    public User updateDisplayName(String displayName) throws UsernameDuplicateException {
        Metadata data = new Metadata();
        String timeZone = TimeZone.getDefault().getDisplayName();
        String langTag = Locale.getDefault().getDisplayLanguage();
        data.put("displayName",displayName);
        data.put("timeZone",timeZone);
        data.put("langTag",langTag);
        String rpcid = "update_user_infos";
        try {
            Rpc call = this.client.rpc(this.session, rpcid, data.getJsonFromMetadata()).get();
            return this.client.getAccount(this.session).get().getUser();
        } catch (ExecutionException e) {
            if(ExceptionCauseCode.getCodeValueFromCause(e.getCause()) == ExceptionCauseCode.ALREADY_EXISTS){
               throw new UsernameDuplicateException(this.activity.getString(R.string.username_already_exists));
            }
        } catch (InterruptedException e) {
            Log.e("fr.aboucorp.variantchess","Erro during updating account");
            return null;
        }
        return null;
    }

    public void launchMatchMaking(GameMode gamemode, MatchListener matchListener) throws ExecutionException, InterruptedException {
        SharedPreferences pref = this.activity.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SocketClient matchmakingSocket = this.client.createSocket();
        MatchMakingSocketListener socketListener = new MatchMakingSocketListener(matchListener);
        matchmakingSocket.connect(this.session, socketListener).get();
        String ticketString = pref.getString("nk.ticket", null);
        if ( !TextUtils.isEmpty(ticketString) ) {
            try {
                matchmakingSocket.removeMatchmaker(ticketString).get();
            }catch(ExecutionException | InterruptedException e)       {
                Log.i("fr.aboucorp.variantchess","Cannot remove matchmaking");
            }
        }
        Metadata<String> stringProps = new Metadata<>();
        Metadata<Double> numProps = new Metadata<>();
        /*Locale current = Configuration.getLocales(activity.getResources().getConfiguration()).get(0);
        stringProps.put("locale",current.getCountry());*/
        stringProps.put("gamemode",gamemode.getName());
        numProps.put("rank",1.0);
        String query = "*";
        int minCount = 2;
        int maxCount = 2;
        MatchmakerTicket matchmakerTicket = matchmakingSocket.addMatchmaker(
                minCount, maxCount,query, stringProps, numProps).get();
        pref.edit().putString("nk.ticket", matchmakerTicket.getTicket()).apply();
    }

    public User getUser() {
        return this.user;
    }
}


