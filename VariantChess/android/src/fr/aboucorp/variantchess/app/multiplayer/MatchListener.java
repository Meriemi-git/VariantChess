package fr.aboucorp.variantchess.app.multiplayer;

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

public interface MatchListener {
    void onDisconnect(Throwable t);

    void onError(Error error);

    void onChannelMessage(ChannelMessage message);

    void onChannelPresence(ChannelPresenceEvent presence);

    void onMatchmakerMatched(MatchmakerMatched matched);

    void onMatchData(MatchData matchData);

    void onMatchPresence(MatchPresenceEvent matchPresence);

    void onNotifications(NotificationList notifications);

    void onStatusPresence(StatusPresenceEvent presence);

    void onStreamPresence(StreamPresenceEvent presence);

    void onStreamData(StreamData data);
}
