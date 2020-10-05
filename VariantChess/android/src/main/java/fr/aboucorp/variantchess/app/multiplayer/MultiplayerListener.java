package fr.aboucorp.variantchess.app.multiplayer;

import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.api.ChannelMessage;

public interface MultiplayerListener {
    void onChannelMessage(ChannelMessage message);

    void onChannelPresence(ChannelPresenceEvent presence);

    void onMatchmakerMatched(MatchmakerMatched matched);

    void onMatchData(MatchData matchData);

    void onMatchPresence(MatchPresenceEvent matchPresence);
}
