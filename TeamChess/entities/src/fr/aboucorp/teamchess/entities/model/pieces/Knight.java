package fr.aboucorp.teamchess.entities.model.pieces;


import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.boards.ClassicBoard;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.movesets.KnightMoveSet;

public class Knight extends Piece {

    public Knight(Square square, ChessColor chessColor, PieceId id, ClassicBoard classicBoard) {
        super(square, chessColor,id);
        this.moveSet = new KnightMoveSet(this, classicBoard);
    }

    @Override
    public char fen() {
        return this.getChessColor() == ChessColor.WHITE ? 'N' : 'n';
    }
}
