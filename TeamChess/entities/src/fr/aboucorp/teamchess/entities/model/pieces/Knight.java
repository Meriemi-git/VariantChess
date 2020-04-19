package fr.aboucorp.teamchess.entities.model.pieces;


import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.utils.ChessList;

public class Knight extends ChessPiece {


    public Knight(ChessCell cell, ChessColor chessColor, PieceId id) {
        super(cell, chessColor,id);
    }

    @Override
    public ChessList<ChessCell> getNextMoves(ChessPiece piece, Board board) {

        return null;
    }
}
