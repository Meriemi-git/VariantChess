package fr.aboucorp.variantchess.app.multiplayer;

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
import com.heroiclabs.nakama.api.NotificationList;

import fr.aboucorp.variantchess.app.multiplayer.listeners.ChatListener;
import fr.aboucorp.variantchess.app.multiplayer.listeners.MatchListener;
import fr.aboucorp.variantchess.app.multiplayer.listeners.MatchmakingListener;
import fr.aboucorp.variantchess.app.multiplayer.listeners.NotificationListener;
import fr.aboucorp.variantchess.app.utils.LogUtil;

public class NakamaSocketListener extends AbstractSocketListener {
    private final SessionManager sessionManager;
    private MatchmakingListener matchmakingListener;
    private ChatListener chatListener;
    private MatchListener matchListener;
    private NotificationListener notificationListener;

    public NakamaSocketListener(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void onDisconnect(Throwable t) {
        super.onDisconnect(t);
        this.sessionManager.setSocketClosed(true);
        LogUtil.i("fr.aboucorp.variantchess", "onDisconnect ");
    }

    @Override
    public void onError(Error error) {
        super.onError(error);
        LogUtil.e("fr.aboucorp.variantchess", "onError " + error.getMessage());

    }

    @Override
    public void onChannelMessage(ChannelMessage message) {
        super.onChannelMessage(message);
        LogUtil.i("fr.aboucorp.variantchess", "onChannelMessage " + message.getContent());
        if (chatListener != null) {
            chatListener.onChannelMessage(message);
        } else {
            LogUtil.e("fr.aboucorp.variantchess", "Missing chatListener.");
        }
    }

    @Override
    public void onChannelPresence(ChannelPresenceEvent presence) {
        super.onChannelPresence(presence);
        LogUtil.i("fr.aboucorp.variantchess", "onChannelPresence " + presence.getRoomName());
        if (chatListener != null) {
            chatListener.onChannelPresence(presence);
        } else {
            LogUtil.e("fr.aboucorp.variantchess", "Missing chatListener.");
        }
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        super.onMatchmakerMatched(matched);
        if (matchmakingListener != null) {
            matchmakingListener.onMatchmakerMatched(matched);
        } else {
            LogUtil.e("fr.aboucorp.variantchess", "Missing matchmakingListener.");
        }
    }

    @Override
    public void onMatchData(MatchData matchData) {
        super.onMatchData(matchData);
        LogUtil.i("fr.aboucorp.variantchess", String.format("onMatchData opCode : %s", matchData.getOpCode()));
        if (matchListener != null) {
            matchListener.onMatchData(matchData);
        } else {
            LogUtil.e("fr.aboucorp.variantchess", "Missing matchListener.");
        }
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        LogUtil.i("fr.aboucorp.variantchess", "onMatchPresence " + matchPresence.getMatchId());
        if (matchListener != null) {
            matchListener.onMatchPresence(matchPresence);
        } else {
            LogUtil.e("fr.aboucorp.variantchess", "Missing matchListener.");
        }
    }

    @Override
    public void onNotifications(NotificationList notifications) {
        super.onNotifications(notifications);
        LogUtil.i("fr.aboucorp.variantchess", "onNotifications notif numbers :" + notifications.getNotificationsCount());
        if (this.notificationListener != null) {
            this.notificationListener.onNotifications(notifications);
        } else {
            LogUtil.e("fr.aboucorp.variantchess", "Missing notificationListener.");
        }
    }

    @Override
    public void onStatusPresence(StatusPresenceEvent presence) {
        super.onStatusPresence(presence);
        LogUtil.i("fr.aboucorp.variantchess", "onStatusPresence " + presence.getJoins().size());
    }

    @Override
    public void onStreamPresence(StreamPresenceEvent presence) {
        super.onStreamPresence(presence);
        LogUtil.i("fr.aboucorp.variantchess", "onStreamPresence " + presence.getJoins().size());

    }

    @Override
    public void onStreamData(StreamData data) {
        super.onStreamData(data);
        LogUtil.i("fr.aboucorp.variantchess", "onStreamData " + data.getData());
    }

    public void setMatchmakingListener(MatchmakingListener matchmakingListener) {
        this.matchmakingListener = matchmakingListener;
    }

    public void setChatListener(ChatListener chatListener) {
        this.chatListener = chatListener;
    }

    public void setMatchListener(MatchListener matchListener) {
        this.matchListener = matchListener;
    }

    public void setNotificationListener(NotificationListener notificationListener) {
        this.notificationListener = notificationListener;
    }
}
