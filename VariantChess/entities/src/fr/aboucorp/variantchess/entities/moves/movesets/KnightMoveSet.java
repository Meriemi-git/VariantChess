package fr.aboucorp.variantchess.entities.moves.movesets;

import fr.aboucorp.variantchess.entities.ChessColor;
import fr.aboucorp.variantchess.entities.Location;
import fr.aboucorp.variantchess.entities.Piece;
import fr.aboucorp.variantchess.entities.Square;
import fr.aboucorp.variantchess.entities.boards.ClassicBoard;
import fr.aboucorp.variantchess.entities.events.GameEventManager;
import fr.aboucorp.variantchess.entities.moves.AbstractMoveSet;
import fr.aboucorp.variantchess.entities.utils.SquareList;

public class KnightMoveSet extends AbstractMoveSet {

    public KnightMoveSet(Piece thisPiece, ClassicBoard classicBoard, GameEventManager gameEventManager) {
        super(thisPiece, classicBoard, gameEventManager);
    }

    @Override
    protected SquareList getPossibleMoves(Piece piece, ChessColor turnColor) {
        SquareList validSquares = new SquareList();
        Location start = piece.getLocation();

        for (Square square : this.classicBoard.getSquares()) {
            Location end = square.getLocation();
            if (end.getX() == start.getX() + 2
                    && (end.getZ() == start.getZ() + 1
                    || end.getZ() == start.getZ() - 1)) {
                if (square.getPiece() == null || square.getPiece().getChessColor() != turnColor) {
                    validSquares.add(square);
                }
            }
            if (end.getX() == start.getX() - 2
                    && (end.getZ() == start.getZ() + 1
                    || end.getZ() == start.getZ() - 1)) {
                if (square.getPiece() == null || square.getPiece().getChessColor() != turnColor) {
                    validSquares.add(square);
                }
            }
            if (end.getZ() == start.getZ() + 2
                    && (end.getX() == start.getX() + 1
                    || end.getX() == start.getX() - 1)) {
                if (square.getPiece() == null || square.getPiece().getChessColor() != turnColor) {
                    validSquares.add(square);
                }
            }
            if (end.getZ() == start.getZ() - 2
                    && (end.getX() == start.getX() + 1
                    || end.getX() == start.getX() - 1)) {
                if (square.getPiece() == null || square.getPiece().getChessColor() != turnColor) {
                    validSquares.add(square);
                }
            }
        }
        return validSquares;
    }

    @Override
    public SquareList getThreats(Piece piece, ChessColor turnColor) {
        return this.getPossibleMoves(piece, turnColor);
    }
}
