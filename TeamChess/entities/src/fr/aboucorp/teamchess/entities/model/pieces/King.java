package fr.aboucorp.teamchess.entities.model.pieces;

import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.movesets.KingMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.ChessList;


public class King extends ChessPiece {

    private KingMoveSet kingMoveSet;
    public King(ChessCell cell, ChessColor chessColor, PieceId pieceId) {
        super(cell, chessColor,pieceId);
        this.kingMoveSet = new KingMoveSet();
    }

    @Override
    public ChessList<ChessCell> getNextMoves(ChessPiece piece, Board board) {
        return  this.kingMoveSet.getMoves(piece, board);
    }
}
