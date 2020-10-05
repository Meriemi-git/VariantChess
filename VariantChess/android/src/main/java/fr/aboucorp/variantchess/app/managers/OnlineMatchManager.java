package fr.aboucorp.variantchess.app.managers;

import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.api.ChannelMessage;

import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.multiplayer.MultiplayerListener;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;

public class OnlineMatchManager extends MatchManager implements MultiplayerListener {
    private SessionManager sessionManager;
    private ChessUser currentPlayer;

    public OnlineMatchManager(BoardManager boardManager, GameEventManager gameEventManager, SessionManager sessionManager, ChessUser currentPlayer) {
        super(boardManager, gameEventManager);
        this.sessionManager = sessionManager;
        this.currentPlayer = currentPlayer;
        this.sessionManager.setMultiplayerListener(this);
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        super.receiveGameEvent(event);
        if (event instanceof TurnEndEvent && this.currentPlayer.userId.equals(((TurnEndEvent) event).turn.getPlayer().getUserID())) {
            this.sessionManager.sendEvent(event, this.chessMatch.getMatchId());
        }
    }

    @Override
    public void startParty(ChessMatch chessMatch) {
        super.startParty(chessMatch);
        if (!chessMatch.whitePlayer.getUserID().equals(this.currentPlayer.userId)) {
            this.boardManager.waitForNextTurn();
        }
    }

    @Override
    public void endTurn(MoveEvent event) {
        this.turnManager.endTurn(event, this.boardManager.getFenFromBoard());
        this.boardManager.waitForNextTurn();
    }

    public void playOppositeMove(TurnEndEvent event) {
        this.boardManager.playTheMove(event);
        this.turnManager.appendTurn(event.turn);
        this.boardManager.stopWaitForNextTurn();
        this.turnManager.startTurn();
    }

    @Override
    public void onChannelMessage(ChannelMessage message) {

    }

    @Override
    public void onChannelPresence(ChannelPresenceEvent presence) {

    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {

    }

    @Override
    public void onMatchData(MatchData matchData) {

    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {

    }

/*    private class ConcreteMatchListener extends AbstractMatchListener {

        @Override
        public void onMatchData(MatchData matchData) {
            super.onMatchData(matchData);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new ByteArrayInputStream(matchData.getData()));
                GameEvent gameEvent = (GameEvent) ois.readObject();
                if (gameEvent instanceof TurnEndEvent) {
                    TurnEndEvent event = (TurnEndEvent) gameEvent;
                    if (!event.turn.getPlayer().getUserID().equals(OnlineMatchManager.this.currentPlayer.userId)) {
                        OnlineMatchManager.this.playOppositeMove(event);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }*/
}
