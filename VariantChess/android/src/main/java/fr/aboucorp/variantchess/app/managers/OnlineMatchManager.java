package fr.aboucorp.variantchess.app.managers;


import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;

public class OnlineMatchManager extends MatchManager {
    private SessionManager sessionManager;
    private ChessUser currentPlayer;

    public OnlineMatchManager(BoardManager boardManager, GameEventManager gameEventManager, SessionManager sessionManager, ChessUser currentPlayer) {
        super(boardManager, gameEventManager);
        this.sessionManager = sessionManager;
        this.currentPlayer = currentPlayer;
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
