package fr.aboucorp.teamchess.entities.model.pieces;


import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.movesets.BishopMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.ChessList;

public class Bishop extends ChessPiece {

    private BishopMoveSet moveSet;
    public Bishop(ChessCell cell, ChessColor chessColor, PieceId pieceId) {
        super(cell, chessColor,pieceId);
        this.moveSet = new BishopMoveSet();
    }


    @Override
    public ChessList<ChessCell> getNextMoves(ChessPiece piece, Board board) {
        return this.moveSet.getMoves(piece, board);
    }

}
