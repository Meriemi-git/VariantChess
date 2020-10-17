package fr.aboucorp.variantchess.app.managers;

import fr.aboucorp.variantchess.app.managers.boards.BoardLoadingListener;
import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.entities.ChessMatch;
import fr.aboucorp.variantchess.entities.PartyLifeCycle;
import fr.aboucorp.variantchess.entities.Player;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.GameEventSubscriber;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;

public abstract class MatchManager implements GameEventSubscriber, BoardLoadingListener, PartyLifeCycle {
    protected final BoardManager boardManager;
    protected final GameEventManager gameEventManager;
    protected ChessMatch chessMatch;
    protected Turn currentTurn;


    public MatchManager(BoardManager boardManager, GameEventManager gameEventManager) {
        this.gameEventManager = gameEventManager;
        this.boardManager = boardManager;
        this.boardManager.setBoardLoadingListener(this);
    }

    @Override
    public abstract void OnBoardLoaded();

    @Override
    public abstract void receiveEvent(GameEvent event);

    @Override
    public abstract void startParty(ChessMatch chessMatch);

    @Override
    public abstract void stopParty();

    public abstract void endTurn(MoveEvent event);

    public abstract void startTurn();

    public abstract String getPartyInfos();

    public abstract void passTurn();

    public abstract boolean isMyTurn();

    public abstract Player getOpponent();
}
