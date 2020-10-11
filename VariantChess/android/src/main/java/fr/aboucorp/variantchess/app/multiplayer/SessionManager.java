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
import fr.aboucorp.variantchess.app.exceptions.ExceptionCauseCode;
import fr.aboucorp.variantchess.app.exceptions.IncorrectCredentials;
import fr.aboucorp.variantchess.app.exceptions.MailAlreadyRegistered;
import fr.aboucorp.variantchess.app.exceptions.UsernameAlreadyRegistered;
import fr.aboucorp.variantchess.app.multiplayer.listeners.MatchListener;
import fr.aboucorp.variantchess.app.multiplayer.listeners.MatchmakingListener;
import fr.aboucorp.variantchess.app.multiplayer.listeners.NotificationListener;
import fr.aboucorp.variantchess.app.utils.JsonExtractor;
import fr.aboucorp.variantchess.app.utils.RPCMethods;
import fr.aboucorp.variantchess.app.utils.VariantVars;


/**
 * <p>Responsible of all operations requiring a nakama session like :</p>
 * <ul>
 *     <li>Nakama User authentification</li>
 *     <li>Nakama Socket management </li>
 *     <li>Nakama RPC management</li>
 * </ul>
 */
public class SessionManager {
    /**
     * Singleton instance
     */
    private static SessionManager INSTANCE;
    /**
     * Nakama client
     */
    private final Client client = new DefaultClient("defaultkey", "192.168.1.37", 7349, false);
    /**
     * Token used to identify this session on this device
     */
    private final String variantChessToken;
    /**
     * Current session (can expire)
     */
    private Session session;
    /**
     * Nakama socket
     */
    private SocketClient socket;
    /**
     * Alive indicator for session
     */
    private boolean socketClosed;
    /**
     * Generic listener for all Nakama events send on the socket
     */
    private NakamaSocketListener nakamaSocketListener;


    private SessionManager() {
        this.variantChessToken = UUID.randomUUID().toString();
        this.nakamaSocketListener = new NakamaSocketListener(this);
    }

    /**
     * Gets singleton instance
     *
     * @return the instance
     */
    public static SessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        return INSTANCE;
    }

    /**
     * Sign in with email chess user.
     *
     * @param mail     the mail The mail with which user is authentificated
     * @param password the password  The password with which user is authentificated
     * @return the chess user authentificated
     * @throws AuthentificationException the authentification exception if provided credetnials are wrong
     */
    public ChessUser signInWithEmail(String mail, String password) throws AuthentificationException {
        try {
            this.session = this.client.authenticateEmail(mail, password, false).get(5000, TimeUnit.MILLISECONDS);
            User user = this.client.getAccount(this.session).get(5000, TimeUnit.MILLISECONDS).getUser();
            connectSocket();
            ChessUser chessUser = ChessUserDto.fromUserToChessUser(user);
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

    /**
     * Sign up with email and password and username.
     *
     * @param mail     the mail
     * @param password the password
     * @param username the display name
     * @return the chess user
     * @throws AuthentificationException the authentification exception
     */
    public ChessUser signUpWithEmail(String mail, String password, String username) throws AuthentificationException {
        try {
            this.session = this.client.authenticateEmail(mail, password, true, username).get(2000, TimeUnit.MILLISECONDS);
            User user = this.client.getAccount(this.session).get(5000, TimeUnit.MILLISECONDS).getUser();
            ChessUser chessUser = ChessUserDto.fromUserToChessUser(user);
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

    /**
     * Try to reconnect a ChessUser previously conencted .
     *
     * @param authToken the auth token (comming from sharedpreferences)
     * @return the chess user connected
     */
    public ChessUser tryReconnectUser(String authToken) {
        // Lets check if we can restore a cached session.
        if (authToken != null && !authToken.isEmpty()) {
            Session restoredSession = DefaultSession.restore(authToken);
            if (!restoredSession.isExpired(new Date())) {
                // Session was valid and is restored now.
                this.session = restoredSession;
                try {
                    User user = this.client.getAccount(this.session).get(5000, TimeUnit.MILLISECONDS).getUser();
                    connectSocket();
                    return ChessUserDto.fromUserToChessUser(user);
                } catch (ExecutionException | TimeoutException | InterruptedException e) {
                    Log.i("fr.aboucorp.variantchess", e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Connect the existoing nakama socket or create and connect a new one
     */
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

    /**
     * Call an RPC method on Nakama backend to check is this user is already connected on device
     * If it the case disconnect others @see VariantChess-Go repository
     *
     * @param socket the actual connected socket
     * @return True if session exists false if not
     * @throws InterruptedException If socket connection is broken durting operation
     * @throws ExecutionException   If Nakama backebnd return an error
     * @throws TimeoutException     If socket take too much time to respond
     */
    private boolean checkIfSessionExists(SocketClient socket) throws InterruptedException, ExecutionException, TimeoutException {
        Metadata data = new Metadata();
        data.put(VariantVars.VARIANT_CHESS_TOKEN, this.variantChessToken);
        Rpc userExistsRpc = null;
        userExistsRpc = socket.rpc(RPCMethods.CHECK_IF_USER_EXISTS, data.getJsonFromMetadata()).get(5000, TimeUnit.MILLISECONDS);
        boolean exists = JsonExtractor.ectractAttributeByName(userExistsRpc.getPayload(), "already_connected");
        return exists;
    }


    /**
     * Send data to all the other user connected to the match identified by the match id
     *
     * @param data    the data to be send to all users
     * @param matchId the id of the match to witch every users are connected
     * @param opcode  the opcode og the seend data information
     */
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


    /**
     * Disconnect the surrent socket and remove the session
     */
    public void disconnect() {
        if (this.socket != null) {
            this.socket.disconnect();
        }
        this.socket = null;
        this.session = null;
    }


    /**
     * Update display name chess user.
     * Update the username of the user by calling an RPC funtion on Nakama backend
     *
     * @param username the display name
     * @return the chess user
     * @throws UsernameAlreadyRegistered if the username is already registered
     */
    public ChessUser updateDisplayName(String username) throws UsernameAlreadyRegistered {
        Metadata data = new Metadata();
        String timeZone = TimeZone.getDefault().getDisplayName();
        String langTag = Locale.getDefault().getDisplayLanguage();
        data.put("displayName", username);
        data.put("timeZone", timeZone);
        data.put("langTag", langTag);
        try {
            this.client.rpc(this.session, RPCMethods.UPDATE_USER_INFOS, data.getJsonFromMetadata()).get();
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

    /**
     * Cancel the current matchmaking identified by the ticket
     *
     * @param ticket the ticket whitch identify the machmaking operation
     */
    public void cancelMatchMaking(String ticket) {
        try {
            this.socket.removeMatchmaker(ticket).get();
        } catch (ExecutionException | InterruptedException e) {
            Log.i("fr.aboucorp.variantchess", "Cannot remove matchmaking");
        } finally {
            this.socket.disconnect();
        }
    }

    /**
     * Join match by token match.
     *
     * @param matchId the id of the match to join
     * @return the match joined
     * @throws ExecutionException   the execution exception if Nakama backend return an error
     * @throws InterruptedException the interrupted exception if some network error occured
     */
    public Match joinMatchByToken(String matchId) throws ExecutionException, InterruptedException {
        return this.socket.joinMatchToken(matchId).get();
    }

    /**
     * Gets users from result of successfull matchmaking operation
     *
     * @param matched The result of successfull matchmaking operation
     * @return the users from matched
     * @throws ExecutionException   the execution exception if Nakama backend return an error
     * @throws InterruptedException the interrupted exception if some network error occured
     */
    public List<User> getUsersFromMatched(MatchmakerMatched matched) throws ExecutionException, InterruptedException {
        return this.client.getUsers(this.session, matched.getUsers().stream().map(u -> u.getPresence().getUserId())
                .collect(Collectors.toList())).get().getUsersList();
    }


    /**
     * Launch a matchmùaking operation
     *
     * @param rulesName the rules name witch is part of the matchmaking filters
     * @param ticket    the ticket of a possible existing matchmaking operation
     * @return the ticket that identifies the matchmaking operation
     * @throws ExecutionException   the execution exception if Nakama backend return an error
     * @throws InterruptedException the interrupted exception if some network error occured
     */
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


