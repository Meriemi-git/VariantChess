package fr.aboucorp.variantchess.app.multiplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.Client;
import com.heroiclabs.nakama.DefaultClient;
import com.heroiclabs.nakama.DefaultSession;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.MatchmakerTicket;
import com.heroiclabs.nakama.Session;
import com.heroiclabs.nakama.SocketClient;
import com.heroiclabs.nakama.SocketListener;
import com.heroiclabs.nakama.StorageObjectId;
import com.heroiclabs.nakama.api.Rpc;
import com.heroiclabs.nakama.api.StorageObjectList;
import com.heroiclabs.nakama.api.StorageObjects;
import com.heroiclabs.nakama.api.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NakamaSessionManager {
    private static NakamaSessionManager INSTANCE;
    private final Context context;
    public final Client client = new DefaultClient("defaultkey","192.168.1.37", 7349, false);
    public Session session;

    private NakamaSessionManager(Context context) {
        this.context = context;
    }


    public void start() throws ExecutionException, InterruptedException {
        String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        SharedPreferences pref = context.getSharedPreferences("nakama",Context.MODE_PRIVATE);
        if(!restorSessionIfPossible(pref)) {
            this.session = client.authenticateCustom(android_id).get();
            pref = context.getSharedPreferences("nakama", Context.MODE_PRIVATE);
            pref.edit().putString("nk.session", session.getAuthToken()).apply();
        }
    }


    public User authentWithEmail(String mail, String password) throws ExecutionException, InterruptedException {
        SharedPreferences pref = context.getSharedPreferences("nakama",Context.MODE_PRIVATE);
        if(!restorSessionIfPossible(pref)){
            this.session = client.authenticateEmail(mail,password).get();
            pref = context.getSharedPreferences("nakama",Context.MODE_PRIVATE);
            pref.edit().putString("nk.session", session.getAuthToken()).apply();
        }
        return this.client.getAccount(this.session).get().getUser();
    }

    public User authentWithGoogle(String idToken, String mail) throws ExecutionException, InterruptedException {
        SharedPreferences pref = context.getSharedPreferences("nakama",Context.MODE_PRIVATE);
        if(!restorSessionIfPossible(pref)){
            Map<String, String> mailInfo = new HashMap<>();
            mailInfo.put("mail",mail);
            this.session = client.authenticateGoogle(idToken,mailInfo).get();
            pref = context.getSharedPreferences("nakama",Context.MODE_PRIVATE);
            pref.edit().putString("nk.session", session.getAuthToken()).apply();
        }
        return this.client.getAccount(this.session).get().getUser();
    }

    public boolean restorSessionIfPossible(SharedPreferences pref){

        // Lets check if we can restore a cached session.
        String sessionString = pref.getString("nk.session", null);
        if (sessionString != null && !sessionString.isEmpty()) {
            Session restoredSession = DefaultSession.restore(sessionString);
            if (!restoredSession.isExpired(new Date())) {
                // Session was valid and is restored now.
                this.session = restoredSession;
                return true;
            }
        }
        return false;
    }

    public void testUser() throws ExecutionException, InterruptedException {
        User user = this.client.getAccount(this.session).get().getUser();
        Log.i("fr.aboucorp.variantchess","DisplayName :" +user.getDisplayName());
        String displayName = "Doremus";
        String avatarUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/55/Tux_Enhanced.svg/878px-Tux_Enhanced.svg.png";
        String location = "Toulouse";
        client.updateAccount(session, null, displayName, avatarUrl, null, location);
        user = this.client.getAccount(this.session).get().getUser();
        Log.i("fr.aboucorp.variantchess","DisplayName :" +user.getDisplayName());
    }

    public SocketClient getSocket() throws ExecutionException, InterruptedException {
        String host = "localhost";
        int port = 7350; // different port to the main_layout API port
        boolean ssl = false;
        SocketClient socket = client.createSocket();
        SocketListener listener = new AbstractSocketListener() {
            @Override
            public void onError(Error error) {
                super.onError(error);
                Log.i("fr.aboucorp.variantchess","Error: " + error.getMessage());
            }

            @Override
            public void onMatchData(MatchData matchData) {
                super.onMatchData(matchData);
                Log.i("fr.aboucorp.variantchess","Match data :" + matchData.getMatchId());
            }

            @Override
            public void onDisconnect(final Throwable t) {
                Log.i("fr.aboucorp.variantchess", "Socket disconnected");
            }

            @Override
            public void onMatchmakerMatched(final MatchmakerMatched matched) {
                Log.i("fr.aboucorp.variantchess",String.format("Received MatchmakerMatched message: %s", matched.toString()));
                Log.i("fr.aboucorp.variantchess",String.format("Matched opponents: %s", matched.getUsers().toString()));
            }
        };

        socket.connect(session, listener).get();

        return socket;
    }

    public void testStorage() throws ExecutionException, InterruptedException {

        StorageObjectId objectId = new StorageObjectId("saves");
        objectId.setUserId(session.getUserId());
        StorageObjects objects = client.readStorageObjects(session, objectId).get();
        Log.i("fr.aboucorp.variantchess", String.format("Read objects %s", objects.getObjectsList().toString()));


        StorageObjectList objectList = client.listUsersStorageObjects(session, "saves", session.getUserId()).get();
        Log.i("fr.aboucorp.variantchess", String.format("List objects %s", objectList));
    }

    public void testMachmaking() throws ExecutionException, InterruptedException {
    SocketClient socket = getSocket();
        String ticket = getInSharedPreference("matchmaking");
        if(ticket != null){
            try {
                socket.removeMatchmaker(ticket).get();
                Log.i("fr.aboucorp.variantchess", "Removed form matcmaking");
            }catch(Exception e){
                Log.i("fr.aboucorp.variantchess", "Cannot remove matchmaking");
            }
        }
        Log.i("fr.aboucorp.variantchess","machmaking creation");
        int minCount = 2;
        int maxCount = 2;
        String query = "+properties.region:europe +properties.rank:>=5 +properties.rank:<=10";
        Map<String, String> stringProperties = new HashMap<String, String>() {{
            put("region", "europe");
        }};
        Map<String, Double> numericProperties = new HashMap<String, Double>() {{
            put("rank", 8.0);
        }};

        MatchmakerTicket tiket = socket.addMatchmaker(minCount,maxCount,query,stringProperties,numericProperties).get();
        Log.i("fr.aboucorp.variantchess",String.format("Matchmaking tiket : %s",tiket.getTicket()));
        putInSharedPreference("matchmaking",tiket.getTicket());
    }

    private String getInSharedPreference(String key){
        SharedPreferences pref = context.getSharedPreferences("nakama",Context.MODE_PRIVATE);
        // Lets check if we can restore a cached session.
        String sessionString = pref.getString(key, null);
        if (sessionString != null && !sessionString.isEmpty()) {
            return  sessionString;
        }else{
            return null;
        }
    }

    private void putInSharedPreference(String key, String value){
        SharedPreferences pref = context.getSharedPreferences("nakama",Context.MODE_PRIVATE);
        pref.edit().putString(key, value).apply();
    }

    public void stopClient() throws InterruptedException {
        client.disconnect(1000, TimeUnit.MILLISECONDS);
    }

    public void testRpc() throws ExecutionException, InterruptedException {
        String payload = "{\"id\": \"11111111-1111-1111-1111-111111111111\"}";
        String rpcid = "get_user_by_id";
        Rpc getUSerById = client.rpc(session, rpcid, payload).get();
        Log.i("fr.aboucorp.variantchess",String.format("Retrieved user: %s", getUSerById.getPayload()));
    }

    public static NakamaSessionManager getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new NakamaSessionManager(context);
        }
        return INSTANCE;
    }
}
