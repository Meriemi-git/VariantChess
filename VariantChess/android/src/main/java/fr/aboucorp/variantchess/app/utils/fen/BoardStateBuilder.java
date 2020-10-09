package fr.aboucorp.variantchess.app.utils.fen;

import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Turn;
import fr.aboucorp.variantchess.entities.boards.Board;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.rules.AbstractRuleSet;

public abstract class BoardStateBuilder {
    protected Board board;
    protected AbstractRuleSet ruleSet;

    public BoardStateBuilder(Board board, AbstractRuleSet abstractRuleSet) {
        this.board = board;
        this.ruleSet = abstractRuleSet;
    }

    public abstract String getStateFromBoard(Turn actualTurn);

    public abstract String getFenFromBoard(Turn actualTurn);

    public abstract PieceId getPiecePlayedFromState(String fenState);

    public abstract Location getFrom(String fenState);

    public abstract Location getTo(String fenState);
}
