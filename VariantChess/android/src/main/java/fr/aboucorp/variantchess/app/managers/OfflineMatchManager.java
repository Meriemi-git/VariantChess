package fr.aboucorp.variantchess.app.managers;

import android.util.Log;

import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.Player;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.enums.EventType;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.PartyEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnEndEvent;
import fr.aboucorp.variantchess.entities.events.models.TurnStartEvent;

public class OfflineMatchManager extends MatchManager {
    public OfflineMatchManager(BoardManager boardManager, GameEventManager gameEventManager) {
        super(boardManager, gameEventManager);
    }

    @Override
    public void OnBoardLoaded() {
        this.gameEventManager.subscribe(GameEvent.class, this, 1, "OfflineMatchManager => GameEvent");
        startTurn();
    }

    @Override
    public void receiveEvent(GameEvent event) {
        if (event instanceof BoardEvent && ((BoardEvent) event).boardEventType == EventType.CHECKMATE) {
            ChessColor winner = this.boardManager.getWinner();
            this.gameEventManager.sendEvent(new PartyEvent(String.format("Game finished ! Winner : %s", winner != null ? winner.name() : "EQUALITY"), EventType.END_GAME));
        } else if (event instanceof MoveEvent && ((BoardEvent) event).boardEventType == EventType.MOVE) {
            endTurn((MoveEvent) event);
        }
    }

    @Override
    public void startParty(ChessMatch chessMatch) {
        this.chessMatch = chessMatch;
        this.boardManager.startParty(chessMatch);
    }

    @Override
    public void stopParty() {

    }

    @Override
    public void endTurn(MoveEvent event) {
        this.currentTurn.setFen(boardManager.getFenFromBoard());
        if (event != null) {
            this.currentTurn.setPlayed(event.played);
            this.currentTurn.setTo(event.to);
            this.currentTurn.setFrom(event.from);
            this.currentTurn.setDeadPiece(event.deadPiece);
        }
        this.chessMatch.getTurns().add(this.currentTurn);
        this.gameEventManager.sendEvent(new TurnEndEvent("Ending turn", this.chessMatch.getTurns().getLast()));
        startTurn();
    }

    @Override
    public void startTurn() {
        Log.i("fr.aboucorp.variantchess", "StartTurn");
        Turn nextTurn;
        Player player = null;
        if (this.chessMatch.getTurns().size() > 0 && this.chessMatch.getTurns().getLast().getTurnColor() == ChessColor.WHITE) {
            player = chessMatch.getBlackPlayer();
        } else {
            player = chessMatch.getWhitePlayer();
        }
        nextTurn = new Turn(this.chessMatch.getTurns().size() + 1, player);
        this.currentTurn = nextTurn;
        String eventMessage = String.format("Turn %s, color : %s", nextTurn.getTurnNumber(), nextTurn.getTurnColor());
        this.gameEventManager.sendEvent(new TurnStartEvent(eventMessage, nextTurn));
    }

    @Override
    public String getPartyInfos() {
        return this.currentTurn.getTurnColor().name();
    }

    @Override
    public void passTurn() {

    }

    @Override
    public boolean isMyTurn() {
        return true;
    }
}
