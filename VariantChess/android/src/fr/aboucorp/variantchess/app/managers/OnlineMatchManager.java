package fr.aboucorp.variantchess.app.managers;

import com.heroiclabs.nakama.MatchData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import fr.aboucorp.variantchess.app.db.entities.ChessUser;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.app.multiplayer.AbstractMatchListener;
import fr.aboucorp.variantchess.app.multiplayer.SessionManager;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;

public class OnlineMatchManager extends MatchManager {
    private SessionManager sessionManager;
    private ChessUser currentPlayer;

    public OnlineMatchManager(BoardManager boardManager, GameEventManager gameEventManager, SessionManager sessionManager, ChessUser currentPlayer) {
        super(boardManager, gameEventManager);
        this.sessionManager = sessionManager;
        this.sessionManager.setMatchListener(new ConcreteMatchListener());
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        super.receiveGameEvent(event);
        this.sessionManager.sendEvent(event, this.chessMatch.getMatchId());
    }

    public void playOppositeMove(TurnEndEvent event) {
        this.boardManager.playTheMove(event);
        this.turnManager.appendTurn(event.turn);
    }

    private class ConcreteMatchListener extends AbstractMatchListener {

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
    }
}
