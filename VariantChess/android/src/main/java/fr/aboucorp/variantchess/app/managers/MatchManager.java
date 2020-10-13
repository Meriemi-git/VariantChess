package fr.aboucorp.variantchess.app.managers;

import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;

public abstract class MatchManager implements GameEventSubscriber, BoardManager.BoardLoadingListener, PartyLifeCycle {
    protected final BoardManager boardManager;
    protected GameEventManager gameEventManager;
    protected ChessMatch chessMatch;
    protected Turn currentTurn;


    public MatchManager(BoardManager boardManager, GameEventManager gameEventManager) {
        this.gameEventManager = gameEventManager;
        this.boardManager = boardManager;
        this.boardManager.setBoardLoadingListener(this);
    }

    @Override
    public void OnBoardLoaded() {
        this.gameEventManager.subscribe(GameEvent.class, this, 1);
        startTurn();
    }

    @Override
    public abstract void receiveEvent(GameEvent event);

    @Override
    public void startParty(ChessMatch chessMatch) {
        this.chessMatch = chessMatch;
        this.boardManager.startParty(chessMatch);
    }

    @Override
    public void stopParty() {
        this.boardManager.stopParty();
        this.chessMatch = null;
    }


    public String getPartyInfos() {
        return this.currentTurn.getTurnColor().name();
    }


    public abstract void endTurn(MoveEvent event);

    public abstract void startTurn();
}
