package fr.aboucorp.teamchess.entities.model.pieces;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.movesets.PawnMoveSet;

public class Pawn extends Piece {

    private boolean isFirstMove = true;

    public Pawn(Square cell, ChessColor chessColor, PieceId pieceId, Board board) {
        super(cell, chessColor,pieceId);
        this.moveSet = new PawnMoveSet(this,board);
    }

    @Override
    public void move(Square square) {
        super.move(square);
        this.isFirstMove = false;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public void setFirstMove(boolean firstMove) {
        isFirstMove = firstMove;
    }
}
