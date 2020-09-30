package fr.aboucorp.variantchess.app.multiplayer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.DefaultSession;
import com.heroiclabs.nakama.Match;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.MatchmakerTicket;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.api.Rpc;
import com.heroiclabs.nakama.api.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import fr.aboucorp.variantchess.app.db.dto.ChessUserDto;
import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.exceptions.AuthentificationException;
import fr.aboucorp.variantchess.app.exceptions.IncorrectCredentials;
import fr.aboucorp.variantchess.app.exceptions.MailAlreadyRegistered;
import fr.aboucorp.variantchess.app.exceptions.UsernameAlreadyRegistered;
import fr.aboucorp.variantchess.app.utils.ExceptionCauseCode;
import fr.aboucorp.variantchess.app.utils.ResultType;
import fr.aboucorp.variantchess.entities.GameMode;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;

public class SessionManager {
    public static final String SHARED_PREFERENCE_NAME = "nakama";
    private static SessionManager INSTANCE;
    private final Client client = new DefaultClient("defaultkey", "192.168.1.37", 7349, false);
    private Session session;
    private User user;
    private SocketClient matchmakingSocket;
    private SharedPreferences pref;
    private MatchMakingSocketListener socketListener;

    private SessionManager(Activity activity) {
        this.pref = activity.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static SessionManager getInstance(Activity activity) {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager(activity);
        }
        return INSTANCE;
    }

    private boolean userExist(String email, boolean searchGoogleAccount) throws InterruptedException, ExecutionException, TimeoutException {
        Metadata metadata = new Metadata();
        metadata.put("email", email);
        metadata.put("searchGoogleAccount", Boolean.toString(searchGoogleAccount));
        String rpcid = "user_exists";
        Rpc userExistsRpc = null;
        userExistsRpc = this.client.rpc(this.session, rpcid, metadata.getJsonFromMetadata()).get(5000, TimeUnit.MILLISECONDS);
        return Boolean.parseBoolean(userExistsRpc.getPayload());
    }

    public ChessUser signInWithEmail(String mail, String password) throws AuthentificationException {
        return ChessUserDto.fromUserToChessUser(this.authentWithEmail(mail, password, null, ResultType.SIGNIN));
    }

    public ChessUser signUpWithEmail(String mail, String password, String displayName) throws AuthentificationException {
        return ChessUserDto.fromUserToChessUser(this.authentWithEmail(mail, password, displayName, ResultType.SIGNUP));
    }

    private User authentWithEmail(String mail, String password, String username, int signType) throws AuthentificationException {
        if (signType == ResultType.SIGNUP) {
            try {
                this.session = this.client.authenticateEmail(mail, password, true, username).get(2000, TimeUnit.MILLISECONDS);
            } catch (ExecutionException e) {
                if (ExceptionCauseCode.getCodeValueFromCause(e.getCause()) == ExceptionCauseCode.UNAUTHENTICATED) {
                    throw new MailAlreadyRegistered("Email already registered");
                } else if (ExceptionCauseCode.getCodeValueFromCause(e.getCause()) == ExceptionCauseCode.ALREADY_EXISTS) {
                    throw new UsernameAlreadyRegistered("Username already registered");
                }
                throw new AuthentificationException("Authentification error");
            } catch (InterruptedException | TimeoutException e) {
                throw new AuthentificationException("Communication problem during authenticateEmail with nakama server : " + e.getMessage());
            }
        } else {
            this.tryReconnectUser();
            if (this.user == null) {
                try {
                    this.session = this.client.authenticateEmail(mail, password, false).get(2000, TimeUnit.MILLISECONDS);
                    this.user = this.client.getAccount(this.session).get(5000, TimeUnit.MILLISECONDS).getUser();
                    this.pref.edit().putString("nk.session", this.session.getAuthToken()).apply();
                } catch (ExecutionException e) {
                    if (ExceptionCauseCode.getCodeValueFromCause(e.getCause()) == ExceptionCauseCode.UNAUTHENTICATED) {
                        throw new IncorrectCredentials("Incorrect credentials during singin");
                    }
                } catch (InterruptedException | TimeoutException e) {
                    throw new AuthentificationException("Communication problem with getAccount nakama server  :" + e.getMessage());
                }
            }
        }
        return this.user;
    }

    public void tryReconnectUser(){
        // Lets check if we can restore a cached session.
        String sessionString = this.pref.getString("nk.session", null);
        if (sessionString != null && !sessionString.isEmpty()) {
            Session restoredSession = DefaultSession.restore(sessionString);
            if (!restoredSession.isExpired(new Date())) {
                // Session was valid and is restored now.
                this.session = restoredSession;
                try {
                    this.user = this.client.getAccount(this.session).get(5000, TimeUnit.MILLISECONDS).getUser();
                } catch (ExecutionException | TimeoutException | InterruptedException e) {
                    Log.i("fr.aboucorp.variantchess", e.getMessage());
                }
            }
        }
    }



    public void destroySession() {
        this.client.disconnect();
        this.pref.edit().putString("nk.session", null).apply();
        this.user = null;
    }

    public ChessUser updateDisplayName(String displayName) throws UsernameAlreadyRegistered {
        Metadata data = new Metadata();
        String timeZone = TimeZone.getDefault().getDisplayName();
        String langTag = Locale.getDefault().getDisplayLanguage();
        data.put("displayName", displayName);
        data.put("timeZone", timeZone);
        data.put("langTag", langTag);
        String rpcid = "update_user_infos";
        try {
            this.client.rpc(this.session, rpcid, data.getJsonFromMetadata()).get();
            return ChessUserDto.fromUserToChessUser(this.client.getAccount(this.session).get().getUser());
        } catch (ExecutionException e) {
            if (ExceptionCauseCode.getCodeValueFromCause(e.getCause()) == ExceptionCauseCode.ALREADY_EXISTS) {
                throw new UsernameAlreadyRegistered("Thius username is already taken");
            }
        } catch (InterruptedException e) {
            Log.e("fr.aboucorp.variantchess", "Erro during updating account");
            return null;
        }
        return null;
    }

    public String launchMatchMaking(GameMode gamemode, MatchListener matchListener) throws ExecutionException, InterruptedException {
        this.matchmakingSocket = this.client.createSocket();
        this.socketListener = new MatchMakingSocketListener(matchListener);
        this.matchmakingSocket.connect(this.session, this.socketListener).get();
        String ticketString = this.pref.getString("nk.ticket", null);
        if (!TextUtils.isEmpty(ticketString)) {
            try {
                this.matchmakingSocket.removeMatchmaker(ticketString).get();
            } catch (ExecutionException | InterruptedException e) {
                Log.i("fr.aboucorp.variantchess", "Cannot remove matchmaking");
            }
        }
        Metadata<String> stringProps = new Metadata<>();
        Metadata<Double> numProps = new Metadata<>();
        Locale current = Locale.getDefault();
        stringProps.put("locale", current.getCountry());
        stringProps.put("gamemode", gamemode.getName());
        numProps.put("rank", 1.0);
        String query = "*";
        int minCount = 2;
        int maxCount = 2;
        MatchmakerTicket matchmakerTicket = this.matchmakingSocket.addMatchmaker(
                minCount, maxCount, query, stringProps, numProps).get();
        this.pref.edit().putString("nk.ticket", matchmakerTicket.getTicket()).apply();
        return matchmakerTicket.getTicket();
    }

    public void cancelMatchMaking(String ticket) {
        try {
            this.matchmakingSocket.removeMatchmaker(ticket).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.i("fr.aboucorp.variantchess", "Cannot remove matchmaking");
        } finally {
            this.matchmakingSocket.disconnect();
        }
    }

    public ChessUser getChessUser() {
        if (this.user == null) {
            return null;
        } else {
            return ChessUserDto.fromUserToChessUser(this.user);
        }
    }

    public Match joinMatchByToken(String token) throws ExecutionException, InterruptedException {
        return this.matchmakingSocket.joinMatchToken(token).get();
    }

    public List<User> getUsersFromMatched(MatchmakerMatched matched) throws ExecutionException, InterruptedException {
        return this.client.getUsers(this.session, matched.getUsers().stream().map(u -> u.getPresence().getUserId())
                .collect(Collectors.toList())).get().getUsersList();
    }

    public void setMatchListener(MatchListener listener) {
        this.socketListener.setListener(listener);
    }

    public void sendEvent(GameEvent event, String matchId) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(event);
            oos.flush();
            byte[] data = bos.toByteArray();
            this.matchmakingSocket.sendMatchData(matchId, event.boardEventType, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean mailExists(String mail) {
        Metadata metadata = new Metadata();
        metadata.put("email", mail);
        String rpcid = "mail_exists";
        Rpc userExistsRpc = null;
        try {
            userExistsRpc = this.client.rpc(this.session, rpcid, metadata.getJsonFromMetadata()).get(5000, TimeUnit.MILLISECONDS);
            return userExistsRpc != null && Boolean.parseBoolean(userExistsRpc.getPayload());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return true;
    }
}


