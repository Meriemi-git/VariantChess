package fr.aboucorp.teamchess.entities.model.pieces;


import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.movesets.QueenMoveSet;
import fr.aboucorp.teamchess.entities.model.utils.ChessList;

public class Queen extends ChessPiece  {

    private QueenMoveSet queenMoveSet;
    public Queen(ChessCell cell, ChessColor chessColor, PieceId pieceId) {
        super(cell, chessColor,pieceId);
        this.queenMoveSet = new QueenMoveSet();
    }

    @Override
    public ChessList<ChessCell> getNextMoves(ChessPiece piece, Board board) {
      return this.queenMoveSet.getMoves(piece, board);
    }
}
