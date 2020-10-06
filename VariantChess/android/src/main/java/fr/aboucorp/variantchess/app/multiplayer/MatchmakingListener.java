package fr.aboucorp.variantchess.app.multiplayer;

import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;

public interface MatchmakingListener {

    void onMatchmakerMatched(MatchmakerMatched matched);

    void onMatchData(MatchData matchData);

    void onMatchPresence(MatchPresenceEvent matchPresence);

}
