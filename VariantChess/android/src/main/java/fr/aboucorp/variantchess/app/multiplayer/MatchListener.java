package fr.aboucorp.variantchess.app.multiplayer;

import com.heroiclabs.nakama.MatchData;

public interface MatchListener {
    void onMatchData(MatchData matchData);

}
