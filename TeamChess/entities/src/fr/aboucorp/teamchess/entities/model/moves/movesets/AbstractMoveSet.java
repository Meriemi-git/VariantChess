package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public abstract class AbstractMoveSet {
    public abstract ChessCellList getPossibleMoves(ChessPiece piece, Board board, ChessColor turnColor);
    public abstract ChessCellList getThreats(ChessPiece piece, Board board, ChessColor turnColor);
}
