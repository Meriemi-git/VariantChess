package fr.aboucorp.variantchess.app.multiplayer;

import com.heroiclabs.nakama.AbstractSocketListener;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchmakerMatched;

public class MatchMakingSocketListener extends AbstractSocketListener {
    private final MatchListener listener;

    public MatchMakingSocketListener(MatchListener listener) {
        this.listener = listener;
    }

    @Override
    public void onError(Error error) {
        super.onError(error);
        this.listener.onError(error);
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        super.onMatchmakerMatched(matched);
        this.listener.onMatchmakerMatched(matched);
    }

    @Override
    public void onMatchData(MatchData matchData) {
        super.onMatchData(matchData);
        this.listener.onMatchData(matchData);
    }
}
