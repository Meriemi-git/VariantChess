package fr.aboucorp.teamchess.entities.model.pieces;


import fr.aboucorp.teamchess.entities.model.ChessCell;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.ChessPiece;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.movesets.KnightMoveSet;

public class Knight extends ChessPiece {

    public Knight(ChessCell cell, ChessColor chessColor, PieceId id) {
        super(cell, chessColor,id);
        this.moveSet = new KnightMoveSet();
    }
}
