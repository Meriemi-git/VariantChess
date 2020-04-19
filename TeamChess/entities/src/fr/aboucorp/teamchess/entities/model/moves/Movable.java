package fr.aboucorp.teamchess.entities.model.moves;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.utils.ChessList;

public interface Movable {

    void move(ChessCell cell);
    ChessList<ChessCell> getNextMoves(ChessPiece piece, Board board);
}
