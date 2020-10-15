package fr.aboucorp.variantchess.app.multiplayer.listeners;

import com.heroiclabs.nakama.MatchPresenceEvent;

public interface MatchListener {
    void onMatchData(String matchId, long opCode, String matchData);

    void onMatchPresence(MatchPresenceEvent matchPresence);

}
