package fr.aboucorp.variantchess.app.multiplayer;

import android.util.Log;

import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.StatusPresenceEvent;
import com.heroiclabs.nakama.StreamData;
import com.heroiclabs.nakama.StreamPresenceEvent;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.Notification;
import com.heroiclabs.nakama.api.NotificationList;

import fr.aboucorp.variantchess.app.utils.JsonExtractor;
import fr.aboucorp.variantchess.app.utils.VariantVars;

public class MultiplayerSocketListener extends AbstractSocketListener {
    private final SessionManager sessionManager;

    public MultiplayerSocketListener(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void onDisconnect(Throwable t) {
        super.onDisconnect(t);
        this.sessionManager.setSocketIsClosed(true);
        Log.i("fr.aboucorp.variantchess", "onDisconnect ");
    }

    @Override
    public void onError(Error error) {
        super.onError(error);
        Log.i("fr.aboucorp.variantchess", "onError " + error.getMessage());

    }

    @Override
    public void onChannelMessage(ChannelMessage message) {
        super.onChannelMessage(message);
        Log.i("fr.aboucorp.variantchess", "onChannelMessage " + message.getContent());

    }

    @Override
    public void onChannelPresence(ChannelPresenceEvent presence) {
        super.onChannelPresence(presence);
        Log.i("fr.aboucorp.variantchess", "onChannelPresence " + presence.getRoomName());

    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        super.onMatchmakerMatched(matched);
        Log.i("fr.aboucorp.variantchess", "onMatchmakerMatched " + matched.getMatchId());
    }

    @Override
    public void onMatchData(MatchData matchData) {
        super.onMatchData(matchData);
        Log.i("fr.aboucorp.variantchess", "onMatchData " + matchData.getOpCode());
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        Log.i("fr.aboucorp.variantchess", "onMatchPresence " + matchPresence.getMatchId());
    }

    @Override
    public void onNotifications(NotificationList notifications) {
        super.onNotifications(notifications);
        Log.i("fr.aboucorp.variantchess", "onNotifications " + notifications.getNotificationsCount());
        for (Notification notification : notifications.getNotificationsList()
        ) {
            if (notification.getCode() == 666) {
                String authToken = JsonExtractor.ectractAttributeByName(notification.getContent(), VariantVars.VARIANT_CHESS_TOKEN);
                if (!authToken.equals(this.sessionManager.getSession().getAuthToken())) {
                    Log.i("fr.aboucorp.variantchess", "Disconnection of user with sessionID : " + authToken);
                    sessionManager.disconnect();
                }
            }
        }

    }

    @Override
    public void onStatusPresence(StatusPresenceEvent presence) {
        super.onStatusPresence(presence);
        Log.i("fr.aboucorp.variantchess", "onStatusPresence " + presence.getJoins().size());
    }

    @Override
    public void onStreamPresence(StreamPresenceEvent presence) {
        super.onStreamPresence(presence);
        Log.i("fr.aboucorp.variantchess", "onStreamPresence " + presence.getJoins().size());

    }

    @Override
    public void onStreamData(StreamData data) {
        super.onStreamData(data);
        Log.i("fr.aboucorp.variantchess", "onStreamData " + data.getData());
    }
}
