package fr.aboucorp.teamchess.app.managers.boards;

import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Turn;
import fr.aboucorp.teamchess.entities.model.boards.Board;
import fr.aboucorp.teamchess.entities.model.events.GameEventManager;
import fr.aboucorp.teamchess.entities.model.rules.AbstracRuleSet;
import fr.aboucorp.teamchess.entities.model.utils.SquareList;
import fr.aboucorp.teamchess.libgdx.Board3dManager;

public abstract class AbstractBoardManager {
    protected final Board board;
    protected final Board3dManager board3dManager;
    protected Piece selectedPiece;
    protected GameEventManager eventManager;
    protected Turn previousTurn;
    protected Turn actualTurn;
    protected final AbstracRuleSet ruleSet;
    protected SquareList possiblesMoves;

    public AbstractBoardManager(Board board, Board3dManager board3dManager, AbstracRuleSet ruleSet) {
        this.board = board;
        this.board3dManager = board3dManager;
        this.ruleSet = ruleSet;
    }
}
