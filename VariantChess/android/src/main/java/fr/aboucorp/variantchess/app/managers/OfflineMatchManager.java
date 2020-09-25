package fr.aboucorp.variantchess.app.managers;

import fr.aboucorp.variantchess.app.managers.boards.BoardManager;
import fr.aboucorp.variantchess.entities.events.GameEventManager;

public class OfflineMatchManager extends MatchManager {
    public OfflineMatchManager(BoardManager boardManager, GameEventManager gameEventManager) {
        super(boardManager, gameEventManager);
    }
}
