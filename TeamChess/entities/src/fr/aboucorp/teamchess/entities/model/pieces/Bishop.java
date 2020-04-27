package fr.aboucorp.teamchess.entities.model.pieces;


import fr.aboucorp.teamchess.entities.model.Board;
import fr.aboucorp.teamchess.entities.model.ChessColor;
import fr.aboucorp.teamchess.entities.model.Piece;
import fr.aboucorp.teamchess.entities.model.Square;
import fr.aboucorp.teamchess.entities.model.enums.PieceId;
import fr.aboucorp.teamchess.entities.model.moves.movesets.BishopMoveSet;

public class Bishop extends Piece {

    public Bishop(Square square, ChessColor chessColor, PieceId pieceId, Board board) {
        super(square, chessColor,pieceId);
        this.moveSet = new BishopMoveSet(this,board);
    }

    @Override
    public char fen() {
        return this.getChessColor() == ChessColor.WHITE ? 'B' : 'b';
    }
}
