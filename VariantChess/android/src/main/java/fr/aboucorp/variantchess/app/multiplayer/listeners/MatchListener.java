package fr.aboucorp.variantchess.app.multiplayer.listeners;

import com.heroiclabs.nakama.MatchData;

public interface MatchListener {
    void onMatchData(MatchData matchData);

}
