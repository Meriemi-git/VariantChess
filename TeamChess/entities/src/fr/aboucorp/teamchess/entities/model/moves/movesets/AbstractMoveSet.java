package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.utils.ChessList;

public abstract class AbstractMoveSet {
    public abstract ChessList<ChessCell> getMoves(ChessPiece piece, Board board);
}
