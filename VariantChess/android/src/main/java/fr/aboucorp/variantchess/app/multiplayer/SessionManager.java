package fr.aboucorp.variantchess.app.multiplayer;

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
import java.util.UUID;
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
import fr.aboucorp.variantchess.app.multiplayer.listeners.MatchListener;
import fr.aboucorp.variantchess.app.multiplayer.listeners.MatchmakingListener;
import fr.aboucorp.variantchess.app.multiplayer.listeners.NotificationListener;
import fr.aboucorp.variantchess.app.utils.ExceptionCauseCode;
import fr.aboucorp.variantchess.app.utils.JsonExtractor;
import fr.aboucorp.variantchess.app.utils.VariantVars;

public class SessionManager {
    private static SessionManager INSTANCE;
    private final Client client = new DefaultClient("defaultkey", "192.168.1.37", 7349, false);
    private Session session;
    private User user;
    private SocketClient socket;
    private boolean socketClosed;
    private final String variantChessToken;
    private NakamaSocketListener nakamaSocketListener;

    private SessionManager() {
        this.variantChessToken = UUID.randomUUID().toString();
        this.nakamaSocketListener = new NakamaSocketListener(this);
    }

    public static SessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        return INSTANCE;
    }

    public ChessUser signInWithEmail(String mail, String password) throws AuthentificationException {
        try {
            this.session = this.client.authenticateEmail(mail, password, false).get(2000, TimeUnit.MILLISECONDS);
            this.user = this.client.getAccount(this.session).get(5000, TimeUnit.MILLISECONDS).getUser();
            connectSocket();
            ChessUser chessUser = ChessUserDto.fromUserToChessUser(this.user);
            chessUser.authToken = this.session.getAuthToken();
            return chessUser;
        } catch (ExecutionException e) {
            if (ExceptionCauseCode.getCodeValueFromCause(e.getCause()) == ExceptionCauseCode.UNAUTHENTICATED) {
                throw new IncorrectCredentials("Incorrect credentials during singin");
            }
            throw new AuthentificationException("Authentification error " + e.getMessage());
        } catch (InterruptedException | TimeoutException e) {
            throw new AuthentificationException("Communication problem with getAccount nakama server  :" + e.getMessage());
        } catch (Exception e) {
            throw new AuthentificationException("Unknown error." + e.getMessage());
        }
    }

    public ChessUser signUpWithEmail(String mail, String password, String displayName) throws AuthentificationException {
        try {
            this.session = this.client.authenticateEmail(mail, password, true, displayName).get(2000, TimeUnit.MILLISECONDS);
            this.user = this.client.getAccount(this.session).get(5000, TimeUnit.MILLISECONDS).getUser();
            ChessUser chessUser = ChessUserDto.fromUserToChessUser(this.user);
            chessUser.authToken = this.session.getAuthToken();
            return chessUser;
        } catch (ExecutionException e) {
            if (ExceptionCauseCode.getCodeValueFromCause(e.getCause()) == ExceptionCauseCode.UNAUTHENTICATED) {
                throw new MailAlreadyRegistered("Email already registered");
            } else if (ExceptionCauseCode.getCodeValueFromCause(e.getCause()) == ExceptionCauseCode.ALREADY_EXISTS) {
                throw new UsernameAlreadyRegistered("Username already registered");
            }
            throw new AuthentificationException("Authentification error : " + e.getMessage());
        } catch (InterruptedException | TimeoutException e) {
            throw new AuthentificationException("Communication problem during authenticateEmail with nakama server : " + e.getMessage());
        }
    }

    public boolean isUserConnected(ChessUser chessUser) {
        return this.session != null && !this.session.IsExpired() && this.session.getAuthToken().equals(chessUser.authToken);
    }

    public ChessUser tryReconnectUser(String authToken) {
        // Lets check if we can restore a cached session.
        if (authToken != null && !authToken.isEmpty()) {
            Session restoredSession = DefaultSession.restore(authToken);
            if (!restoredSession.isExpired(new Date())) {
                // Session was valid and is restored now.
                this.session = restoredSession;
                try {
                    this.user = this.client.getAccount(this.session).get(5000, TimeUnit.MILLISECONDS).getUser();
                    connectSocket();
                    return ChessUserDto.fromUserToChessUser(this.user);
                } catch (ExecutionException | TimeoutException | InterruptedException e) {
                    Log.i("fr.aboucorp.variantchess", e.getMessage());
                }
            }
        }
        return null;
    }


    private void connectSocket() {
        if (this.socket == null || this.socketClosed) {

            this.socket = this.client.createSocket();
            try {
                socket.connect(this.session, this.nakamaSocketListener).get();
                this.checkIfSessionExists(this.socket);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkIfSessionExists(SocketClient socket) throws InterruptedException, ExecutionException, TimeoutException {
        String rpcid = "check_user_session_exists";
        Metadata data = new Metadata();
        data.put(VariantVars.VARIANT_CHESS_TOKEN, this.variantChessToken);
        Rpc userExistsRpc = null;
        userExistsRpc = socket.rpc(rpcid, data.getJsonFromMetadata()).get(5000, TimeUnit.MILLISECONDS);
        boolean exists = JsonExtractor.ectractAttributeByName(userExistsRpc.getPayload(), "already_connected");
        Log.i("fr.aboucorp.variantchess", "Existing session : " + exists);
        return exists;
    }

    public void sendData(Object data, String matchId, long opcode) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(data);
            oos.flush();
            byte[] binaryData = bos.toByteArray();
            this.socket.sendMatchData(matchId, opcode, binaryData);
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

    public void disconnect() {
        if (this.socket != null) {
            this.socket.disconnect();
        }
        this.socket = null;

        this.session = null;
        this.user = null;

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
                throw new UsernameAlreadyRegistered("This username is already taken");
            }
        } catch (InterruptedException e) {
            Log.e("fr.aboucorp.variantchess", "Error during updating account");
            return null;
        }
        return null;
    }

    public void cancelMatchMaking(String ticket) {
        try {
            this.socket.removeMatchmaker(ticket).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.i("fr.aboucorp.variantchess", "Cannot remove matchmaking");
        } finally {
            this.socket.disconnect();
        }
    }

    public Match joinMatchByToken(String token) throws ExecutionException, InterruptedException {
        return this.socket.joinMatchToken(token).get();
    }

    public List<User> getUsersFromMatched(MatchmakerMatched matched) throws ExecutionException, InterruptedException {
        return this.client.getUsers(this.session, matched.getUsers().stream().map(u -> u.getPresence().getUserId())
                .collect(Collectors.toList())).get().getUsersList();
    }

    public String launchMatchMaking(String rulesName, String ticket) throws ExecutionException, InterruptedException {
        if (this.socket == null) {
            connectSocket();
        }
        if (!TextUtils.isEmpty(ticket)) {
            try {
                this.socket.removeMatchmaker(ticket).get();
            } catch (ExecutionException | InterruptedException e) {
                Log.i("fr.aboucorp.variantchess", "Cannot remove matchmaking");
            }
        }
        Metadata<String> stringProps = new Metadata<>();
        Metadata<Double> numProps = new Metadata<>();
        Locale current = Locale.getDefault();
        stringProps.put("locale", current.getCountry());
        stringProps.put("gamemode", rulesName);
        numProps.put("rank", 1.0);
        String query = "*";
        int minCount = 2;
        int maxCount = 2;
        MatchmakerTicket matchmakerTicket = this.socket.addMatchmaker(
                minCount, maxCount, query, stringProps, numProps).get();
        return matchmakerTicket.getTicket();
    }

    public void setSocketClosed(boolean socketClosed) {
        this.socketClosed = socketClosed;
    }

    public Session getSession() {
        return this.session;
    }

    public SocketClient getSocket() {
        return this.socket;
    }

    public void setMatchmakingListener(MatchmakingListener listener) {
        this.nakamaSocketListener.setMatchmakingListener(listener);
    }

    public void setMatchListener(MatchListener matchListener) {
        this.nakamaSocketListener.setMatchListener(matchListener);
    }

    public void setNotificationListener(NotificationListener notificationListener) {
        this.nakamaSocketListener.setNotificationListener(notificationListener);
    }


}


