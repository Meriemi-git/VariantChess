package fr.aboucorp.variantchess.app.multiplayer.listeners;

import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;

public interface MatchListener {
    void onMatchData(MatchData matchData);

    void onMatchPresence(MatchPresenceEvent matchPresence);

}
