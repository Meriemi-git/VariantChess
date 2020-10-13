package fr.aboucorp.variantchess.app.managers;

import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.events.models.GameEvent;
import fr.aboucorp.variantchess.entities.events.models.MoveEvent;

public class OfflineMatchManager extends MatchManager {
    public OfflineMatchManager(BoardManager boardManager, GameEventManager gameEventManager) {
        super(boardManager, gameEventManager);
    }

    @Override
    public void receiveEvent(GameEvent event) {

    }

    @Override
    public void endTurn(MoveEvent event) {

    }

    @Override
    public void startTurn() {

    }
}
