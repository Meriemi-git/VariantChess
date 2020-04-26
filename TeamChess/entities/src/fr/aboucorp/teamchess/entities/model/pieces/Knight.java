package fr.aboucorp.teamchess.entities.model.pieces;


import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.movesets.KnightMoveSet;

public class Knight extends Piece {

    public Knight(Square square, ChessColor chessColor, PieceId id, Board board) {
        super(square, chessColor,id);
        this.moveSet = new KnightMoveSet(this,board);
    }
}
