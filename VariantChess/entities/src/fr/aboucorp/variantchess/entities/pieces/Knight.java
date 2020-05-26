package fr.aboucorp.variantchess.entities.pieces;


import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.enums.PieceId;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.moves.movesets.KnightMoveSet;

public class Knight extends Piece {

    public Knight(Square square, ChessColor chessColor, PieceId id, ClassicBoard classicBoard, GameEventManager gameEventManager) {
        super(square, chessColor, id);
        this.moveSet = new KnightMoveSet(this, classicBoard, gameEventManager);
    }

    @Override
    public char fen() {
        return this.getChessColor() == ChessColor.WHITE ? 'N' : 'n';
    }
}
