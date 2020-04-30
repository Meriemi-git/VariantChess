package fr.aboucorp.teamchess.entities.model.pieces;

import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.boards.ClassicBoard;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.movesets.PawnMoveSet;

public class Pawn extends Piece {

    private boolean isFirstMove = true;

    public Pawn(Square square, ChessColor chessColor, PieceId pieceId, ClassicBoard classicBoard) {
        super(square, chessColor,pieceId);
        this.moveSet = new PawnMoveSet(this, classicBoard);
    }

    @Override
    public void move(Square square) {
        super.move(square);
        this.isFirstMove = false;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    @Override
    public char fen() {
        return this.getChessColor() == ChessColor.WHITE ? 'P' : 'p';
    }
}
