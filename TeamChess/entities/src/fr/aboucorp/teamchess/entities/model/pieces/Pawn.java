package fr.aboucorp.teamchess.entities.model.pieces;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.movesets.PawnMoveSet;

public class Pawn extends ChessPiece {

    private boolean isFirstMove = true;

    public Pawn(ChessCell cell, ChessColor chessColor, PieceId pieceId, Board board) {
        super(cell, chessColor,pieceId);
        this.moveSet = new PawnMoveSet(this,board);
    }

    @Override
    public void move(ChessCell cell) {
        super.move(cell);
        this.isFirstMove = false;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }

    public void setFirstMove(boolean firstMove) {
        isFirstMove = firstMove;
    }
}
