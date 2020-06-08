package fr.aboucorp.variantchess.app.multiplayer;

import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchmakerMatched;

public interface MatchListener {

    void onError(Error error);

    void onMatchmakerMatched(MatchmakerMatched matched);

    void onMatchData(MatchData matchData);

}
