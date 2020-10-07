package fr.aboucorp.variantchess.app.multiplayer;

import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;

public interface MatchmakingListener {

    void onMatchmakerMatched(MatchmakerMatched matched);

    void onMatchPresence(MatchPresenceEvent matchPresence);

}
