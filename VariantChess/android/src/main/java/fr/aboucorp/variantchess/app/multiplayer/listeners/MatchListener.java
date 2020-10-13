package fr.aboucorp.variantchess.app.multiplayer.listeners;

import com.heroiclabs.nakama.MatchPresenceEvent;

import fr.aboucorp.variantchess.app.utils.SignedData;

public interface MatchListener {
    void onMatchData(long opCode, SignedData signedData);

    void onMatchPresence(MatchPresenceEvent matchPresence);

}
