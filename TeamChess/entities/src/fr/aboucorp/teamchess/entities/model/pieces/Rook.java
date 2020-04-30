package fr.aboucorp.teamchess.entities.model.pieces;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.boards.ClassicBoard;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.movesets.RookMoveSet;

public class Rook extends Piece {
    public Rook(Square square, ChessColor chessColor, PieceId pieceId, ClassicBoard classicBoard) {
        super(square, chessColor,pieceId);
        this.moveSet = new RookMoveSet(this, classicBoard);
    }

    @Override
    public char fen() {
        return this.getChessColor() == ChessColor.WHITE ? 'R' : 'r';
    }
}
