package fr.aboucorp.variantchess.app.multiplayer;

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
import fr.aboucorp.variantchess.app.exception.UsernameDuplicateException;
import fr.aboucorp.variantchess.app.utils.ExceptionCauseCode;
import fr.aboucorp.variantchess.app.utils.ResultType;
import fr.aboucorp.variantchess.app.views.activities.VariantChessActivity;
import fr.aboucorp.variantchess.entities.GameMode;

public class SessionManager {
    public static final String SHARED_PREFERENCE_NAME = "nakama";
    private static SessionManager INSTANCE;
    private final VariantChessActivity activity;
    public final Client client = new DefaultClient("defaultkey", "192.168.1.37", 7349, false);
    public Session session;

    private SessionManager(VariantChessActivity activity) {
        this.activity = activity;
    }

    public User authentWithEmail(String mail, String password, int signType) throws ExecutionException, InterruptedException {
        Metadata<String> metadata = new Metadata();
        User connected;
        SharedPreferences pref = activity.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (signType == ResultType.SIGNUP) {
            metadata.put("signType", "SIGNUP");
            connected = restoreSessionIfPossible(pref);
            if (connected == null) {
                this.session = client.authenticateEmail(mail, password, false).get();
                connected = this.client.getAccount(session).get().getUser();
            }
        } else {
            metadata.put("signType", "SIGNIN");
            this.session = client.authenticateEmail(mail, password, metadata).get();
            connected = this.client.getAccount(session).get().getUser();
        }
        pref.edit().putString("nk.session", session.getAuthToken()).apply();
        return connected;
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

    public static SessionManager getInstance(VariantChessActivity activity) {
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
            userExistsRpc = client.rpc(session, rpcid, metadata.getJsonFromMetadata()).get();
        } catch (ExecutionException e) {
            if(ExceptionCauseCode.getCodeValueFromCause(e.getCause()) == ExceptionCauseCode.ALREADY_EXISTS){
                Toast.makeText(activity, R.string.username_already_exists, Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i("fr.aboucorp.variantchess",String.format("UserExists return: %s", userExistsRpc.getPayload()));
        return Boolean.parseBoolean(userExistsRpc.getPayload());
    }

    public void signInWithEmail(String mail, String password) throws ExecutionException, InterruptedException {
        authentWithEmail(mail, password, ResultType.SIGNIN);
        User connected = this.client.getAccount(this.session).get().getUser();
        activity.userIsConnected(connected);
    }

    public void signUpWithEmail(String mail, String password) {
        User user = null;
        try {
            user = authentWithEmail(mail, password, ResultType.SIGNUP);
            activity.userIsConnected(user);
        } catch (Exception e) {
            Toast.makeText(activity, R.string.failed_login, Toast.LENGTH_LONG).show();
            Log.e("fr.aboucorp.variantchess", "Exception message=" + e.getMessage());
        }
    }

    public void destroySession() {

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
            Rpc call = client.rpc(session, rpcid, data.getJsonFromMetadata()).get();
            return this.client.getAccount(session).get().getUser();
        } catch (ExecutionException e) {
            if(ExceptionCauseCode.getCodeValueFromCause(e.getCause()) == ExceptionCauseCode.ALREADY_EXISTS){
               throw new UsernameDuplicateException(activity.getString(R.string.username_already_exists));
            }
        } catch (InterruptedException e) {
            Log.e("fr.aboucorp.variantchess","Erro during updating account");
            return null;
        }
        return null;
    }

    public void launchMatchMaking(GameMode gamemode, MatchListener matchListener) throws ExecutionException, InterruptedException {
        SharedPreferences pref = activity.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SocketClient matchmakingSocket = client.createSocket();
        MatchMakingSocketListener socketListener = new MatchMakingSocketListener(matchListener);
        matchmakingSocket.connect(session, socketListener).get();
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
}


