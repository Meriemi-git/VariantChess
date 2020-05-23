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

class MatchMakingSocketListener extends AbstractSocketListener {
    private final MatchListener listener;

    public MatchMakingSocketListener(MatchListener listener) {
        this.listener = listener;
    }
    @Override
    public void onDisconnect(Throwable t) {
        super.onDisconnect(t);
        this.listener.onDisconnect(t);
    }

    @Override
    public void onError(Error error) {
        super.onError(error);
        this.listener.onError(error);
    }

    @Override
    public void onChannelMessage(ChannelMessage message) {
        super.onChannelMessage(message);
        this.listener.onChannelMessage(message);
    }

    @Override
    public void onChannelPresence(ChannelPresenceEvent presence) {
        super.onChannelPresence(presence);
        this.listener.onChannelPresence(presence);
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        super.onMatchmakerMatched(matched);
        this.listener.onMatchmakerMatched(matched);
    }

    @Override
    public void onMatchData(MatchData matchData) {
        super.onMatchData(matchData);
        this.listener.onMatchData(matchData);
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        super.onMatchPresence(matchPresence);
        this.listener.onMatchPresence(matchPresence);
    }

    @Override
    public void onNotifications(NotificationList notifications) {
        super.onNotifications(notifications);
        this.listener.onNotifications(notifications);
    }

    @Override
    public void onStatusPresence(StatusPresenceEvent presence) {
        super.onStatusPresence(presence);
        this.listener.onStatusPresence(presence);
    }

    @Override
    public void onStreamPresence(StreamPresenceEvent presence) {
        super.onStreamPresence(presence);
        this.listener.onStreamPresence(presence);
    }

    @Override
    public void onStreamData(StreamData data) {
        super.onStreamData(data);
        this.listener.onStreamData(data);
    }
}
