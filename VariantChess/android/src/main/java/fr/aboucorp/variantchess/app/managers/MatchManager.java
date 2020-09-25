package fr.aboucorp.variantchess.app.managers;

import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.enums.EventType;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.BoardEvent;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;
import fr.aboucorp.variantchess.entities.events.models.PartyEvent;

public class MatchManager implements GameEventSubscriber, BoardManager.BoardLoadingListener, PartyLifeCycle {
    protected final BoardManager boardManager;
    protected final TurnManager turnManager;
    protected GameEventManager eventManager;
    protected ChessMatch chessMatch;


    public MatchManager(BoardManager boardManager, GameEventManager gameEventManager) {
        this.eventManager = gameEventManager;
        this.boardManager = boardManager;
        this.boardManager.setBoardLoadingListener(this);
        this.turnManager = new TurnManager(gameEventManager);
    }

    @Override
    public void OnBoardLoaded() {
        this.eventManager.subscribe(GameEvent.class, this, 1);
        this.turnManager.startParty(this.chessMatch);
    }

    @Override
    public void receiveGameEvent(GameEvent event) {
        if (event instanceof BoardEvent && ((BoardEvent) event).boardEventType == EventType.CHECKMATE) {
            ChessColor winner = this.boardManager.getWinner();
            this.eventManager.sendMessage(new PartyEvent(String.format("Game finished ! Winner : %s", winner != null ? winner.name() : "EQUALITY"), EventType.END_GAME));
        } else if (event instanceof MoveEvent && ((BoardEvent) event).boardEventType == EventType.MOVE) {
            this.endTurn(((MoveEvent) event));
        }
    }

    @Override
    public void startParty(ChessMatch chessMatch) {
        this.chessMatch = chessMatch;
        this.boardManager.startParty(chessMatch);
    }

    @Override
    public void stopParty() {
        this.boardManager.stopParty();
    }


    public String getPartyInfos() {
        return this.turnManager.getTurnColor().name();
    }

    public void endTurn(MoveEvent event) {
        this.turnManager.endTurn(event, this.boardManager.getFenFromBoard());
        this.turnManager.startTurn();
    }

}
