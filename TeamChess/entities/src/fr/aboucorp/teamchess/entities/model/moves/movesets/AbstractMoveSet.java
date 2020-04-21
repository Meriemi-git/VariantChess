package fr.aboucorp.teamchess.entities.model.moves.movesets;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.utils.ChessCellList;

public abstract class AbstractMoveSet {
    public abstract ChessCellList getMoves(ChessPiece piece, Board board, ChessColor turnColor);
}
